package com.example.casino

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casino.models.Partido
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MomiosAdminActivity : AppCompatActivity() {
    private lateinit var spinnerPartidos: Spinner
    private lateinit var equipo1Label: TextView
    private lateinit var equipo2Label: TextView
    private lateinit var momio1Input: EditText
    private lateinit var momio2Input: EditText
    private lateinit var momio3Input: EditText
    private lateinit var btnGuardar: Button

    private lateinit var partidos: List<Partido>
    private var partidoSeleccionado: Partido? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_momios_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val isAdmin = intent.getBooleanExtra("isAdmin", false);
        val idUsuario = intent.getStringExtra("IdUsuario");


        // Inicializar vistas
        spinnerPartidos = findViewById(R.id.spinnerPartidos)
        equipo1Label = findViewById(R.id.equipo1)
        equipo2Label = findViewById(R.id.equipo2)
        momio1Input = findViewById(R.id.momio1)
        momio2Input = findViewById(R.id.momio2)
        momio3Input = findViewById(R.id.momio3)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Cargar partidos desde SharedPreferences
        partidos = cargarPartidos()

        // Configurar Spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            partidos.map { "${it.equipo1} vs ${it.equipo2}" }
        )
        spinnerPartidos.adapter = adapter

        // Manejar selección de partido
        spinnerPartidos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                partidoSeleccionado = partidos[position]
                actualizarFormulario(partidos[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Guardar datos
        btnGuardar.setOnClickListener {
            guardarDatos()
        }
    }

    private fun actualizarFormulario(partido: Partido) {
        equipo1Label.text = partido.equipo1
        equipo2Label.text = partido.equipo2
        momio1Input.setText(partido.momio1.toString())
        momio2Input.setText(partido.momio2.toString())
        momio3Input.setText(partido.momio3.toString())
    }

    private fun cargarPartidos(): List<Partido> {
        val sharedPreferences = getSharedPreferences("momios", MODE_PRIVATE)
        val partidosJson = sharedPreferences.getString("momios", "[]")
        return Json.decodeFromString(partidosJson ?: "[]")
    }

    private fun guardarDatos() {
        partidoSeleccionado?.let { partido ->
            partido.momio1 = momio1Input.text.toString().toFloatOrNull() ?: partido.momio1
            partido.momio2 = momio2Input.text.toString().toFloatOrNull() ?: partido.momio2
            partido.momio3 = momio3Input.text.toString().toFloatOrNull() ?: partido.momio3

            val updatedPartidos = partidos.map { if (it.id == partido.id) partido else it }
            val sharedPreferences = getSharedPreferences("momios", MODE_PRIVATE)
            val partidosJson = Json.encodeToString(updatedPartidos)
            sharedPreferences.edit().putString("momios", partidosJson).apply()

            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
        }
    }
}