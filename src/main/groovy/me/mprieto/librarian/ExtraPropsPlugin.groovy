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
    }

    void setExtraProperties(Project project, String fileName) {
        def file = new File(fileName)
        if (!file.exists()) {
            return
        }

        new FileInputStream(fileName).withCloseable { is ->
            Map<Object, Object> extraDef = new Yaml().load(is)
            setExtraProperties(project, extraDef)
        }
    }


    void setExtraProperties(Project project, entry) {
        entry.ext.each {
            project.extensions.add(it.key as String, it.value)
        }

        project.subprojects.forEach(subproject -> {
            setExtraProperties(subproject, entry.subprojects[subproject.name])
        })
    }
}
