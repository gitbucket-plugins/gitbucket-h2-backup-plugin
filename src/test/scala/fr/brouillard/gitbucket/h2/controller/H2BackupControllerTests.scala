package fr.brouillard.gitbucket.h2.controller

import gitbucket.core.model.Account
import gitbucket.core.servlet.ApiAuthenticationFilter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.{convertToAnyShouldWrapper, equal}
import org.scalatra.{Ok, Params, ScalatraParams}
import org.scalatra.test.scalatest.ScalatraFunSuite

import java.io.File
import java.util.Date

class H2BackupControllerTests extends ScalatraFunSuite {
  addFilter(classOf[ApiAuthenticationFilter], path="/api/*")
  addFilter(classOf[H2BackupController], "/*")

  test("get database backup api") {
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

  private def buildAccount(isAdmin: Boolean) = {
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

  test("generates default file name") {
    assertDefaultFileName(H2BackupController.defaultBackupFileName())
  }

  test("post database backup with admin credentials is executed with default file name") {
    val account = buildAccount(true)
    val params: Params = new ScalatraParams(Map())

    var executed = false;

    val exportDatabase = (file: File) => {
      assert(!executed)
      assertDefaultFileName(file.getName)

      executed = true
    }

    val action = H2BackupController.doBackup(exportDatabase, Some(account), params)

    assert(executed)
    assert(action.status == 200)

    // Not JSON and not HTML
    assert(action.headers.get("Content-Type").contains("text/plain"))
  }

  test("post database backup with admin credentials is executed with specific file name") {
    val fileName = "foo.zip"
    val account = buildAccount(true)
    val params: Params = new ScalatraParams(Map("dest" -> Seq(fileName)))

    var executed = false;

    val exportDatabase = (file: File) => {
      assert(!executed)
      assert(file.getName.equals(fileName))

      executed = true
    }

    val action = H2BackupController.doBackup(exportDatabase, Some(account), params)

    assert(executed)
    assert(action.status == 200)

    // Not JSON and not HTML
    assert(action.headers.get("Content-Type").contains("text/plain"))
  }

  test("post database backup with unprivileged credentials is unauthorized") {
    val account = buildAccount(false)
    val params: Params = new ScalatraParams(Map())

    val exportDatabase = (file: File) => {
      fail()
    }

    val action = H2BackupController.doBackup(exportDatabase, Some(account), params)
    assert(action.status == 401)
  }

}
