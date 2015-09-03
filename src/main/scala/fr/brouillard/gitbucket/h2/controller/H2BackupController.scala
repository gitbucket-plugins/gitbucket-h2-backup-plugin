package fr.brouillard.gitbucket.h2.controller

import java.io.File

import gitbucket.core.controller.ControllerBase
import gitbucket.core.servlet.Database
import gitbucket.core.util.Directory._
import fr.brouillard.gitbucket.h2._
import org.scalatra.Ok
import org.slf4j.LoggerFactory
import jp.sf.amateras.scalatra.forms._

class H2BackupController extends ControllerBase {
  private val logger = LoggerFactory.getLogger(classOf[H2BackupController])

  case class BackupForm(destFile: String)

  private val backupForm = mapping(
    "dest" -> trim(label("Destination", text(required)))
  )(BackupForm.apply)

  def exportDatabase(exportFile: File): Unit = {
    val session = Database.getSession(request)
    val conn = session.conn

    logger.info("exporting database to {}", exportFile)

    conn.prepareStatement("BACKUP TO '" + exportFile + "'").execute();
  }

  def exportDatabase: Unit = {
    val exportFile = new File(GitBucketHome, "gitbucket-database-backup.zip")
    exportDatabase(exportFile);
  }

  get("/admin/h2backup") {
    html.export(flash.get("info"));
  }

  get("/database/backup") {
    val filePath:String = params.getOrElse("dest", new File(GitBucketHome, "gitbucket-database-backup.zip").toString)
    exportDatabase(new File(filePath))
    Ok("done")
  }

  post("/database/backup", backupForm) { form: BackupForm =>
    exportDatabase(new File(form.destFile))
    flash += "info" -> "H2 Database has been exported."
    redirect("/admin/h2backup")
  }
}
