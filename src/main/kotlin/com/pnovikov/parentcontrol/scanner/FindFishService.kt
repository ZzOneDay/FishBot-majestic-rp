package com.pnovikov.parentcontrol.scanner

import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
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
class FindFishService {

    fun findFish(image: BufferedImage): java.awt.Point {
        val minY = 100;
        val minX = 100;
        val maxY = image.height - 300
        val maxX = image.width - 400

//        for (xStep in minX .. maxX step 5) {
//            for(yStep in minY .. maxY step 5) {
//                //image.setRGB(xStep,yStep, 0xFF0000)
//                //val currentStep = image.getRGB(xStep, yStep)
//                val rgb = image.getRGB(xStep, yStep)
//                val color = Color(rgb)
////                if (color.blue > 200 && color.red > 200 && color.green > 200 ) {
////                    image.setRGB(xStep,yStep, 0xFF0000)
////                }
//            }
//        }

        val point = openCV(image)

        makeTarget(image, point.x, point.y)

        return java.awt.Point()
    }

    fun openCV(image: BufferedImage): java.awt.Point {
        // Загрузка изображения и шаблона
        val sourceImage = bufferedImageToMat(image)//Imgcodecs.imread("source_image.jpg")
        val templateImagePath = object {}.javaClass.getResource("template/template.png")?.path ?:
        throw IllegalArgumentException("Could not find resource: template")
        val templateImage = Imgcodecs.imread(templateImagePath)

        // Создание матрицы для хранения результата сравнения
        val result = Mat()

        // Сравнение шаблона с изображением
        Imgproc.matchTemplate(sourceImage, templateImage, result, Imgproc.TM_CCOEFF_NORMED)

        // Поиск лучшего совпадения
        val minMaxLocResult: Core.MinMaxLocResult = Core.minMaxLoc(result)

        // Получение координат наилучшего совпадения
        val matchLoc = minMaxLocResult.maxLoc

//        // Отрисовка прямоугольника вокруг совпадения
//        Imgproc.rectangle(
//            sourceImage,
//            matchLoc,
//            Point(matchLoc.x + templateImage.cols(), matchLoc.y + templateImage.rows()),
//            Scalar(0.0, 255.0, 0.0),
//            2
//        )
//
//
//
//        // Сохранение результата
//        Imgcodecs.imwrite("result_image.jpg", sourceImage)
        return java.awt.Point(
            (matchLoc.x + templateImage.cols()).toInt(),
            (matchLoc.y + templateImage.rows()).toInt()
        )
    }

    fun bufferedImageToMat(bufferedImage: BufferedImage): Mat {
        val byteArrayOutputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val inputStream = ByteArrayInputStream(byteArray)
        return Imgcodecs.imdecode(MatOfByte(*inputStream.readAllBytes()), Imgcodecs.IMREAD_COLOR)
    }

    fun makeTarget(image: BufferedImage, x: Int, y: Int) {
        val xFrom = x - 10
        val xTo = x + 10

        val yFrom = y - 10;
        val yTo = y + 10;

        for (xStep in xFrom .. xTo) {
            for(yStep in yFrom .. yTo) {
                image.setRGB(xStep,yStep, 0xFF0000)
            }
        }
    }


}