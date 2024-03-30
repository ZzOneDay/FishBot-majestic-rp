package com.pnovikov.parentcontrol

import com.pnovikov.parentcontrol.service.fishing.FishWayService
import com.pnovikov.parentcontrol.integration.input.ScreenInput
import com.pnovikov.parentcontrol.integration.output.*
import com.pnovikov.parentcontrol.service.fishing.FindFishService
import com.pnovikov.parentcontrol.service.fishing.FishWay
import com.pnovikov.parentcontrol.service.game.GameStatus
import com.pnovikov.parentcontrol.service.game.GameStatusService
import com.pnovikov.parentcontrol.service.random.TimeRandomService
import org.springframework.stereotype.Service
import java.awt.Point
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.util.concurrent.LinkedBlockingQueue

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

    private val actionQueue = LinkedBlockingQueue<PushAction>()
    private var lastAction: PushAction? = null

    init {
        // Создание и запуск потока для обработки действий
        val actionProcessingThread = Thread {
            while (true) {
                try {
                    val action = actionQueue.take() // Блокирующий вызов, ожидание нового действия
                    pushButtonService.doAction(action)
                } catch (e: InterruptedException) {
                    // Обработка прерывания
                    break
                }
            }
        }
        actionProcessingThread.start()
    }

    fun run() {
        var currentStatus: GameStatus
        var lastPoint: Point? = null
        var isFishing = false
        var isCatching = false

        while (running) {
            Thread.sleep(randomService.randomTime(1000))
            val bufferedImage = screenInput.getScreen()
            currentStatus = findProcessStatus.getCurrentStatus(bufferedImage)

            // Рыбка сорвалась
            if (currentStatus == GameStatus.BROKEN) {
                isFishing = false
                isCatching = false
                lastPoint = null

                val cancelledLastAction = cancelledLastAction(lastAction!!)
                actionQueue.add(cancelledLastAction)
                lastAction = null
                continue
            }

            // Начинаем тянуть рыбу
            if (currentStatus == GameStatus.CATCH) {
                isFishing = false
                isCatching = true
                lastPoint = null

                val cancelledLastAction = cancelledLastAction(lastAction!!)
                val clickMouse = PushAction(
                    KeyType.MOUSE,
                    InputEvent.BUTTON1_DOWN_MASK,
                    Action.PUSH
                )
                actionQueue.add(cancelledLastAction)
                actionQueue.add(clickMouse)
                lastAction = clickMouse
            }

            // Заканчиваем тянуть рыбу
            if (isCatching && currentStatus != GameStatus.CATCH) {
                isCatching = false
                // it will be unpush mouse
                val cancelledLastAction = cancelledLastAction(lastAction!!)
                actionQueue.add(cancelledLastAction)
                lastAction = null
            }

            // Поиск рыбы, движения рыбы, нажимаем кнопки для ловли
            if (currentStatus == GameStatus.FINISHING || isFishing) {
                isFishing = true
                val point = findFishService.findFish(bufferedImage)

                if (lastPoint == null) {
                    lastPoint = point
                    continue
                } else {
                    if (point != null) {
                        // Находим движение рыбы и начинаем тянуть
                        val way = fishWayService.getWay(lastPoint, point)
                        val actionOfWay = getActionOfWay(way)
                        // Если команда будет такая же которая была - она будет игнорироваться
                        actionQueue.addAll(actionOfWay)
                        lastPoint = point
                    }
                }
            }
        }
    }

    fun getActionOfWay(way: FishWay): List<PushAction> {
        val actions = arrayListOf<PushAction>()
        if (lastAction != null) {
            actions.add(cancelledLastAction(lastAction!!))
        }
        if (way == FishWay.RIGHT) {
            //println(">> PUSH 'A'")
            val newAction = PushAction(
                KeyType.KEYBOARD,
                KeyEvent.VK_A,
                Action.PUSH
            )
            actions.add(newAction)
            lastAction = newAction
        }

        if (way == FishWay.LEFT) {
            //println(">> PUSH 'D'")
            val newAction = PushAction(
                KeyType.KEYBOARD,
                KeyEvent.VK_D,
                Action.PUSH
            )
            actions.add(newAction)
            lastAction = newAction
        }

        return actions
    }

    fun cancelledLastAction(lastAction: PushAction): PushAction {
        val newAction = when (lastAction.pushAction) {
            Action.PUSH -> Action.UNPUSH
            Action.UNPUSH -> Action.PUSH
        }

        return PushAction(
            lastAction.type,
            lastAction.keyCode,
            newAction
        )
    }

    fun stop() {
        running = false
    }
}