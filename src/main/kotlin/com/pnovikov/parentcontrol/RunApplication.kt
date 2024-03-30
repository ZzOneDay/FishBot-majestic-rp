package com.pnovikov.parentcontrol

import com.pnovikov.parentcontrol.service.fishing.FishWayService
import com.pnovikov.parentcontrol.integration.input.ScreenInput
import com.pnovikov.parentcontrol.integration.output.PushButtonService
import com.pnovikov.parentcontrol.service.fishing.FindFishService
import com.pnovikov.parentcontrol.service.fishing.FishWay
import com.pnovikov.parentcontrol.service.game.GameStatus
import com.pnovikov.parentcontrol.service.game.GameStatusService
import com.pnovikov.parentcontrol.service.random.TimeRandomService
import org.springframework.stereotype.Service
import java.awt.Point

@Service
class RunApplication(
    val findProcessStatus: GameStatusService,
    val screenInput: ScreenInput,
    val findFishService: FindFishService,
    val fishWayService: FishWayService,
    val pushButtonService: PushButtonService,
    val randomService: TimeRandomService
) {
    @Volatile
    var running = true

    fun run() {
        var currentStatus: GameStatus
        var way: FishWay? = null
        var lastPoint: Point? = null

        var isFishing = false

        while (running) {
            Thread.sleep(randomService.randomTime(100))
            val bufferedImage = screenInput.getScreen()
            currentStatus = findProcessStatus.getCurrentStatus(bufferedImage)

            // fish broken
            if (currentStatus == GameStatus.BROKEN) {
                isFishing = false
                lastPoint = null
                way = null
                continue
            }

            if (currentStatus == GameStatus.CATCH) {
                isFishing = false
                lastPoint = null
                way = null
                pushButtonService.click()
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
                        pushButtonService.push(way)
                        lastPoint = point
                    } else if (way != null) {
                        pushButtonService.push(way)
                    }
                }
            }
        }
    }

    fun stop() {
        running = false
    }
}