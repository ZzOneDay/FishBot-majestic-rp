package com.pnovikov.parentcontrol.integration.output

import com.pnovikov.parentcontrol.service.fishing.FishWay
import com.pnovikov.parentcontrol.service.random.TimeRandomService
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.awt.Robot
import java.awt.event.InputEvent
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

    val lastAction: PushAction? = null

    fun doAction(action: PushAction) {
        if (action != lastAction) {
            when (action.type) {
                KeyType.MOUSE -> handleMouse(action);
                KeyType.KEYBOARD -> handleKeyboard(action)
            }
        }
    }

    //InputEvent.BUTTON1_DOWN_MASK
    fun handleMouse(action: PushAction) {
        when (action.pushAction) {
            Action.PUSH -> {
                println(">>PUSH Mouse: ${action.keyCode}")
                robot.mousePress(action.keyCode)
            }

            Action.UNPUSH -> {
                println(">>UNPUSH Mouse: ${action.keyCode}")
                robot.mouseRelease(action.keyCode)
            }
        }
    }

    fun handleKeyboard(action: PushAction) {
        when(action.pushAction) {
            Action.PUSH -> {
                println(">>PUSH Keyboard: ${action.keyCode}")
                robot.keyPress(action.keyCode)
            }
            Action.UNPUSH -> {
                println(">>UNPUSH Keyboard: ${action.keyCode}")
                robot.keyRelease(action.keyCode)
            }
        }
    }

    fun push(way: FishWay) {
        if (way == FishWay.RIGHT) {
            println(">> PUSH 'A'")
            pushButton(KeyEvent.VK_A, randomService.randomTime(1000))
        }

        if (way == FishWay.LEFT) {
            println(">> PUSH 'D'")
            pushButton(KeyEvent.VK_D, randomService.randomTime(1000))
        }
    }

    fun click() {
        println(">> PUSH 'Click'")
        playSound("catching.wav")
        pushMouse(randomService.randomTime(4000))
    }

    private fun pushButton(keyCode: Int, time: Long) {
        robot.keyPress(keyCode)

        // Удержание клавиши 'A' в течение 1 секунды (1000 миллисекунд)
        Thread.sleep(time)

        // Отпускание клавиши 'A'
        robot.keyRelease(keyCode)
    }

    private fun pushMouse(time: Long) {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)

        // Удержание клавиши 'A' в течение 1 секунды (1000 миллисекунд)
        Thread.sleep(time)

        // Отпускание клавиши 'A'
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }

    fun playSound(fileName: String) {
        val path = resourceLoader.getResource("sound/$fileName").file.path
        val audioInputStream: AudioInputStream = AudioSystem.getAudioInputStream(File(path))
        val clip: Clip = AudioSystem.getClip()
        clip.open(audioInputStream)
        clip.start()
    }
}