# gitbucket-h2-backup-plugin

This plugin enhances [takezoe/gitbucket](https://github.com/takezoe/gitbucket) by offering a way to backup/dump the h2 database of gitbucket.

It originates from [pull request #845](takezoe/gitbucket#845) and can be used to adress database backup described in [gitbucket backup documentation](https://github.com/takezoe/gitbucket/wiki/Backup)

## Features

### H2 Backup

This plugin allows to backup the underlying H2 database used by default by gitbucket.

## Usage

The plugin provides 2 ways to backup the H2 database:

- via an administration page and a backup button
- via an HTTP call to http://YOUR_GITBUCKET/database/backup

Default backup file points to `GITBUCKET_DATA/gitbucket-database-backup-YYYY-MM-DD-hhmm.zip` where:

- _GITBUCKET_DATA_ is on a standard gitbucket installation `~/.gitbucket`
- _YYYY-MM-DD-hhmm_ represents a date with hour

### UI usage

- goto the `System Administration` menu, section `H2 Backup`
- modify the file path if the default one doesn't fit your needs
- hit the `Export database` button

![backup ui](https://cloud.githubusercontent.com/assets/1119660/9659908/3d0afcd4-5253-11e5-8124-39f8a538f6c3.png)

### HTTP call usage

Using your preferred tool (curl, wget, ...) it is possible fire a backup of the H2 database.

The URL to call looks like `http://YOUR_GITBUCKET/database/backup`.

You can pass an optional argument `dest` that references the destination file path where the backup will be done on the server. For example calling `http://YOUR_GITBUCKET/database/backup?dest=/var/backups/gitbucket.zip` will do an H2 backup of the gitbucket database into the file `/var/backups/gitbucket.zip`.
Since `1.3.0`, the _dest_ parameter can denote a relative file path, in this case the file will be created relatively to `GITBUCKET_DATA`.

On success, you will receive a `HTTP 200` answer with a body containing `done: FULL_PATH_OF_BACKUP_FILE`.

## Compatibility

Plugin version | GitBucket version
:--------------|:-----------------
1.4.x          | >= 4.10.y
1.3.x          | >= 4.3.y
1.2.x          | 4.x.y
1.1.x          | 3.11.x
1.0.x          | 3.6.x, 3.7.x, 3.8.x,, 3.9.x, 3.10.x

## Build

```
sbt clean package
```

## Installation

- copy gitbucket-h2-backup.jar into GITBUCKET_HOME/plugins
- restart gitbucket

## Release Notes

### 1.4.0

- compatibility with gitbucket 4.10, scala 2.12 [#20](https://github.com/gitbucket-plugins/gitbucket-h2-backup-plugin/issues/20)
- allow to secure `database/backup` endpoint [#1](https://github.com/gitbucket-plugins/gitbucket-h2-backup-plugin/issues/1),[#19](https://github.com/gitbucket-plugins/gitbucket-h2-backup-plugin/issues/19)  
   see [Securing backup endpoint](#securing-backup-endpoint) paragraph

### 1.3.0

- compatibility with gitbucket 4.3.0
- fix version order in gitbucket Plugins panel
- default backup file contains date with timestamp [#12](https://github.com/gitbucket-plugins/gitbucket-h2-backup-plugin/issues/12)

### 1.2.0

- binary compatibility with gitbucket 4.x

### 1.1

- recompilation to ensure binary compatibility with gitbucket 3.11

### 1.0

- introduce gitbucket-h2-backup-plugin
- allows to backup h2 database via a live dump

## Securing backup endpoint

In version 1.4.0, it is possible to secure the `database/backup` endpoint:

- launch gitbucket with System property _secure.backup_ set to true (for example `-Dsecure.backup=true` on the command line)
- due to actual limitations of gibucket & plugins security, once the previous setting is activated, 
a call to `http://YOUR_GITBUCKET/database/backup` will be temporary redirected `http://YOUR_GITBUCKET/api/v3/plugins/database/backup`. 
You have to follow this temporary redirection.
   - if you call the endpoint using _httpie_, use the `--follow` parameter
- this secured endpoint route is TEMPORARY you should not call it directly. 
If you do think that it will change in the future when gitbucket will support secured routes for plugins.  