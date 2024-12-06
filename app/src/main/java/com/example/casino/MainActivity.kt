package com.example.casino

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casino.models.Partido
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {

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

    private fun getStoredMomios(): List<Partido> {
        val sharedPreferences = getSharedPreferences("momios", MODE_PRIVATE)
        val storedPartidosJson = sharedPreferences.getString("momios", null)

        return if (!storedPartidosJson.isNullOrEmpty()) {
            Json.decodeFromString(ListSerializer(Partido.serializer()), storedPartidosJson)
        } else {
            emptyList()
        }
    }

}