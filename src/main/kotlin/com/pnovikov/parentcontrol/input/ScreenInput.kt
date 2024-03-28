package com.pnovikov.parentcontrol.input

import org.springframework.stereotype.Component
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage

@Component
class ScreenInput(
    val robot: Robot
) {

    fun getScreen(): BufferedImage {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val screenRectangle = Rectangle(screenSize)
        return robot.createScreenCapture(screenRectangle)
    }
}