package com.example.pruebamemoria

import android.os.Bundle
import androidx.lifecycle.ViewModel
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pruebamemoria.ui.theme.PruebaMemoriaTheme

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: GameViewModel
    private lateinit var adapter: CardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        adapter = CardAdapter(emptyList()) {
            viewModel.flipCard(it)
        }
        val progressBar = findViewById<ProgressBar>(R.id.progressMoves)
        viewModel.moves.observe(this) { moveCount ->
            findViewById<TextView>(R.id.tvMoves).text = "Movimientos: $moveCount"
            progressBar.progress = moveCount
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvBoard)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.adapter = adapter

        viewModel.cards.observe(this) {
            adapter.updateCards(it)
        }



        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            viewModel.startGame()
            progressBar.progress = 0
        }
        viewModel.winEvent.observe(this) { hasWon ->
            if (hasWon) {
                Toast.makeText(
                    this,
                    "¡Ganaste!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}