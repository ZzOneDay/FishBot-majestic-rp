package com.pnovikov.parentcontrol.integration

import com.pnovikov.parentcontrol.service.game.GameStatus
import com.pnovikov.parentcontrol.service.game.GameStatusService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ResourceLoader
import javax.imageio.ImageIO

@SpringBootTest
class GameStatusServiceTest() {

    @Autowired
    private lateinit var findProcessStatus: GameStatusService

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @Test
    fun getFishingStatus() {
        val imageFile = resourceLoader.getResource("fish/hello38.png").file
        val image = ImageIO.read(imageFile)
        val status = findProcessStatus.getCurrentStatus(image)
        assertEquals(status, GameStatus.FINISHING)
    }

    @Test
    fun getCatchingStatus() {
        val imageFile = resourceLoader.getResource("nofish/hello42.png").file
        val image = ImageIO.read(imageFile)
        val status = findProcessStatus.getCurrentStatus(image)
        assertEquals(GameStatus.CATCH, status)
    }

    @Test
    fun getBrokenStatus() {
        val imageFile = resourceLoader.getResource("nofish/hello0.png").file
        val image = ImageIO.read(imageFile)
        val status = findProcessStatus.getCurrentStatus(image)
        assertEquals(GameStatus.BROKEN, status)
    }

    @Test
    fun getStopStatus() {
        val imageFile = resourceLoader.getResource("helloKISA341.png").file
        val image = ImageIO.read(imageFile)
        val status = findProcessStatus.getCurrentStatus(image)
        assertEquals(GameStatus.STOP, status)
    }
}