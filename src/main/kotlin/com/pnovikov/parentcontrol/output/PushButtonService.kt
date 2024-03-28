package com.pnovikov.parentcontrol.output

import com.pnovikov.parentcontrol.core.Ways
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

@Service
class PushButtonService(
    val resourceLoader: ResourceLoader
) {

    fun push(way: Ways) {
        if (way == Ways.LEFT) {
            println("Нажимаю кнопку 'A'")
            playSound("push_left.wav")
            Thread.sleep(4000)
            playSound("unpush_left.wav")
            println("Отпуска кнопку 'A'")
        }

        if (way == Ways.RIGHT) {
            println("Нажимаю кнопку 'D'")
            playSound("push_right.wav")
            Thread.sleep(4000)
            playSound("unpush_right.wav")
            println("Отпуска кнопку 'D'")
        }
    }

    fun click() {
        println("Нажимаю кнопку Click")
        playSound("catching.wav")
        Thread.sleep(4000)
        println("Отпуска кнопку Click")
    }

    fun playSound(fileName: String) {
        val path = resourceLoader.getResource("sound/$fileName").file.path
        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File(path))
        val clip: Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        clip.start()
    }
}