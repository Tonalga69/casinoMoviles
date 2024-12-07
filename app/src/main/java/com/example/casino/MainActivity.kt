package com.example.casino

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casino.models.Partido
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

    data class Apuesta(
        val partido: Partido,
        val momioSeleccionado: Float,
        val monto: Int
    )

    private lateinit var partidos: List<Partido>
    private var partidoSeleccionado: Partido? = null
    private lateinit var spinnerPartidos: Spinner
    private lateinit var equipo1Label: TextView
    private lateinit var equipo2Label: TextView
    private lateinit var momio1Button: ToggleButton
    private lateinit var momio2Button: ToggleButton
    private lateinit var momio3Button: ToggleButton
    private val apuestas = mutableListOf<Apuesta>()

    private var montoApostado: Int = 0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUpMomios()

        val isAdmin = intent.getBooleanExtra("isAdmin", false);
        val idUsuario = intent.getStringExtra("IdUsuario");

        val sharedPreferences = getSharedPreferences(idUsuario, MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "")
        val saldo = sharedPreferences.getFloat("saldo", 0.0f)

        Toast.makeText(this, "¡Hola, $username! Bienvenido 😊", Toast.LENGTH_SHORT).show()

        // Inicializar vistas
        spinnerPartidos = findViewById(R.id.spinnerPartidos)
        equipo1Label = findViewById(R.id.equipo1)
        equipo2Label = findViewById(R.id.equipo2)
        momio1Button = findViewById(R.id.momio1Button)
        momio2Button = findViewById(R.id.momio2Button)
        momio3Button = findViewById(R.id.momio3Button)

        val textViewSaldo = findViewById<TextView>(R.id.textViewSaldo)
        textViewSaldo.text = saldo.toString()

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

        configurarToggleButtons()
        configurarBotonesMonto()
        configurarBotonEnviarApuesta()
        configurarBotonVerificarApuestas()


        val buttonAdmin = findViewById<Button>(R.id.buttonAdmin)
        if (!isAdmin) {
            buttonAdmin.visibility = View.GONE
        }
        buttonAdmin.setOnClickListener {
            val intent = Intent(this, AdminSaldosActivity::class.java)
            intent.putExtra("IdUsuario", idUsuario)
            intent.putExtra("isAdmin", isAdmin)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

    }

    private fun setUpMomios() {
        val sharedPreferences = getSharedPreferences("momios", MODE_PRIVATE)
        val storedPartidosJson = sharedPreferences.getString("momios", null)

        if (storedPartidosJson.isNullOrEmpty()) {
            // Lista de partidos
            val partidos = listOf(
                Partido(1, "Chivas", "Atlas", 1.2f, 3f, 0.8f),
                Partido(2, "América", "Pumas", 1.5f, 2.8f, 1.0f),
                Partido(3, "Cruz Azul", "Tigres", 2.0f, 1.9f, 1.2f),
                Partido(4, "León", "Santos", 1.3f, 3.5f, 0.9f),
                Partido(5, "Toluca", "Necaxa", 1.8f, 2.4f, 1.1f)
            )

            // Convertir la lista completa a JSON
            val jsonPartidos = Json.encodeToString(ListSerializer(Partido.serializer()), partidos)

            // Guardar la lista como un solo JSON en SharedPreferences
            sharedPreferences.edit {
                putString("momios", jsonPartidos)
            }
        }
    }


    private fun cargarPartidos(): List<Partido> {
        val sharedPreferences = getSharedPreferences("momios", MODE_PRIVATE)
        val partidosJson = sharedPreferences.getString("momios", "[]")
        return Json.decodeFromString(partidosJson ?: "[]")
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarFormulario(partido: Partido) {
        equipo1Label.text = partido.equipo1
        equipo2Label.text = partido.equipo2

        momio1Button.text = partido.momio1.toString()
        momio1Button.textOn = partido.momio1.toString()
        momio1Button.textOff = partido.momio1.toString()

        momio2Button.text = partido.momio2.toString()
        momio2Button.textOn = partido.momio2.toString()
        momio2Button.textOff = partido.momio2.toString()

        momio3Button.text = partido.momio3.toString()
        momio3Button.textOn = partido.momio3.toString()
        momio3Button.textOff = partido.momio3.toString()
    }

    private fun configurarToggleButtons() {
        val momioButtons = listOf(momio1Button, momio2Button, momio3Button)

        momioButtons.forEach { button ->
            button.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    button.setBackgroundColor(resources.getColor(R.color.colorSelected, null))
                    momioButtons.filter { it != button }.forEach {
                        it.isChecked = false
                        it.setBackgroundColor(resources.getColor(R.color.colorDeselected, null))
                    }
                } else {
                    button.setBackgroundColor(resources.getColor(R.color.colorDeselected, null))
                }
            }
        }
    }


    private fun configurarBotonesMonto() {
        val incrementarButton = findViewById<Button>(R.id.buttonIncrementar)
        val decrementarButton = findViewById<Button>(R.id.buttonDecrementar)
        val montoInput = findViewById<TextView>(R.id.montoApostadoInput)

        incrementarButton.setOnClickListener {
            montoApostado += 10
            montoInput.text = montoApostado.toString()
        }

        decrementarButton.setOnClickListener {
            if (montoApostado >= 10) {
                montoApostado -= 10
                montoInput.text = montoApostado.toString()
            }
        }
    }

    private fun configurarBotonEnviarApuesta() {
        val enviarApuestaButton = findViewById<Button>(R.id.buttonEnviarApuesta)

        enviarApuestaButton.setOnClickListener {
            if (partidoSeleccionado == null || montoApostado <= 0) {
                Toast.makeText(this, "Selecciona un partido y monto válido", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val momioSeleccionado = when {
                momio1Button.isChecked -> partidoSeleccionado!!.momio1
                momio2Button.isChecked -> partidoSeleccionado!!.momio2
                momio3Button.isChecked -> partidoSeleccionado!!.momio3
                else -> {
                    Toast.makeText(this, "Selecciona un momio", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            apuestas.add(Apuesta(partidoSeleccionado!!, momioSeleccionado, montoApostado))
            Toast.makeText(this, "Apuesta guardada", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun configurarBotonVerificarApuestas() {
        val verificarButton = findViewById<Button>(R.id.buttonVerificarApuestas)
        val saldoTextView = findViewById<TextView>(R.id.textViewSaldo)
        var saldo = saldoTextView.text.toString().toFloat()
        val montoInput = findViewById<TextView>(R.id.montoApostadoInput)

        verificarButton.setOnClickListener {
            var dineroGanado = 0.0f
            var dineroPerdido = 0.0f
            val resultadosPartidos = ArrayList<String>() // Lista de resultados

            apuestas.forEach { apuesta ->
                val resultadoAleatorio = (1..3).random() // 1: equipo1, 2: empate, 3: equipo2
                val ganador = when (resultadoAleatorio) {
                    1 -> apuesta.partido.equipo1
                    2 -> "Empate"
                    3 -> apuesta.partido.equipo2
                    else -> "Sin resultado"
                }
                resultadosPartidos.add("Partido: ${apuesta.partido.equipo1} vs ${apuesta.partido.equipo2} - Ganador: $ganador")

                val momioGanador = when (resultadoAleatorio) {
                    1 -> apuesta.partido.momio1
                    2 -> apuesta.partido.momio2
                    3 -> apuesta.partido.momio3
                    else -> 1.0f
                }

                if (momioGanador == apuesta.momioSeleccionado) {
                    val ganancia = apuesta.monto * apuesta.momioSeleccionado
                    dineroGanado += ganancia
                    saldo += ganancia
                } else {
                    dineroPerdido += apuesta.monto
                    saldo -= apuesta.monto
                }
            }

            // Limpiar apuestas y restablecer UI
            apuestas.clear()
            saldoTextView.text = saldo.toString()
            montoApostado = 0
            montoInput.text = montoApostado.toString()

            // Deseleccionar momios
            momio1Button.isChecked = false
            momio2Button.isChecked = false
            momio3Button.isChecked = false

            // Mandar datos a ResultadosActivity
            val intent = Intent(this, ResultadosActivity::class.java)
            intent.putExtra("dineroGanado", dineroGanado)
            intent.putExtra("dineroPerdido", dineroPerdido)
            intent.putExtra("saldo", saldo)
            intent.putStringArrayListExtra("resultadosPartidos", resultadosPartidos)
            startActivity(intent)
        }
    }


}