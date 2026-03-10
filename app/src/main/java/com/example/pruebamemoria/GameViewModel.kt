package com.example.pruebamemoria

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val _winEvent = MutableLiveData<Boolean>()
    val winEvent: LiveData<Boolean> = _winEvent

    private val _cards = MutableLiveData<List<Card>>()
    val cards: LiveData<List<Card>> = _cards

    private val _moves = MutableLiveData<Int>()
    val moves: LiveData<Int> = _moves

    private val _elapsedSeconds = MutableLiveData<Int>(0)
    val elapsedSeconds: LiveData<Int> = _elapsedSeconds

    private val _timerRunning = MutableLiveData<Boolean>(false)
    val timerRunning: LiveData<Boolean> = _timerRunning

    private var firstSelectedCardIndex: Int? = null
    private var lockBoard = false

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (_timerRunning.value == true) {
                _elapsedSeconds.value = (_elapsedSeconds.value ?: 0) + 1
                handler.postDelayed(this, 1000)
            }
        }
    }

    init {
        startGame()
    }

    fun startGame() {
        stopTimer()
        val imageResources = listOf(
            R.drawable.img_1, R.drawable.img_2, R.drawable.img_3, R.drawable.img_4,
            R.drawable.img_5, R.drawable.img_6, R.drawable.img_7, R.drawable.img_8
        )
        val shuffledCards = (imageResources + imageResources).shuffled().map { Card(imageResId = it) }
        _cards.value = shuffledCards
        _moves.value = 0
        _elapsedSeconds.value = 0
        _winEvent.value = false
        firstSelectedCardIndex = null
        lockBoard = false
    }

    fun flipCard(index: Int) {
        if (lockBoard) return
        val currentCards = _cards.value?.toMutableList() ?: return
        val selectedCard = currentCards[index]
        if (selectedCard.isFaceUp || selectedCard.isMatched) return

        if (_timerRunning.value == false) startTimer()

        selectedCard.isFaceUp = true

        if (firstSelectedCardIndex == null) {
            firstSelectedCardIndex = index
        } else {
            _moves.value = _moves.value?.plus(1)
            val firstCard = currentCards[firstSelectedCardIndex!!]
            if (firstCard.imageResId == selectedCard.imageResId) {
                firstCard.isMatched = true
                selectedCard.isMatched = true
                firstSelectedCardIndex = null
                checkWin(currentCards)
            } else {
                lockBoard = true
                Handler(Looper.getMainLooper()).postDelayed({
                    firstCard.isFaceUp = false
                    selectedCard.isFaceUp = false
                    firstSelectedCardIndex = null
                    lockBoard = false
                    _cards.value = currentCards
                }, 1000)
            }
        }
        _cards.value = currentCards
    }

    private fun startTimer() {
        _timerRunning.value = true
        handler.postDelayed(timerRunnable, 1000)
    }

    private fun stopTimer() {
        _timerRunning.value = false
        handler.removeCallbacks(timerRunnable)
    }

    private fun checkWin(cards: List<Card>) {
        if (cards.all { it.isMatched }) {
            stopTimer()
            _winEvent.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    companion object {
        fun formatTime(seconds: Int): String {
            val m = seconds / 60
            val s = seconds % 60
            return "%02d:%02d".format(m, s)
        }
    }
}
