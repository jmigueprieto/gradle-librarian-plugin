package me.mprieto.librarian

import org.gradle.api.Project
import org.gradle.api.Plugin

class Versions {
    String common = '2.1.3'
}

class LibrarianPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.extensions.create("versions", Versions)
        project.extensions.add("commonVersion", "1.2.3")

//        project.task('hello') {
//            doLast {
//                println 'Hello from the Librarian Plugin'
//            }
//        }
    }
}