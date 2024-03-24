package com.pnovikov.parentcontrol.scanner

import nu.pattern.OpenCV
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.objdetect.CascadeClassifier
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.awt.image.Raster
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
class FindFishService(
        var resourceLoader: ResourceLoader
) {

    fun findFish(image: BufferedImage): java.awt.Point {
        val minY = 100;
        val minX = 100;
        val maxY = image.height - 300
        val maxX = image.width - 400

        OpenCV.loadLocally()
        val hookDetector = CascadeClassifier()

        val templateImagePath = resourceLoader.getResource("template/cascade.xml")?.file?.path
                ?: throw IllegalArgumentException()

        val sourceImage = bufferedImageToMat(image)

        // Detecting hook
        val hookDetections = MatOfRect()
        hookDetector.load(templateImagePath);

        hookDetector.detectMultiScale(sourceImage, hookDetections, 1.1,1,1, org.opencv.core.Size(24.0, 24.0))

        val hooks: List<Rect> = hookDetections.toList()

        for (rect in hooks) {
            Imgproc.rectangle(
                    sourceImage,
                    org.opencv.core.Point(rect.tl().x, rect.tl().y), // top-left corner
                    org.opencv.core.Point(rect.br().x, rect.br().y), // bottom-right corner
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

        return java.awt.Point()
    }

    fun bufferedImageToMat(bufferedImage: BufferedImage): Mat {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val inputStream = ByteArrayInputStream(byteArray)
        return Imgcodecs.imdecode(MatOfByte(*inputStream.readAllBytes()), Imgcodecs.IMREAD_COLOR)
    }
}