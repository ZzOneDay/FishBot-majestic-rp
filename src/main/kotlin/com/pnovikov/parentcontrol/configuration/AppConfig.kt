package com.pnovikov.parentcontrol.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.awt.AWTException
import java.awt.Robot

@Configuration
@ComponentScan("com.pnovikov.parentcontrol")
class AppConfig {

    @Bean
    @Throws(AWTException::class)
    fun robot(): Robot {
        System.setProperty("java.awt.headless", "false")
        return Robot()
    }
}