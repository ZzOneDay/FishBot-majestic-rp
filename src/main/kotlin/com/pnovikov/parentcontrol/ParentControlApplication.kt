package com.pnovikov.parentcontrol

import com.pnovikov.parentcontrol.input.ScreenInput
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import java.util.Scanner

@SpringBootApplication
class ParentControlApplication

fun main(args: Array<String>) {
    val application = runApplication<ParentControlApplication>(*args)
    val runner = application.getBean(RunApplication::class.java)
    val scanner = Scanner(System.`in`)

    while (true) {
        val readLine = scanner.nextLine()
        if (readLine.equals("START", true)) {
            println("Starting...")
            val thread = Thread {
                runner.run()
            }
            thread.start()
        }

        if (readLine.equals("STOP", true)) {
            println("Stopping...")
            runner.stop()
        }
    }
}
