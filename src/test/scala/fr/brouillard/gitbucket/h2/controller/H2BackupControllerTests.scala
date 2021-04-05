package fr.brouillard.gitbucket.h2.controller

import gitbucket.core.servlet.ApiAuthenticationFilter
import org.scalatra.test.scalatest.ScalatraFunSuite

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
