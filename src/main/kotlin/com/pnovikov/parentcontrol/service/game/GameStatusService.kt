package com.pnovikov.parentcontrol.service.game

import jakarta.annotation.PostConstruct
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.abs

@Service
class GameStatusService(
    val resourceLoader: ResourceLoader
) {
    var fishingStatusTemplateImage: BufferedImage? = null
    var catchingStatusTemplateImage: BufferedImage? = null
    var brokenStatusTemplateImage: BufferedImage? = null

    val compareGoodPercent = 90
    val compareImageDelta = 20

    fun getCurrentStatus(image: BufferedImage): GameStatus {
        if (isFishingStatus(image)) {
            return GameStatus.FINISHING
        }

        if (isCatchingStatus(image)) {
            return GameStatus.CATCH
        }

        if (isBrokenStatus(image)) {
            return GameStatus.BROKEN
        }

        return GameStatus.NOTHING
    }

    private fun isFishingStatus(image: BufferedImage): Boolean {
        val x = 20 // начальная точка X
        val y = 21 // начальная точка Y
        val width = 385 // ширина
        val height = 32 // высота
        val croppedImage: BufferedImage = image.getSubimage(x, y, width, height)
        return isTheSameImages(fishingStatusTemplateImage!!, croppedImage)
    }

    private fun isCatchingStatus(image: BufferedImage): Boolean {
        val x = 1215 // начальная точка X
        val y = 880 // начальная точка Y
        val width = 130 // ширина
        val height = 25 // высота
        val croppedImage: BufferedImage = image.getSubimage(x, y, width, height)
        return isTheSameImages(catchingStatusTemplateImage!!, croppedImage)
    }

    private fun isBrokenStatus(image: BufferedImage): Boolean {
        val x = 22 // начальная точка X
        val y = 22 // начальная точка Y
        val width = 123 // ширина
        val height = 28 // высота
        val croppedImage: BufferedImage = image.getSubimage(x, y, width, height)
        return isTheSameImages(brokenStatusTemplateImage!!, croppedImage)
    }

    private fun isTheSameImages(
        image1: BufferedImage,
        image2: BufferedImage
    ): Boolean {
        if (image1.width != image2.width || image1.height != image2.height) {
            return false
        }

        var successCount = 0.0;
        var unSuccessCount = 0.0;
        for (y in 0 until image1.height) {
            for (x in 0 until image1.width) {
                val originalColor = Color(image1.getRGB(x, y))
                val compareColor = Color(image2.getRGB(x, y))

                if (isSameColor(originalColor, compareColor)) {
                    successCount++
                } else {
                    unSuccessCount++
                }
            }
        }

        val comparePercent = (successCount / (successCount + unSuccessCount)) * 100

        //println(">>ComparePercent: $comparePercent%")

        return comparePercent > compareGoodPercent
    }

    private fun isSameColor(originalColor: Color, compareColor: Color): Boolean {
        return abs(originalColor.red - compareColor.red) < compareImageDelta ||
                abs(originalColor.green - compareColor.green) < compareImageDelta ||
                abs(originalColor.blue - compareColor.blue) < compareImageDelta

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
        brokenStatusTemplateImage = ImageIO.read(
            resourceLoader.getResource("template/fishing_broken.png").file
        )
        println(">>Loading templates finished...")
    }
}