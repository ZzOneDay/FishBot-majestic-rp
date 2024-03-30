package com.pnovikov.parentcontrol.integration.output

data class PushAction(
    val type: KeyType,
    val keyCode: Int,
    val pushAction: Action
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PushAction

        if (type != other.type) return false
        if (keyCode != other.keyCode) return false
        return pushAction == other.pushAction
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + keyCode
        result = 31 * result + pushAction.hashCode()
        return result
    }
}

enum class Action{
    PUSH,
    UNPUSH
}

enum class KeyType {
    KEYBOARD,
    MOUSE
}