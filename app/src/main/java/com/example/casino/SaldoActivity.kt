package com.example.casino

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SaldoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_saldos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val isAdmin = intent.getBooleanExtra("isAdmin", false)
        val idUsuario = intent.getStringExtra("IdUsuario")

        if (idUsuario != null) {
            val sharedPreferences = getSharedPreferences(idUsuario, MODE_PRIVATE)

            val saldo = sharedPreferences.getFloat("saldo", 0.0f)
            println("Saldo: ${saldo.toString()}")
            sharedPreferences.edit().putFloat("saldo", saldo).apply()
            val saldoTextView = findViewById<TextView>(R.id.tvSaldo)
            saldoTextView.text = saldo.toString()
        } else {
            finish()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_saldo


        // Boton de historial Test
        val buttonHistorial = findViewById<Button>(R.id.buttonHistorial)
        buttonHistorial.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            intent.putExtra("IdUsuario", idUsuario)
            startActivity(intent)
        }

        bottomNavigationView.selectedItemId = R.id.nav_saldo
        // Configura la navegación
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("IdUsuario", idUsuario)
                    intent.putExtra("isAdmin", isAdmin)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
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

                    bottomNavigationView.selectedItemId = R.id.nav_saldo
                    true
                }

                else -> false
            }
        }
        val btnMinusBet = findViewById<Button>(R.id.btnMinusBet3)
        val btnPlusBet = findViewById<Button>(R.id.btnPlusBet3)
        val editTextBet3 = findViewById<EditText>(R.id.editTextBet3)

        fun updateBetValue(editText: EditText, increment: Int) {
            val currentBet = editText.text.toString().toIntOrNull()
                ?: 0 // Valor actual, 0 si está vacío o inválido
            val newBet = (currentBet + increment).coerceAtLeast(0) // Evita valores negativos
            editText.setText(newBet.toString())
        }

        btnMinusBet.setOnClickListener {
            updateBetValue(editTextBet3, -10) // Decrementa en 10
        }

        btnPlusBet.setOnClickListener {
            updateBetValue(editTextBet3, 10) // Incrementa en 10
        }
        // Configurar SharedPreferences para el usuario
        val sharedPreferences = getSharedPreferences(idUsuario, MODE_PRIVATE)
        var saldo = sharedPreferences.getFloat("saldo", 0.0f) // Recuperar saldo inicial
        val saldoTextView = findViewById<TextView>(R.id.tvSaldo)
        saldoTextView.text = saldo.toString()

        // Referencias a las vistas
        val btnAplicar = findViewById<Button>(R.id.button)
        findViewById<EditText>(R.id.editTextBet3)

        // Evento para aplicar el incremento al saldo
        btnAplicar.setOnClickListener {
            val betText = editTextBet3.text.toString()
            if (betText.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa una cantidad válida.", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            // Convertir la cantidad ingresada a Float
            val amountToAdd = betText.toFloatOrNull()
            if (amountToAdd == null || amountToAdd <= 0) {
                Toast.makeText(this, "Ingresa una cantidad válida.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Actualizar saldo
            saldo += amountToAdd
            saldoTextView.text = saldo.toString()
            sharedPreferences.edit().putFloat("saldo", saldo).apply() // Guardar saldo actualizado

            Toast.makeText(this, "Saldo actualizado. Nuevo saldo: $saldo", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

