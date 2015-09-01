package fr.brouillard.gitbucket.h2.controller

import java.io.File

import gitbucket.core.controller.ControllerBase
import gitbucket.core.servlet.Database
import gitbucket.core.util.Directory._
import fr.brouillard.gitbucket.h2._
import org.scalatra.Ok
import org.slf4j.LoggerFactory

class H2BackupController extends ControllerBase {
  private val logger = LoggerFactory.getLogger(classOf[H2BackupController])

  def exportDatabase: Unit = {
    val session = Database.getSession(request)
    val conn = session.conn
    val exportFile = new File(GitBucketHome, "gitbucket-database-backup.zip")

    logger.info("exporting database to {}", exportFile)

    conn.prepareStatement("BACKUP TO '" + exportFile + "'").execute();
  }

  get("/admin/h2backup") {
    html.export(flash.get("info"));
  }

  get("/database/backup") {
    exportDatabase
    Ok("done")
  }

  post("/database/backup") {
    exportDatabase
    flash += "info" -> "H2 Database has been exported."
    redirect("/admin/h2backup")
  }
}
