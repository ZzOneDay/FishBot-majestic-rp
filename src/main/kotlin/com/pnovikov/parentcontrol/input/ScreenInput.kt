package com.pnovikov.parentcontrol.input

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.awt.Rectangle
import java.awt.Robot
import java.awt.Toolkit
import java.awt.image.BufferedImage

@Component
class ScreenInput {

    fun getScreen(): BufferedImage {
        val robot = Robot()
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val screenRectangle = Rectangle(screenSize)
        return robot.createScreenCapture(screenRectangle)
    }
}