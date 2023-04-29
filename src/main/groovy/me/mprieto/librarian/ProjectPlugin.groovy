package me.mprieto.librarian

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.yaml.snakeyaml.Yaml

class ProjectPlugin implements Plugin<Project> {

    private Logger logger = Logging.getLogger(ProjectPlugin.class)

    void apply(Project project) {
        setExtraProperties(project, "gradle-properties.yaml")
        addExtraPropsTask(project)
    }

    void setExtraProperties(Project project, String fileName) {
        def file = new File(fileName)
        if (!file.exists()) {
            return
        }

        Map<Object, Object> data = new FileInputStream(fileName).withCloseable { is ->
             new Yaml().load(is) as Map<Object, Object>
        }

        setExtraProperties(project, data)
    }

    void setExtraProperties(Project project, entry) {
        entry.ext.each {
            logger.info("Adding prop ${it.key} ${it.value} to ${project.name}")
            project.ext[it.key as String] = it.value
        }

        project.subprojects.forEach {
            // Extra props specific for a subproject. But, keep in mind that
            // subprojects can access extra properties on their parent projects.
            setExtraProperties(it, entry.subprojects[it.name])
        }
    }

    void addExtraPropsTask(Project project) {
        project.task('extraProps') {
            doLast {
                project.ext.properties
                        .sort { a, b -> a.key <=> b.key }
                        .each {
                            def key = it.key as String
                            if (key.startsWith('signing.') ||
                                    key.startsWith('org.gradle')
                                    || key.contains('password')
                                    || key.contains('secret')
                                    || key.contains('token')) {
                                return
                            }
                            println("${it.key} ${it.value}")
                        }
            }
        }

        project.subprojects.each { addExtraPropsTask(it) }
    }
}
