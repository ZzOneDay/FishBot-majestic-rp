package com.pnovikov.parentcontrol.scanner

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class FindFishService(
    val fishHookDetection: CascadeClassifier
) {

    fun findFish(image: BufferedImage): java.awt.Point? {

        // Place for searching
        val minX = 100
        val minY = 100
        val width = 400
        val height = 1400

        // Image to Mat
        val cropImage = cropImage(image, minX, minY, width, height)
        val sourceImage = bufferedImageToMat(cropImage)

        // Detecting hook on Mat image
        val hookDetections = MatOfRect()
        fishHookDetection.detectMultiScale(sourceImage, hookDetections, 1.1, 1, 1, Size(24.0, 24.0))


        return hookDetections.toList().map { java.awt.Point(it.x, it.y) }.firstOrNull()
    }

    fun cropImage(image: BufferedImage, minX: Int, minY: Int, width: Int, height: Int): BufferedImage {
        return image.getSubimage(minX, minY, width, height)
    }

    fun showResult(sourceImage: Mat, hooks: List<Rect>) {
        for (rect in hooks) {
            Imgproc.rectangle(
                sourceImage,
                Point(rect.tl().x, rect.tl().y), // top-left corner
                Point(rect.br().x, rect.br().y), // bottom-right corner
                Scalar(0.0, 255.0, 0.0), // color (green)
                2 // thickness of the rectangle
            )
        }

        // Конвертация Mat в BufferedImage
        val imageBytes = MatOfByte()
        Imgcodecs.imencode(".jpg", sourceImage, imageBytes)
        val byteArray = imageBytes.toArray()
        val inputStream = ByteArrayInputStream(byteArray)
        val bufferedImage = ImageIO.read(inputStream)
        print("Check bufferedImage in debug")
    }

    fun bufferedImageToMat(bufferedImage: BufferedImage): Mat {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val inputStream = ByteArrayInputStream(byteArray)
        return Imgcodecs.imdecode(MatOfByte(*inputStream.readAllBytes()), Imgcodecs.IMREAD_COLOR)
    }
}