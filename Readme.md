FilmChecker
===========

A simple android app to check the status of film orders. Currently only Rossmann (only in Germany) and DM (Germany and Austria) are supported.

[![Available on F-Droid](https://f-droid.org/wiki/images/c/ca/F-Droid-button_available-on_smaller.png)](https://f-droid.org/repository/browse/?fdid=me.murks.filmchecker)

Pushing Updates
---------------
F-Droid is
[configured](https://gitlab.com/fdroid/fdroiddata/blob/master/metadata/me.murks.filmchecker.txt) to automatically scan this repo and publish new versions.
To release a new version the version string and number has to be incremented in
the app/build.gradle file and a tag consisting of the verionName.0 has to be
created in the master branch. E.g. to publish a new version with the versionName
2.1.1 a tag named 2.1.1.0 needs to exist.

License
-------
FilmChecker is published under the MIT License, see the license file.
