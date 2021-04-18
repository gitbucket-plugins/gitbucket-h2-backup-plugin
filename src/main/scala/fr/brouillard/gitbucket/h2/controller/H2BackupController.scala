package fr.brouillard.gitbucket.h2.controller

import java.io.File
import java.util.Date
import fr.brouillard.gitbucket.h2._
import fr.brouillard.gitbucket.h2.controller.H2BackupController.{defaultBackupFileName, doBackup}
import gitbucket.core.controller.ControllerBase
import gitbucket.core.model.Account
import gitbucket.core.util.AdminAuthenticator
import gitbucket.core.util.Directory._
import gitbucket.core.servlet.Database
import org.scalatra.{ActionResult, Ok, Params}
import org.slf4j.LoggerFactory
import org.scalatra.forms._

object H2BackupController {
  def defaultBackupFileName(): String = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm")
    "gitbucket-db-" + format.format(new Date()) + ".zip"
  }

  def doBackup(exportDatabase: File => Unit, loginAccount: Option[Account], params: Params): ActionResult = {
    loginAccount match {
      case Some(x) if x.isAdmin =>
        val filePath: String = params.getOrElse("dest", defaultBackupFileName())
        exportDatabase(new File(filePath))
        Ok(filePath, Map("Content-Type" -> "text/plain"))
      case _ => org.scalatra.Unauthorized()
    }
  }
}

class H2BackupController extends ControllerBase with AdminAuthenticator {
  private val logger = LoggerFactory.getLogger(classOf[H2BackupController])

  case class BackupForm(destFile: String)

  private val backupForm = mapping(
    "dest" -> trim(label("Destination", text(required)))
  )(BackupForm.apply)

  // private val defaultBackupFile:String = new File(GitBucketHome, "gitbucket-database-backup.zip").toString;

  def exportDatabase(exportFile: File): Unit = {
    val destFile = if (exportFile.isAbsolute) exportFile else new File(GitBucketHome + "/backup", exportFile.toString)

    val session = Database.getSession(request)
    val conn = session.conn

    logger.info("exporting database to {}", destFile)

    conn.prepareStatement("BACKUP TO '" + destFile + "'").execute()
  }

  get("/admin/h2backup")(adminOnly {
    html.export(flash.get("info"), flash.get("dest").orElse(Some(defaultBackupFileName())))
  })

  get("/api/v3/plugins/database/backup") {
    doBackupMoved()
  }

  post("/api/v3/plugins/database/backup") {
    doBackup(exportDatabase, context.loginAccount, params)
  }

  // Legacy api that was insecure/open by default
  get("/database/backup") {
    doBackupMoved()
  }

  private def doBackupMoved(): ActionResult = {
    org.scalatra.MethodNotAllowed("This has moved to POST /api/v3/plugins/database/backup")
  }

  // Responds to a form post from a web page
  post("/database/backup", backupForm) { form: BackupForm =>
    exportDatabase(new File(form.destFile))
    val msg: String = "H2 Database has been exported to '" + form.destFile + "'."
    flash.update("info", msg)
    flash.update("dest", form.destFile)
    redirect("/admin/h2backup")
  }

}
