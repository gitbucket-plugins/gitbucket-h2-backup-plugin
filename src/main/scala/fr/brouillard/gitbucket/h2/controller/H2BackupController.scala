package fr.brouillard.gitbucket.h2.controller

import java.io.File
import java.util.Date

import fr.brouillard.gitbucket.h2._

import gitbucket.core.controller.ControllerBase
import gitbucket.core.util.AdminAuthenticator
import gitbucket.core.util.Directory._
import gitbucket.core.servlet.Database

import org.scalatra.Ok
import org.slf4j.LoggerFactory

import io.github.gitbucket.scalatra.forms._

class H2BackupController extends ControllerBase with AdminAuthenticator {
  private val logger = LoggerFactory.getLogger(classOf[H2BackupController])

  case class BackupForm(destFile: String)

  private val backupForm = mapping(
    "dest" -> trim(label("Destination", text(required)))
  )(BackupForm.apply)

  // private val defaultBackupFile:String = new File(GitBucketHome, "gitbucket-database-backup.zip").toString;

  def exportDatabase(exportFile: File): Unit = {
    val destFile = if (exportFile.isAbsolute()) exportFile else new File(GitBucketHome+"/backup", exportFile.toString)

    val session = Database.getSession(request)
    val conn = session.conn

    logger.info("exporting database to {}", destFile)

    conn.prepareStatement("BACKUP TO '" + destFile + "'").execute()
  }

  get("/admin/h2backup") (adminOnly {
    html.export(flash.get("info"), flash.get("dest").orElse(Some(defaultBackupFileName())))
  })

  get("/api/v3/plugins/database/backup") {
    context.loginAccount match {
      case Some(x) if(x.isAdmin) => doExport()
      case _ => org.scalatra.Unauthorized()
    }
  }

  get("/database/backup") {
    if (sys.props.get("secure.backup") exists (_ equalsIgnoreCase "true"))
      org.scalatra.TemporaryRedirect("/api/v3/plugins/database/backup?dest=" + params.getOrElse("dest", defaultBackupFileName()))
    else {
      doExport()
    }
  }

  private def doExport(): Unit = {
    val filePath:String = params.getOrElse("dest", defaultBackupFileName())
    exportDatabase(new File(filePath))
    Ok("done: " + filePath)
  }

  post("/database/backup", backupForm) { form: BackupForm =>
    exportDatabase(new File(form.destFile))
    val msg:String = "H2 Database has been exported to '"+form.destFile+"'."
    flash += "info" -> msg
    flash += "dest" -> form.destFile
    redirect("/admin/h2backup")
  }

  private def defaultBackupFileName(): String = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm")
    "gitbucket-db-" + format.format(new Date())+ ".zip"
  }
}
