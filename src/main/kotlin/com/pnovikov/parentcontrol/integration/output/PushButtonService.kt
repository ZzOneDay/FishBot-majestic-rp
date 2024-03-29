package com.pnovikov.parentcontrol.integration.output

import com.pnovikov.parentcontrol.service.fishing.FishWay
import com.pnovikov.parentcontrol.service.random.TimeRandomService
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Robot
import java.awt.event.KeyEvent
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

@Service
class PushButtonService(
    val resourceLoader: ResourceLoader,
    val robot: Robot,
    val randomService: TimeRandomService
) {

    fun push(way: FishWay) {
        if (way == FishWay.RIGHT) {
            println(">> PUSH 'A'")
            pushButton(KeyEvent.VK_A, randomService.randomTime(2000))
        }

        if (way == FishWay.LEFT) {
            println(">> PUSH 'D'")
            pushButton(KeyEvent.VK_D, randomService.randomTime(2000))
        }
    }

    fun click() {
        println(">> PUSH 'Click'")
        playSound("catching.wav")
        Thread.sleep(4000)
    }

    private fun pushButton(keyCode: Int, time: Long) {
        robot.keyPress(keyCode)

        // Удержание клавиши 'A' в течение 1 секунды (1000 миллисекунд)
        Thread.sleep(time)

        // Отпускание клавиши 'A'
        robot.keyRelease(keyCode)
    }

    fun playSound(fileName: String) {
        val path = resourceLoader.getResource("sound/$fileName").file.path
        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File(path))
        val clip: Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        clip.start()
    }
}