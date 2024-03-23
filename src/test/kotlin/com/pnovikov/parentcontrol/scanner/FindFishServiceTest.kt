package com.pnovikov.parentcontrol.scanner

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

@SpringBootTest
class FindFishServiceTest {

    @Autowired
    private lateinit var findFishService: FindFishService

    @Test
    fun test() {
        val images = listOf<String>(
            "hello38.png",
            "hello39.png",
            "hello40.png",
            "hello63.png",
            "hello64.png",
            "hello65.png",
            "hello123.png",
            "hello124.png",
            "hello125.png",
            "hello126.png",
            "hello155.png",
            "hello156.png",
            "hello157.png",
            "hello158.png",
            "hello159.png",
            "hello192.png",
            "hello193.png",
            "hello194.png",
            "hello195.png",
        )
        for(image in images) {
            val fish = loadImageFromResources("fish/$image")
            findFishService.findFish(fish!!) 
        }


    }

    fun loadImageFromResources(resourceName: String): BufferedImage? {
        return try {
            val classLoader = Thread.currentThread().contextClassLoader
            val inputStream = classLoader.getResourceAsStream(resourceName)
            if (inputStream != null) {
                ImageIO.read(inputStream)
            } else {
                println("Ресурс $resourceName не найден")
                null
            }
        } catch (e: Exception) {
            println("Ошибка при загрузке изображения из ресурсов: ${e.message}")
            null
        }
    }
}