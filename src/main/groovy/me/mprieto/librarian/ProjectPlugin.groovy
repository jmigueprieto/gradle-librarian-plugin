package me.mprieto.librarian

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.yaml.snakeyaml.Yaml

class ProjectPlugin implements Plugin<Project> {

    private Logger logger = Logging.getLogger(ProjectPlugin.class)

    //TODO make this configurable?
    private static String FILE_NAME = 'gradle-properties.yaml'

    void apply(Project project) {
        setExtraProperties(project, FILE_NAME)
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
            logger.info('Adding prop {} : {} to {}', it.key, it.value, project.name)
            project.ext[it.key as String] = it.value
        }

        project.subprojects.forEach {
            // Extra props specific for a subproject. But, keep in mind that
            // subprojects can access extra properties on their parent projects.
            if (entry.subprojects && entry.subprojects[it.name]) {
                setExtraProperties(it, entry.subprojects[it.name])
            }
        }
    }

    void addExtraPropsTask(Project project) {
        project.task('extraProps') {
            doLast {
                project.ext.properties
                        .sort { a, b -> a.key <=> b.key }
                        .each {
                            def key = it.key as String
                            //TODO sensitive data shouldn't be shown or should be masked
                            if (key.startsWith('signing.') ||
                                    key.startsWith('org.gradle')
                                    || key.contains('password')
                                    || key.contains('secret')
                                    || key.contains('token')
                                    || key.contains('key')) {
                                return
                            }
                            println("${it.key} ${it.value}")
                        }
            }
        }

        project.subprojects.each {
            if (it.parent == project) {
                addExtraPropsTask(it)
            }
        }
    }
}
