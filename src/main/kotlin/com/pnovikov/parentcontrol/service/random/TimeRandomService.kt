package com.pnovikov.parentcontrol.service.random

import org.springframework.stereotype.Component

@Component
class TimeRandomService {

    fun randomTime(time: Int): Long {
        val partTime = time.toDouble() / 3
        return (time + Math.random() * partTime).toLong()
    }
}