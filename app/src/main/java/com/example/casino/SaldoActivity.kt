package com.example.casino

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
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
    }
}