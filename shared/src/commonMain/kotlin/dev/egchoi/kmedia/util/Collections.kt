package dev.egchoi.kmedia.util

internal fun <T> List<T>.reorder(from: Int, to: Int): List<T> {
    return this.toMutableList().apply {
        val movedItem = removeAt(from)
        add(to, movedItem)
    }
}

internal fun <T> MutableList<T>.reorder(from: Int, to: Int) {
    val movedItem = removeAt(from)
    add(to, movedItem)
}