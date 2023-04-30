# Librarian 

This project contains a set of Gradle plugins that try to make the development flow smoother and help in managing the 
configuration of large multi-module projects that use dependencies which are in active development and frequently changing.

## Settings plugin

```
id: me.mprieto.librarian.settings
```

A settings plugin that allows you to handle included builds via a configuration yaml `gradle-settings.yaml`.

In the root of project you can have a `gradle-settings.yaml` like this:

```yaml
builds:
  - name: My Library
    path: /path/to/my-library
    enabled: true
  - name: Another library
    path: /path/to/another-library
    enabled: false
```

`enabled` is by default `true` and can be omitted but if you want gradle to resolve the dependency rather than using
the included build you can set it to `false`.

# Project plugin

```
id: me.mprieto.librarian.project
```

This plugin is a Work In Progress. At the moment it gives you the ability to externalize the **ext props**
to a yaml file `gradle-properties.yaml`

E.g.:

```yaml
ext:
  myLibraryVersion: 1.2.3
  anotherLibrary: 2.0.0
subprojects:
  submodule-1:
    ext:
      # this submodule uses a specific version of myLibrary
      myLibraryVersion: 1.5.3
  submodule-2:
    ext:
      # this submodule uses a specific version of anotherLibrary
      anotherLibrary: 2.2.4
```

Let's assume that in your `build.gradle` files you have dependencies declared like this:

```
dependencies {
    implementation "me.mprieto:my-library:{myLibraryVersion}"
    implementation "me.mprieto:another-library:{anotherLibrary}"
}
```

The root project may define these dependencies for all subprojects, but in some subprojects you may want to
override them or maybe you just want to provide a default version and let each subproject declare its dependencies.

You can obviously achieve this by simply using the `ext` block

```
ext {
    myLibraryVersion = '1.5.3'
}
```

but with this `yaml` file you get a centralized view of which version each project is using and it helps in keeping
things a bit more tidy and avoid changing the `build.gradle` files if these properties are changing frequently.
