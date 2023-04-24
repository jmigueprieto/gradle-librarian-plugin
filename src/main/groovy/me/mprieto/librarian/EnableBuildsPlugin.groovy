package me.mprieto.librarian

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

class EnableBuildsPlugin implements Plugin<Project> {

    void apply(Project project) {
        addTasks(project, 'builds.yaml')
    }

    void addTasks(Project project, String fileName) {
        project.task('enableBuild') {
            doLast {
                changeEnabledProperty(fileName, project.buildName as String, 'true')
            }
        }

        project.task('disableBuild') {
            doLast {
                changeEnabledProperty(fileName, project.buildName as String, 'false')
            }
        }
    }

    void changeEnabledProperty(String fileName, String buildName, String value) {
        def file = new File(fileName)
        if (!file.exists()) {
            throw new RuntimeException("file $fileName not found")
        }

        Map<Object, Object> data = new FileInputStream(fileName).withCloseable { is ->
            new Yaml().load(is) as Map<Object, Object>
        }

        List builds = data.builds as List
        def found = builds.find { it.name == buildName }
        if (found != null) {
            found.enabled = value
        } else {
            throw new RuntimeException("Build $buildName not found")
        }

        // Rewrite file
        def options = new DumperOptions()
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        def yaml = new Yaml(options)
        new File(fileName).write(yaml.dump(data))
    }
}
