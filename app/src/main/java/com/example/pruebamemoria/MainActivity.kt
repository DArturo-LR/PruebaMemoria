package com.example.pruebamemoria

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: GameViewModel
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        adapter = CardAdapter(emptyList()) { viewModel.flipCard(it) }

        val progressBar = findViewById<ProgressBar>(R.id.progressMoves)
        val tvMoves = findViewById<TextView>(R.id.tvMoves)
        val tvTimer = findViewById<TextView>(R.id.tvTimer)

        // Observe moves
        viewModel.moves.observe(this) { moveCount ->
            tvMoves.text = moveCount.toString()
            progressBar.progress = moveCount
        }

        // Observe timer
        viewModel.elapsedSeconds.observe(this) { seconds ->
            tvTimer.text = GameViewModel.formatTime(seconds)
        }

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rvBoard)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = adapter

        viewModel.cards.observe(this) { adapter.updateCards(it) }

        // Restart button
        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            viewModel.startGame()
            progressBar.progress = 0
        }

        // Menu button - go back
        findViewById<Button>(R.id.btnMenu).setOnClickListener {
            finish()
        }

        // Win event
        viewModel.winEvent.observe(this) { hasWon ->
            if (hasWon) {
                val time = GameViewModel.formatTime(viewModel.elapsedSeconds.value ?: 0)
                val moves = viewModel.moves.value ?: 0
                Toast.makeText(
                    this,
                    "🎉 ¡Ganaste! Tiempo: $time | Movimientos: $moves",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
