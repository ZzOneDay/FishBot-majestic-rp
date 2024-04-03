package com.pnovikov.parentcontrol.configuration

import nu.pattern.OpenCV
import org.opencv.objdetect.CascadeClassifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ResourceLoader
import java.awt.AWTException
import java.awt.Robot

@Configuration
@ComponentScan("com.pnovikov.parentcontrol")
class AppConfig {

    init {
        OpenCV.loadLocally()
    }

    @Bean
    @Throws(AWTException::class)
    fun robot(): Robot {
        System.setProperty("java.awt.headless", "false")
        return Robot()
    }

    @Bean
    fun fishHookDetection(resourceLoader: ResourceLoader): CascadeClassifier {
        val hookDetector = CascadeClassifier()
        // template/cascade.xml was top
        val templateImagePath = resourceLoader.getResource("template/cascade3.xml").file.path
            ?: throw IllegalArgumentException("Cascade is not found")
        hookDetector.load(templateImagePath)
        return hookDetector
    }
}