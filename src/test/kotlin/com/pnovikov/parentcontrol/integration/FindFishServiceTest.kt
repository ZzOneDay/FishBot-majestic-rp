package com.pnovikov.parentcontrol.integration

import com.pnovikov.parentcontrol.service.fishing.FindFishService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import javax.imageio.ImageIO

@SpringBootTest
class FindFishServiceTest {

    @Autowired
    private lateinit var findFishService: FindFishService

    @Test
    fun testFishIsFound() {

        var errors = ""
        for (imageFile in loadImageFromResources("fish")) {
            val image = ImageIO.read(imageFile)
            val point = findFishService.findFish(image)
            if (point == null) {
                errors += "Fish not found in file: ${imageFile.name}\n"
            }
        }
        assertTrue(errors == "", errors)
    }

    @Test
    fun testFishIsNotFound() {
        var errors = ""
        for (imageFile in loadImageFromResources("nofish")) {
            val image = ImageIO.read(imageFile)
            val point = findFishService.findFish(image)
            if (point != null) {
                errors += "Fish is found in file: ${imageFile.name}\n"
            }
        }
        assertTrue(errors == "", errors)
    }

    fun loadImageFromResources(resourceFolder: String): List<File> {
        val classLoader = Thread.currentThread().contextClassLoader
        val folderPath = classLoader.getResource(resourceFolder)!!.path
        return File(folderPath).listFiles()?.toList() ?: throw IllegalArgumentException()
    }
}