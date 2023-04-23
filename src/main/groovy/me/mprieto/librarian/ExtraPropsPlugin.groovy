package me.mprieto.librarian

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.yaml.snakeyaml.Yaml

class ExtraPropsPlugin implements Plugin<Project> {

    private Logger logger = Logging.getLogger(ExtraPropsPlugin.class)

    void apply(Project project) {
        setExtraProperties(project, "extra-properties.yaml")
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

        project.subprojects.forEach(subproject -> {
            setExtraProperties(subproject, entry.subprojects[subproject.name])
        })
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
