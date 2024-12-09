package com.example.casino

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
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

        val isRoulette = intent.getBooleanExtra("isRoulette", false)
        val historySlot = if (isRoulette) "ruleta" else "momios"

        // Recuperar el historial de SharedPreferences
        val sharedPreferences = getSharedPreferences("${idUsuario}${historySlot}", MODE_PRIVATE)
        val historialConcatenado = sharedPreferences.getString("historial", "") ?: ""
        val historialList = historialConcatenado.split(";").filter { it.isNotBlank() }

        // Configurar el adaptador con la lista de historial
        val adapter = HistorialAdapter(historialList)
        recyclerView.adapter = adapter

        // traete el perro boton del XML
        val btnBorrarHistorial: Button = findViewById(R.id.btnBorrarHistorial)

        // Configurar al perro boton de cagada que borra el historial
        btnBorrarHistorial.setOnClickListener {
            // Eliminar el historial de SharedPreferences alv
            val editor = sharedPreferences.edit()
            editor.remove("historial") // Elimina el historial almacenado
            editor.apply()

            // Actualiza el RecyclerView para mostrar la lista vacía
            val emptyList = listOf<String>()
            val emptyAdapter = HistorialAdapter(emptyList)
            recyclerView.adapter = emptyAdapter

            // Opcional: Muestra un mensaje confirmando que el historial fue borrado
            Toast.makeText(this, "Historial borrado exitosamente", Toast.LENGTH_SHORT).show()
        }
    }
}