import fr.brouillard.gitbucket.h2.controller.H2BackupController
import io.github.gitbucket.solidbase.model.Version
import gitbucket.core.controller.Context
import gitbucket.core.plugin.Link


class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "h2-backup"

  override val pluginName: String = "H2 Database Backup Plugin"

  override val description: String = "Allows to export h2 database of gitbucket"

  override val versions: List[Version] = List(
    new Version("1.0.0"),
    new Version("1.2.0"),
    new Version("1.3.0"),
    new Version("1.4.0")
  )

  override val systemSettingMenus: Seq[(Context) => Option[Link]] = Seq(
    (ctx: Context) => Some(Link("h2-backup", "H2 Backup", "admin/h2backup"))
  )

  override val controllers = Seq(
    "/admin/h2backup" -> new H2BackupController()
    , "/database/backup" -> new H2BackupController()
  )
}
