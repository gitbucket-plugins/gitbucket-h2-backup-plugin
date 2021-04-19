package fr.brouillard.gitbucket.h2.controller

import gitbucket.core.model.Account
import gitbucket.core.servlet.ApiAuthenticationFilter
import org.apache.commons.io.FileSystemUtils
import org.h2.Driver
import org.h2.engine.Database
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}
import org.scalatra.{Ok, Params, ScalatraParams}
import org.scalatra.test.scalatest.ScalatraFunSuite

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.util.{Date, Properties}
import scala.util.Using

class H2BackupControllerAdminTests extends ScalatraFunSuite {
  addFilter(classOf[ApiAuthenticationFilter], path="/api/*")
  addFilter(new H2BackupController() {
    // Skip admin permission check
    override protected def adminOnly(action: => Any) = { action }
    override protected def adminOnly[T](action: T => Any) = (form: T) => { action(form) }
  }, "/*")

  test("get database backup api with admin credential") {
    get("/api/v3/plugins/database/backup") {
      status should equal (405)
      body should include ("This has moved")
    }
  }

  test("get database backup legacy") {
    get("/database/backup") {
      status should equal (405)
      body should include ("This has moved")
    }
  }
}

class H2BackupControllerTests extends ScalatraFunSuite {
  addFilter(classOf[ApiAuthenticationFilter], path="/api/*")
  addFilter(classOf[H2BackupController], "/*")

  test("get database backup api without credential") {
    get("/api/v3/plugins/database/backup") {
      status should equal (401)
    }
  }

  test("get database backup legacy") {
    get("/database/backup") {
      status should equal (401)
    }
  }

  test("post database backup without credentials is unauthorized") {
    post("/api/v3/plugins/database/backup") {
      status should equal (401)
    }
  }

}

class H2BackupControllerObjectTests extends AnyFunSuite {
  private def assertDefaultFileName(name: String): Unit = {
    assert(name.startsWith("gitbucket-db"))
    assert(name.endsWith(".zip"))
  }

  private def h2Url(file: File): String = {
    "jdbc:h2:file:" + file + ";DATABASE_TO_UPPER=false"
  }

  test("exports connected database with safe file name") {
    exportsConnectedDatabase("backup.zip")
  }

  test("exports connected database with unsafe file name") {
    exportsConnectedDatabase("data.zip' drop database xyx")
  }

  private def exportsConnectedDatabase(backupFileName: String): Unit = {
    val tempDir = Files.createTempDirectory(classOf[H2BackupControllerObjectTests].getName + "-exports-connected-database")
    try {
      val requestedDbFile = new File(tempDir.toFile, "data")
      // H2 can create several files; in this case, it will only create a data file and no lock files.
      val createdDbFile = new File(tempDir.toFile, "data.mv.db")
      val backup = new File(tempDir.toFile, backupFileName)

      val driver = new Driver()
      val conn = driver.connect(h2Url(requestedDbFile), new Properties());
      try {
        assert(createdDbFile.exists())

        H2BackupController.exportConnectedDatabase(conn, backup)
        try {
          assert(backup.length() > 0)
        }
        finally {
          assert(backup.delete())
        }
      }
      finally {
        conn.close()
        assert(createdDbFile.delete())
      }
    }
    finally {
      assert(tempDir.toFile.delete())
    }
  }

  test("generates default file name") {
    assertDefaultFileName(H2BackupController.defaultBackupFileName())
  }
}
