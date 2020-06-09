package com.chumfuchiu.code.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MicroPlugin implements Plugin<Project> {
    Logger mLogger = LoggerFactory.getLogger(MicroPlugin.getSimpleName())

    @Override
    void apply(Project project) {
        mLogger.error("${MicroPlugin.class.getSimpleName()} Plugin has apply")
    }
}