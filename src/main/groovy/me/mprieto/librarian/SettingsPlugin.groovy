package me.mprieto.librarian

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.yaml.snakeyaml.Yaml

class SettingsPlugin implements Plugin<Settings> {

    private Logger logger = Logging.getLogger(SettingsPlugin.class)

    //TODO make this configurable?
    private static String FILE_NAME = 'gradle-settings.yaml'

    private static String FILE_SEPARATOR = System.getProperty("file.separator")

    void apply(Settings settings) {
        readSettings(settings, FILE_NAME)
    }

    void readSettings(Settings settings, String fileName) {
        def file = new File(fileName)
        if (!file.exists()) {
            settings.ext['librarianEnabled'] = false
            return
        }

        settings.ext['librarianEnabled'] = true
        new FileInputStream(fileName).withCloseable { is ->
            def yaml = new Yaml().load(is)
            def projects = yaml?.projects
            projects?.each { project ->
                if (project.enabled == null || project.enabled) {
                    String name = project.name
                    logger.info('Including project: {}', name)
                    if (project.recursive) {
                        includeRecursive(settings, name, [])
                    } else {
                        settings.include(name)
                    }
                }
            }

            def builds = yaml?.builds
            builds?.each { build ->
                if (build.enabled == null || build.enabled) {
                    logger.info('Including build, name: {} - path: {}', build.name, build.path)
                    settings.includeBuild(build.path)
                }
            }
        }
    }

    void includeRecursive(Settings settings, String name, List<String> parents) {
        def projectFQN = (parents.empty ? '' : (parents.join(':') + ':')) + name
        settings.include(projectFQN)

        def dir = new File(projectFQN.replace(':', FILE_SEPARATOR))
        dir.listFiles()
                .findAll {
                    it.isDirectory() && it.listFiles().find {it.name == 'build.gradle' } != null
                }
                .each {
                    includeRecursive(settings, it.name, parents + name)
                }
    }
}
