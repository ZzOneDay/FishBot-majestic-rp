package com.pnovikov.parentcontrol.scanner

import com.pnovikov.parentcontrol.GameStatus
import jakarta.annotation.PostConstruct
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO




@Service
class FindProcessStatus(
    val imageService: ImageService,
    val resourceLoader: ResourceLoader
) {
    var fishingStatusTemplateImage: BufferedImage? = null
    var catchingStatusTemplateImage: BufferedImage? = null

    fun getCurrentStatus(image: BufferedImage): GameStatus {
        // if catch 'can left/right' status fishing
        if (isFishingStatus(image)) {
            return GameStatus.FINISHING
        }

        if (isCatchingStatus(image)) {
            return GameStatus.CATCH
        }

        // if after 'click mouse' nothing -> throw

        // nothing - nothing
        return GameStatus.WAITING
    }

    private fun isFishingStatus(image: BufferedImage): Boolean {
        val x = 20 // начальная точка X
        val y = 21 // начальная точка Y
        val width = 385 // ширина
        val height = 32 // высота
        val croppedImage: BufferedImage = image.getSubimage(x, y, width, height)
        return imageService.isTheSameImages(fishingStatusTemplateImage!!, croppedImage)
    }

    private fun isCatchingStatus(image: BufferedImage): Boolean {
        val x = 1215 // начальная точка X
        val y = 880 // начальная точка Y
        val width = 130 // ширина
        val height = 25 // высота
        val croppedImage: BufferedImage = image.getSubimage(x, y, width, height)
        return imageService.isTheSameImages(catchingStatusTemplateImage!!, croppedImage)
    }

    @PostConstruct
    fun loadImages() {
        println(">>Loading templates started...")
        fishingStatusTemplateImage = ImageIO.read(
            resourceLoader.getResource("template/fishing_status.png").file
        )
        catchingStatusTemplateImage = ImageIO.read(
            resourceLoader.getResource("template/fishing_catch.png").file
        )
        println(">>Loading templates finished...")
    }
}