package com.github.daisuzz.sampleintellijplatformplugintemplate.services

import com.intellij.openapi.project.Project
import com.github.daisuzz.sampleintellijplatformplugintemplate.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
