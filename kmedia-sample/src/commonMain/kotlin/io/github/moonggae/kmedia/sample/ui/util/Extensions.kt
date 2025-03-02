package io.github.moonggae.kmedia.sample.ui.util

import androidx.compose.ui.Modifier

fun Modifier.conditional(condition : Boolean, modifier : Modifier.() -> Modifier) : Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}


fun <T> List<T>.reorder(from: Int, to: Int): List<T> {
    return this.toMutableList().apply {
        val movedItem = removeAt(from)
        add(to, movedItem)
    }
}

fun <T> MutableList<T>.reorder(from: Int, to: Int) {
    val movedItem = removeAt(from)
    add(to, movedItem)
}

fun <T> List<T>.replace(index: Int, element: T): List<T> {
    return this.toMutableList().apply {
        removeAt(index)
        add(index, element)
    }
}

fun <T> MutableList<T>.toggle(item: T) {
    if (contains(item)) {
        remove(item)
    } else {
        add(item)
    }
}

fun <T> List<T>.toggleElement(item: T): List<T> {
    return if (contains(item)) {
        filterNot { it == item }
    } else {
        plus(item)
    }
}

fun Long.toTimestampMMSS(): String {
    val minutes = when (this > 0) {
        true -> this / 1000 / 60
        else -> 0
    }
    val seconds = when (this > 0) {
        true -> this / 1000 % 60
        else -> 0
    }
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}