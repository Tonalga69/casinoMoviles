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
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private lateinit var idUsuario: String
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


        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        idUsuario = intent.getStringExtra("IdUsuario") ?: ""

        if (idUsuario.isEmpty()) {
            val defaultSharedPreferences = getSharedPreferences("default", MODE_PRIVATE)
            idUsuario = defaultSharedPreferences.getString("currentUserId", "") ?: ""
        }


        val sharedPreferences = getSharedPreferences(idUsuario, MODE_PRIVATE)
        val username = sharedPreferences.getString("nombre", "")
        val saldo = sharedPreferences.getFloat("saldo", 0.0f)

        Toast.makeText(this, getString(R.string.hola_bienvenido, username), Toast.LENGTH_SHORT)
            .show()

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


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home
        // Configura la navegación
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Permanece en MainActivity
                    bottomNavigationView.selectedItemId = R.id.nav_home
                    true
                }

                R.id.nav_apuestas -> {
                    val intent = Intent(this, RouletteActivity::class.java)
                    intent.putExtra("IdUsuario", idUsuario)
                    intent.putExtra("isAdmin", isAdmin)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

                    true
                }

                R.id.nav_saldo -> {
                    val intent = Intent(this, SaldoActivity::class.java)
                    intent.putExtra("IdUsuario", idUsuario)
                    intent.putExtra("isAdmin", isAdmin)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

                    true
                }

                else -> false
            }
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

    @SuppressLint("SetTextI18n")
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
        val saldo = findViewById<TextView>(R.id.textViewSaldo).text.toString().toFloat()
        enviarApuestaButton.setOnClickListener {
            if (partidoSeleccionado == null || montoApostado <= 0) {
                Toast.makeText(
                    this,
                    getString(R.string.selecciona_un_partido_y_monto_v_lido), Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            val momioSeleccionado = when {
                momio1Button.isChecked -> partidoSeleccionado!!.momio1
                momio2Button.isChecked -> partidoSeleccionado!!.momio2
                momio3Button.isChecked -> partidoSeleccionado!!.momio3
                else -> {
                    Toast.makeText(
                        this,
                        getString(R.string.selecciona_un_momio),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            if (apuestas.fold(0) { acc, apuesta -> acc + apuesta.monto } > saldo) {
                Toast.makeText(
                    this,
                    getString(R.string.no_tienes_saldo_suficiente), Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            apuestas.add(Apuesta(partidoSeleccionado!!, momioSeleccionado, montoApostado))
            Toast.makeText(this, getString(R.string.apuesta_guardada), Toast.LENGTH_SHORT).show()
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
                    2 -> getString(R.string.empate)
                    3 -> apuesta.partido.equipo2
                    else -> getString(R.string.sin_resultado)
                }
                resultadosPartidos.add(
                    getString(
                        R.string.partido_vs_ganador,
                        apuesta.partido.equipo1,
                        apuesta.partido.equipo2,
                        ganador
                    )
                )

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


            // Guardar Saldo
            val sharedPreferences = getSharedPreferences(idUsuario, MODE_PRIVATE)
            sharedPreferences.edit {
                putFloat("saldo", saldo)
            }

            // Deseleccionar momios
            momio1Button.isChecked = false
            momio2Button.isChecked = false
            momio3Button.isChecked = false

            // Mandar datos a ResultadosActivity
            val intent = Intent(this, ResultadosActivity::class.java)
            intent.putExtra("idUser", idUsuario)
            intent.putExtra("dineroGanado", dineroGanado)
            intent.putExtra("dineroPerdido", dineroPerdido)
            intent.putExtra("saldo", saldo)
            intent.putStringArrayListExtra("resultadosPartidos", resultadosPartidos)
            startActivity(intent)
        }
    }


}