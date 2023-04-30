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

    void apply(Settings settings) {
        includeBuilds(settings, FILE_NAME)
    }

    void includeBuilds(Settings settings, String fileName) {
        def file = new File(fileName)
        if (!file.exists()) {
            return
        }

        new FileInputStream(fileName).withCloseable { is ->
            def yaml = new Yaml().load(is)
            def builds = yaml?.builds
            builds?.each { build ->
                if (build.enabled == null || build.enabled) {
                    logger.info('Including build, name: {} - path: {}', build.name, build.path)
                    settings.includeBuild(build.path)
                }
            }
        }
    }
}
