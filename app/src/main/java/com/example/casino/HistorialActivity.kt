package com.example.casino

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistorialActivity : AppCompatActivity() {
    private lateinit var idUsuario: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        // Recuperar el RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewHistorial)

        // Configurar el RecyclerView con un LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        idUsuario = intent.getStringExtra("IdUsuario") ?: ""


        // Recuperar el historial de SharedPreferences
        val sharedPreferences = getSharedPreferences("${idUsuario}momios", MODE_PRIVATE)
        val historialConcatenado = sharedPreferences.getString("historial", "") ?: ""
        val historialList = historialConcatenado.split(";").filter { it.isNotBlank() }

        // Configurar el adaptador con la lista de historial
        val adapter = HistorialAdapter(historialList)
        recyclerView.adapter = adapter
    }
}
