package me.mprieto.librarian

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings

import org.yaml.snakeyaml.Yaml

class LibrarianPlugin implements Plugin<Project> {

    void apply(Project project) {
        def yaml = new Yaml()
        def inputStream = new FileInputStream(new File("project.yaml"))
        def data = yaml.load(inputStream)
        println data.projects
        println data.builds
        Settings settings = null

        settings.getRootProject().getChildren().forEach(p -> {

            p.getProperties().put()

        })
        // Project path
        // settings.includeBuild()
    }
}