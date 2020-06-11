package com.chumfuchiu.code.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.google.common.io.Files
import groovy.xml.MarkupBuilder
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.PriorityBlockingQueue

class MicroPlugin implements Plugin<Project> {
    Logger mLogger = LoggerFactory.getLogger(MicroPlugin.getSimpleName())
    Project mProject
    File outDir
    MarkupBuilder markupBuilder
    PriorityBlockingQueue<File> allImageFiles = new PriorityBlockingQueue<>(16, new Comparator<File>() {
        @Override
        int compare(File e, File t1) {
            return t1.length() - e.length()
        }
    })

    @Override
    void apply(Project project) {
        mLogger.error("${MicroPlugin.class.getSimpleName()} Plugin has apply")
        mProject = project
        //该插件必须apply在application工程中
        if (!project.plugins.hasPlugin("com.android.application")) {
            return
        }
        //输出文件夹
        outDir = new File(mProject.getBuildDir(), "/img")
        if (outDir.exists()) {
            FileUtils.cleanDirectory(outDir)
        } else {
            outDir.mkdirs()
        }
        //xml报表
        File xmlFile = new File(outDir, "ImageSize.xml")
        markupBuilder = new MarkupBuilder(new PrintWriter(xmlFile))

        AppExtension appExtension = project.extensions.findByName("android")
        project.afterEvaluate {
            def copyTask = project.task("CopyImageTask")
            appExtension.applicationVariants.all { ApplicationVariant variant ->
                if (variant.buildType.name != "debug") {
                    return
                }
                copyTask.doLast {
                    mLogger.error("ApplicationVariant <${variant.class}>")
                    variant.allRawAndroidResources.files.each {
                        copyImageFile(it, 0)
                    }
                    markupBuilder.ImageSize {
                        allImageFiles.each {
                            markupBuilder.item(name: it.getName(), size: "${it.length() / 1024} kb")
                        }
                    }
                }
                Task mergeResTask = variant.mergeResourcesProvider.get()
                mergeResTask.dependsOn(copyTask)
            }
        }
    }

    private void copyImageFile(File file,int depth) {
        if (!file.exists()) {
            return
        }
        if (!file.isDirectory() && file.name.endsWith(".png")) {
            File destParentFile = new File(outDir, file.getParentFile().getName())
            if (!destParentFile.exists()) {
                destParentFile.mkdirs()
            }
            File img = new File(destParentFile, file.getName())
            img.createNewFile()
            allImageFiles.add(file)
            Files.copy(file, img)
        } else {
            file.listFiles().each {
                copyImageFile(it, ++depth)
            }
        }
    }
}