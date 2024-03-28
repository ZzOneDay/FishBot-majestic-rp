package com.pnovikov.parentcontrol.scanner

import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs

@Service
class ImageService {

    val goodPercent = 90

    fun isTheSameImages(
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

                if (isSameColor(originalColor, compareColor, 20)) {
                    successCount++
                } else {
                    unSuccessCount++
                }
            }
        }

        val comparePercent = (successCount / (successCount + unSuccessCount)) * 100
        println(">>ComparePercent: $comparePercent%")

        return comparePercent > goodPercent
    }

    fun isSameColor(originalColor: Color, compareColor: Color, delta: Int): Boolean {
        return abs(originalColor.red - compareColor.red) < delta ||
                abs(originalColor.green - compareColor.green) < delta ||
                abs(originalColor.blue - compareColor.blue) < delta

    }
}