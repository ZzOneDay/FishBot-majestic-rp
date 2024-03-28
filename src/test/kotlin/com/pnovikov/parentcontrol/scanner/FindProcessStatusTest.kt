package com.pnovikov.parentcontrol.scanner

import com.pnovikov.parentcontrol.GameStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import java.io.File
import javax.imageio.ImageIO

@SpringBootTest
class FindProcessStatusTest() {

    @Autowired
    private lateinit var findProcessStatus: FindProcessStatus

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @Test
    fun getFishingStatus() {
        val waitingStatus = GameStatus.WAITING
        val imageFile = resourceLoader.getResource("fish/hello38.png").file
        val image = ImageIO.read(imageFile)
        val status = findProcessStatus.getCurrentStatus(image)
        assertEquals(status, GameStatus.FINISHING)
    }

    @Test
    fun getCatchingStatus() {
        val waitingStatus = GameStatus.WAITING
        val imageFile = resourceLoader.getResource("nofish/hello42.png").file
        val image = ImageIO.read(imageFile)
        val status = findProcessStatus.getCurrentStatus(image)
        assertEquals(GameStatus.CATCH, status)
    }
}