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

In current version database backup file is written to `GITBUCKET_HOME/.gitbucket/gitbucket-database-backup.zip`.

## Release Notes

### 1.0

- introduce gitbucket-h2-backup-plugin
- allows to backup h2 database via a live dump
