package dev.egchoi.kmedia.controller

import dev.egchoi.kmedia.model.RepeatMode
import kotlin.experimental.ExperimentalNativeApi

// Shuffle Manager
class ShuffleManager {
    private var shuffledIndices = mutableListOf<Int>()

    fun updateShuffleIndices(currentIndex: Int, totalSize: Int) {
        val remainingIndices = (0 until totalSize)
            .filter { it != currentIndex }
            .toMutableList()
        remainingIndices.shuffle()

        shuffledIndices = mutableListOf(currentIndex).apply {
            addAll(remainingIndices)
        }
    }

    fun getNextIndex(currentIndex: Int, repeatMode: RepeatMode): Int? {
        val currentShuffledIndex = shuffledIndices.indexOf(currentIndex)
        return when {
            currentShuffledIndex == shuffledIndices.lastIndex &&
                    repeatMode == RepeatMode.REPEAT_MODE_OFF -> null
            currentShuffledIndex == shuffledIndices.lastIndex ->
                shuffledIndices.firstOrNull()
            else -> shuffledIndices.getOrNull(currentShuffledIndex + 1)
        }
    }

    fun getPreviousIndex(currentIndex: Int): Int? {
        val currentShuffledIndex = shuffledIndices.indexOf(currentIndex)
        return if (currentShuffledIndex > 0) {
            shuffledIndices[currentShuffledIndex - 1]
        } else null
    }

    @OptIn(ExperimentalNativeApi::class)
    fun removeIndex(index: Int) {
        shuffledIndices.remove(index)
        shuffledIndices.replaceAll { if (it > index) it - 1 else it }
    }

    fun clear() {
        shuffledIndices.clear()
    }

    fun addNewIndices(startIndex: Int, count: Int) {
        val newIndices = (startIndex until startIndex + count).toMutableList()
        newIndices.shuffle()
        shuffledIndices.addAll(newIndices)
    }
}