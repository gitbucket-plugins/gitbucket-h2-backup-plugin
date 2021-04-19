package fr.brouillard.gitbucket.h2.controller

import java.io.File
import java.util.Date
import fr.brouillard.gitbucket.h2._
import fr.brouillard.gitbucket.h2.controller.H2BackupController.{defaultBackupFileName, exportConnectedDatabase}
import gitbucket.core.controller.ControllerBase
import gitbucket.core.model.Account
import gitbucket.core.util.AdminAuthenticator
import gitbucket.core.util.Directory._
import gitbucket.core.servlet.Database
import org.scalatra.{ActionResult, Ok, Params}
import org.slf4j.LoggerFactory
import org.scalatra.forms._

import java.sql.Connection
import scala.util.Using

object H2BackupController {

  private val logger = LoggerFactory.getLogger(classOf[H2BackupController])

  def defaultBackupFileName(): String = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm")
    "gitbucket-db-" + format.format(new Date()) + ".zip"
  }

  def exportConnectedDatabase(conn: Connection, exportFile: File): Unit = {
    val destFile = if (exportFile.isAbsolute) exportFile else new File(GitBucketHome + "/backup", exportFile.toString)

    logger.info("Exporting database to {}", destFile)

    Using.resource(conn.prepareStatement("BACKUP TO ?")){ statement =>
      statement.setString(1, destFile.toString)
      statement.execute()
    }

    logger.info("Exported {} bytes.", exportFile.length())
  }

}

class H2BackupController extends ControllerBase with AdminAuthenticator {

  case class BackupForm(destFile: String)

  private val backupForm = mapping(
    "dest" -> trim(label("Destination", text(required)))
  )(BackupForm.apply)

  private def exportDatabase(exportFile: File): Unit = {
    exportConnectedDatabase(Database.getSession(request).conn, exportFile)
  }

  private def doBackupMoved(): ActionResult = {
    org.scalatra.MethodNotAllowed("This has moved to POST /api/v3/plugins/database/backup")
  }

  get("/admin/h2backup")(adminOnly {
    html.export(flash.get("info"), flash.get("dest").orElse(Some(defaultBackupFileName())))
  })

  // Migrated to POST method. This action will be removed in the future version.
  get("/api/v3/plugins/database/backup")(adminOnly {
    doBackupMoved()
  })

  post("/api/v3/plugins/database/backup")(adminOnly {
    val filePath = params.getOrElse("dest", defaultBackupFileName())
    exportDatabase(new File(filePath))
    Ok(filePath, Map("Content-Type" -> "text/plain"))
  })

  // Legacy api that was insecure/open by default. This action will be removed in the future version.
  get("/database/backup")(adminOnly {
    doBackupMoved()
  })

  // Responds to a form post from a web page
  post("/database/backup", backupForm)(adminOnly { form: BackupForm =>
    exportDatabase(new File(form.destFile))
    val msg: String = "H2 Database has been exported to '" + form.destFile + "'."
    flash.update("info", msg)
    flash.update("dest", form.destFile)
    redirect("/admin/h2backup")
  })

}
