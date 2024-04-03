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
        var isAutoStart = false

        while (running) {
            Thread.sleep(randomService.randomTime(500))
            val bufferedImage = screenInput.getScreen()
            currentStatus = findProcessStatus.getCurrentStatus(bufferedImage)

            if (currentStatus == GameStatus.NOTHING && isAutoStart) {
                Thread.sleep(randomService.randomTime(2000))
                actionQueue.add(PushAction(
                    KeyType.KEYBOARD,
                    KeyEvent.VK_E,
                    Action.PUSH
                ))
                actionQueue.add(
                    PushAction(
                        KeyType.KEYBOARD,
                        KeyEvent.VK_E,
                        Action.UNPUSH
                    )
                )
                lastAction = null
                isAutoStart = false
            }

            if (currentStatus == GameStatus.STOP) {
                isFishing = false
                isCatching = false
                isAutoStart = false
                if (lastAction != null) {
                    val cancelledLastAction = cancelledLastAction(lastAction!!)
                    actionQueue.add(cancelledLastAction)
                }
                lastPoint = null
            }

            // Рыбка сорвалась
            if (currentStatus == GameStatus.BROKEN && lastAction != null) {
                isFishing = false
                isCatching = false
                isAutoStart = false
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
                actionQueue.addAll(getActionOfCatch())
            }

            // Заканчиваем тянуть рыбу
            if (isCatching && currentStatus != GameStatus.CATCH && lastAction != null) {
                isCatching = false
                // it will be unpush mouse
                val cancelledLastAction = cancelledLastAction(lastAction!!)
                actionQueue.add(cancelledLastAction)
                lastAction = null
                isAutoStart = true
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
        val newAction = when(way) {
            FishWay.RIGHT -> PushAction(
                KeyType.KEYBOARD,
                KeyEvent.VK_A,
                Action.PUSH
            )
            FishWay.LEFT -> PushAction(
                KeyType.KEYBOARD,
                KeyEvent.VK_D,
                Action.PUSH
            )
        }

        return if (lastAction != newAction) {
            if (lastAction != null) {
                actions.add(cancelledLastAction(lastAction!!))
            }
            actions.add(newAction)
            lastAction = newAction
            actions
        } else {
            emptyList()
        }
    }

    fun getActionOfCatch(): List<PushAction> {
        val actions = arrayListOf<PushAction>()
        val newAction = PushAction(
            KeyType.MOUSE,
            InputEvent.BUTTON1_DOWN_MASK,
            Action.PUSH
        )

        return if (lastAction != newAction) {
            if (lastAction != null) {
                actions.add(cancelledLastAction(lastAction!!))
            }
            actions.add(newAction)
            lastAction = newAction
            actions
        } else {
            emptyList()
        }
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