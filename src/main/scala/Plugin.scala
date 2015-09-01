import javax.servlet.ServletContext

import fr.brouillard.gitbucket.h2.controller.H2BackupController
import gitbucket.core.plugin.PluginRegistry
import gitbucket.core.service.SystemSettingsService.SystemSettings
import gitbucket.core.util.Version

class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "h2-backup"

  override val pluginName: String = "H2 Database Backup Plugin"

  override val description: String = "Allows to export h2 database of gitbucket"

  override val versions: List[Version] = List(
    Version(1, 0)
  )

  override def javaScripts(registry: PluginRegistry, context: ServletContext, settings: SystemSettings): Seq[(String, String)] = {
    // Add Snippet link to the header
    val path = settings.baseUrl.getOrElse(context.getContextPath)
    Seq(
      ".*/admin/h2backup" -> s"""
        |$$('#system-admin-menu-container>li:last').after(
        |  $$('<li class="active"><a href="${path}/admin/h2backup">H2 Backup</a></li>')
        |);
      """.stripMargin,
      ".*/admin/(?!h2backup).*" -> s"""
        |$$('#system-admin-menu-container>li:last').after(
        |  $$('<li><a href="${path}/admin/h2backup">H2 Backup</a></li>')
        |);
      """.stripMargin
    )
  }

  override val controllers = Seq(
    "/admin/h2backup" -> new H2BackupController()
    , "/database/backup" -> new H2BackupController()
  )
}
