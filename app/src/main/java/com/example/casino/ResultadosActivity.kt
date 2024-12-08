package com.example.casino

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class ResultadosActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultados)

        // Obtener los datos enviados
        val dineroGanado = intent.getFloatExtra("dineroGanado", 0.0f)
        val dineroPerdido = intent.getFloatExtra("dineroPerdido", 0.0f)
        val saldo = intent.getFloatExtra("saldo", 0.0f)
        val resultadosPartidos = intent.getStringArrayListExtra("resultadosPartidos")

        // Referenciar los TextViews
        val textViewGanado = findViewById<TextView>(R.id.textViewGanado)
        val textViewPerdido = findViewById<TextView>(R.id.textViewPerdido)
        val textViewResultados = findViewById<TextView>(R.id.textViewResultados)
        val textViewSaldo = findViewById<TextView>(R.id.saldo)
        val videoView = findViewById<VideoView>(R.id.videoView)

        // Mostrar los resultados
        textViewSaldo.text = getString(R.string.saldo, saldo.toString())
        textViewGanado.text = getString(R.string.dinero_ganado, dineroGanado.toString())
        textViewPerdido.text = getString(R.string.dinero_perdido, dineroPerdido.toString())
        textViewResultados.text = resultadosPartidos?.joinToString("\n") ?: getString(R.string.no_hubo_resultados)

        // Configurar el video
        val videoUri = Uri.parse("android.resource://${packageName}/raw/tu_video")
        videoView.setVideoURI(videoUri)

        // Silenciar el video
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.setVolume(0f, 0f)
        }

        // Reproducir el video
        videoView.start()
    }
}
