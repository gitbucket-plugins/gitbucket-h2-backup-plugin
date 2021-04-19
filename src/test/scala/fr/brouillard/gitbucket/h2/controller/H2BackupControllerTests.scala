package fr.brouillard.gitbucket.h2.controller

import gitbucket.core.controller.Context
import gitbucket.core.model.Account
import gitbucket.core.servlet.ApiAuthenticationFilter
import org.apache.commons.io.FileSystemUtils
import org.h2.Driver
import org.h2.engine.Database
import org.mockito.Mockito._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}
import org.scalatra.{Ok, Params, ScalatraParams}
import org.scalatra.test.scalatest.ScalatraFunSuite

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.util.{Date, Properties}
import scala.util.Using

import H2BackupControllerTests._
import gitbucket.core.service.SystemSettingsService

class H2BackupControllerWithAdminTests extends ScalatraFunSuite {
  addFilter(classOf[ApiAuthenticationFilter], path="/api/*")
  addFilter(new H2BackupController() {
    override implicit val context = buildContext(isAdmin = true)
  }, "/*")

  test("get database backup api with admin") {
    get("/api/v3/plugins/database/backup") {
      status should equal (405)
      body should include ("This has moved")
    }
  }

  test("get database backup legacy with admin") {
    get("/database/backup") {
      status should equal (405)
      body should include ("This has moved")
    }
  }
}

class H2BackupControllerWithNonAdminTests extends ScalatraFunSuite {
  addFilter(classOf[ApiAuthenticationFilter], path="/api/*")
  addFilter(new H2BackupController() {
    override implicit val context = buildContext(isAdmin = false)
  }, "/*")

  test("get database backup api with non-admin") {
    get("/api/v3/plugins/database/backup") {
      status should equal (401)
    }
  }

  test("get database backup legacy with non-admin") {
    get("/database/backup") {
      status should equal (401)
    }
  }

  test("post database backup with non-admin") {
    post("/api/v3/plugins/database/backup") {
      status should equal (401)
    }
  }
}

class H2BackupControllerWithoutLoginTests extends ScalatraFunSuite {
  addFilter(classOf[ApiAuthenticationFilter], path="/api/*")
  addFilter(classOf[H2BackupController], "/*")

  test("get database backup api without login") {
    get("/api/v3/plugins/database/backup") {
      status should equal (401)
    }
  }

  test("get database backup legacy without login") {
    get("/database/backup") {
      status should equal (401)
    }
  }

  test("post database backup without login") {
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

object H2BackupControllerTests {
  val systemSetting = mock(classOf[SystemSettingsService.SystemSettings])
  when(systemSetting.sshAddress).thenReturn(None)

  def buildContext(isAdmin: Boolean) = {
    val context = mock(classOf[Context])
    when(context.baseUrl).thenReturn("http://localhost:8080")
    when(context.loginAccount).thenReturn(Some(buildAccount(isAdmin)))
    when(context.settings).thenReturn(systemSetting)
    context
  }

  def buildAccount(isAdmin: Boolean) = {
    Account(
      userName = "a",
      fullName = "b",
      mailAddress = "c",
      password = "d",
      isAdmin = isAdmin,
      url = None,
      registeredDate = new Date(),
      updatedDate = new Date(),
      lastLoginDate = None,
      image = None,
      isGroupAccount = false,
      isRemoved = false,
      description = None)
  }
}
