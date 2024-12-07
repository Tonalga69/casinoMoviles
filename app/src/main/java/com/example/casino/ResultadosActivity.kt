package com.example.casino

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class ResultadosActivity : AppCompatActivity() {
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
        textViewSaldo.text = "Saldo: $saldo"
        textViewGanado.text = "Dinero ganado: $dineroGanado"
        textViewPerdido.text = "Dinero perdido: $dineroPerdido"
        textViewResultados.text = resultadosPartidos?.joinToString("\n") ?: "No hubo resultados"

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
