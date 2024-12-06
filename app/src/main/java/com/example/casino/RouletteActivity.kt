package com.example.casino

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RouletteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_roulette)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recupera datos del intent
        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        val idUsuario = intent.getStringExtra("IdUsuario")
        val sharedPreferences = getSharedPreferences(idUsuario, MODE_PRIVATE)
        var saldo = sharedPreferences.getFloat("saldo", 0.0f)


        saldo = sharedPreferences.getFloat("saldo", 0.0f)

        // Encuentra el tvSaldoR y actualiza el valor del saldo
        val saldoTextView = findViewById<TextView>(R.id.tvSaldoR)
        saldoTextView.text = saldo.toString()

        // Inicializa las variables de los videos
        val videos = listOf(
            Pair(R.raw.video1, 1.0f),
            Pair(R.raw.video2, 2.2f),
            Pair(R.raw.video3, 1.1f),
            Pair(R.raw.video4, 0.4f),
            Pair(R.raw.video5, 5.8f)
        )

        // Busca elementos en la interfaz
        val videoView = findViewById<VideoView>(R.id.videoView)
        val btnSpin = findViewById<Button>(R.id.btnSpin)
        val editTextBet = findViewById<EditText>(R.id.editTextBet)
        val btnMinusBet = findViewById<Button>(R.id.btnMinusBet)
        val btnPlusBet = findViewById<Button>(R.id.btnPlusBet)

        //Funcion para aumentar o disminuir mi editTextBet
        fun updateBetValue(editText: EditText, increment: Int) {
            val currentBet = editText.text.toString().toIntOrNull()
                ?: 0 // Valor actual, 0 si está vacío o inválido
            val newBet = (currentBet + increment).coerceAtLeast(0) // Evita valores negativos
            editText.setText(newBet.toString())
        }

        btnMinusBet.setOnClickListener {
            updateBetValue(editTextBet, -10) // Decrementa en 10
        }

        btnPlusBet.setOnClickListener {
            updateBetValue(editTextBet, 10) // Incrementa en 10
        }


        videoView.setBackgroundResource(R.drawable.ruleta_inicio)
        // Configura el botón btnSpin
        btnSpin.setOnClickListener {


            // Obtiene el texto ingresado en el EditText
            val betText = editTextBet.text.toString()
            if (betText.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa una apuesta.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convierte la apuesta a Float
            val bet = betText.toFloatOrNull()
            if (bet == null || bet <= 0) {
                Toast.makeText(this, "Ingresa una apuesta válida.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verifica que haya saldo suficiente
            if (bet > saldo) {
                Toast.makeText(this, "No tienes suficiente saldo.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Selecciona un video aleatorio y obtiene su multiplicador
            val randomVideo = videos.random()
            val videoUri = Uri.parse("android.resource://$packageName/${randomVideo.first}")
            val multiplier = randomVideo.second

            // Reproduce el video
            videoView.setVideoURI(videoUri)
            videoView.setOnPreparedListener {
                it.isLooping = false // No se repite
                videoView.start()
            }
            //
            videoView.setOnPreparedListener {
                // Quitar el color de fondo cuando el video comienza
                videoView.setBackgroundColor(android.graphics.Color.TRANSPARENT) // Quita el fondo
                it.isLooping = false // No se repite
                videoView.start() // Inicia el video

            }
            // Multiplica la apuesta por un número segun el video
            val winnings = bet * multiplier

            //538.9
            videoView.setOnCompletionListener {
                // Muestra un mensaje con la ganancia y el multiplicador
                Toast.makeText(
                    this,
                    " Ganaste: $winnings",
                    Toast.LENGTH_LONG
                ).show()

                videoView.setBackgroundResource(R.drawable.ruleta_ganador)

                saldo += winnings
                saldoTextView.text = saldo.toString()
                sharedPreferences.edit().putFloat("saldo", saldo).apply()

            }
        }
    }
}




