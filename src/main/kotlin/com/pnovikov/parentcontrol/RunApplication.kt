package com.pnovikov.parentcontrol

import com.pnovikov.parentcontrol.core.FishWayService
import com.pnovikov.parentcontrol.core.Ways
import com.pnovikov.parentcontrol.input.ScreenInput
import com.pnovikov.parentcontrol.output.PushButtonService
import com.pnovikov.parentcontrol.scanner.FindFishService
import com.pnovikov.parentcontrol.scanner.FindProcessStatus
import org.springframework.stereotype.Service
import java.awt.Point
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

@Service
class RunApplication(
    val findProcessStatus: FindProcessStatus,
    val screenInput: ScreenInput,
    val findFishService: FindFishService,
    val fishWayService: FishWayService,
    val pushButtonService: PushButtonService
) {
    @Volatile
    var running = true

    fun run() {
        var currentStatus: GameStatus
        var way: Ways? = null
        var lastPoint: Point? = null

        var isFishing = false

        while (running) {
            Thread.sleep(2000)
            val bufferedImage = screenInput.getScreen()
            currentStatus = findProcessStatus.getCurrentStatus(bufferedImage)

            if (currentStatus == GameStatus.WAITING) {
                // Ждемся
            }

            if (currentStatus == GameStatus.FINISHING || isFishing) {
                isFishing = true
                val point = findFishService.findFish(bufferedImage)

                if (lastPoint == null) {
                    lastPoint = point
                    continue
                } else {
                    if (point != null) {
                        way = fishWayService.getWay(lastPoint, point)
                        Thread.sleep(1000)
                        pushButtonService.push(way)
                        lastPoint = point
                    }
                }
            }

            if (currentStatus == GameStatus.CATCH) {
                isFishing = false
                lastPoint = null
                way = null
                pushButtonService.click()
            }
        }
    }

    fun stop() {
        running = false
    }
}