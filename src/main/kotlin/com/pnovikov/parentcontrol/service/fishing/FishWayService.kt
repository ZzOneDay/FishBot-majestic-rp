package com.pnovikov.parentcontrol.service.fishing

import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Point
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

@Service
class FishWayService(
    val resourceLoader: ResourceLoader
) {

    fun getWay(start: Point, finish: Point): FishWay {
        return if (start.x < finish.x) {
            playSound(
                resourceLoader.getResource("sound/left_sound.wav").file.path
            )
            FishWay.RIGHT
        } else {
            playSound(
                resourceLoader.getResource("sound/right_sound.wav").file.path
            )
            FishWay.LEFT
        }
    }


    fun playSound(filePath: String) {
        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File(filePath))
        val clip: Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        clip.start()
    }
}