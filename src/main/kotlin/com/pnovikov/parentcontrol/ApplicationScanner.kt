package com.pnovikov.parentcontrol

import com.pnovikov.parentcontrol.integration.input.ScreenInput
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

@Service
class ApplicationScanner(
    val screenInput: ScreenInput
) {

    fun scanning() {

        for (i in 1..1000) {
            Thread.sleep(1000)
            val image: BufferedImage = screenInput.getScreen()
            val file = File("C:\\Users\\azmin\\IdeaProjects\\ParentControl\\picture$i.png")
            saveImage(image, file, "PNG")
        }

    }

    private fun saveImage(image: BufferedImage, outputFile: File, format: String) {
        try {
            ImageIO.write(image, format, outputFile)
            println("Изображение успешно сохранено в ${outputFile.absolutePath}")
        } catch (e: Exception) {
            println("Ошибка при сохранении изображения: ${e.message}")
        }
    }

}