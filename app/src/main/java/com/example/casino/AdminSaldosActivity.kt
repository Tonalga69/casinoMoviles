package com.example.casino

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdminSaldosActivity : AppCompatActivity() {
    lateinit var editTextTragamonedas : EditText
    lateinit var buttonAplicar : Button
    lateinit var buttonMomios : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_saldos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editTextTragamonedas = findViewById(R.id.editTextTragamonedas)
        buttonAplicar = findViewById(R.id.buttonAplicar)
        buttonMomios = findViewById(R.id.buttonMomios)
        val sharedPreferences = getSharedPreferences("Default", Context.MODE_PRIVATE)
        val probabilidadTragamonedas= sharedPreferences.getFloat("tragamonedas", 50.0f)
        editTextTragamonedas.setText(probabilidadTragamonedas.toString())
        buttonAplicar.setOnClickListener {
            if(editTextTragamonedas.text.toString().isEmpty()){
                return@setOnClickListener
            }
            val probabilidad = editTextTragamonedas.text.toString().toFloat()
            sharedPreferences.edit().putFloat("tragamonedas", probabilidad).apply()
            Toast.makeText(this, getString(R.string.updated_prob), Toast.LENGTH_SHORT).show()

        }
        
        buttonMomios.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}