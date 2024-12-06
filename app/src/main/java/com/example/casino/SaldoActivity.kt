package com.example.casino

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.core.content.edit

class SaldoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saldos)

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
    }
}