package com.pnovikov.parentcontrol

import com.pnovikov.parentcontrol.input.ScreenInput
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class ParentControlApplication

fun main(args: Array<String>) {
    val application = runApplication<ParentControlApplication>(*args)
    val scanner = application.getBean(ApplicationScanner::class.java)
    scanner.scanning()

}
