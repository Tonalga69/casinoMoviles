package com.example.casino

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.casino.models.UsuarioLoginData
import kotlinx.serialization.json.Json
import java.util.Locale

class LoginActivity : AppCompatActivity() {
    lateinit var nameEditText : EditText
    lateinit var passwordEditText : EditText
    lateinit var cleanButton: Button
    lateinit var loginButton: Button
    lateinit var goToRegisterButton: Button
    lateinit var changeLanguageButton: Button
    val usuarioLoginData = UsuarioLoginData("admin", "admin", "1", true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setUpAdmin()
        nameEditText = findViewById(R.id.editTextUserName)
        passwordEditText = findViewById(R.id.editTextPassword)
        cleanButton = findViewById(R.id.buttonClean)
        loginButton = findViewById(R.id.buttonLogin)
        goToRegisterButton = findViewById(R.id.buttonRegister)
        cleanButton.setOnClickListener {
            nameEditText.setText("")
            passwordEditText.setText("")
        }
        loginButton.setOnClickListener(login())
        goToRegisterButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        changeLanguageButton = findViewById(R.id.changeLanguageButton)
        changeLanguageButton.setOnClickListener {

            val sharedPreferences = getSharedPreferences("default", MODE_PRIVATE)
            val idioma = sharedPreferences.getString("idioma", "es")
            if (idioma == "en") {
                sharedPreferences.edit {
                    putString("idioma", "es")
                }
                setLocale(this, "es")
            } else {
                sharedPreferences.edit {
                    putString("idioma", "en")
                }
                setLocale(this, "en")
            }
            Toast.makeText(this, sharedPreferences.getString("idioma", "es"), Toast.LENGTH_SHORT).show()



        }
    }


    private fun login(): OnClickListener {
        return OnClickListener {
            val sharedPreferences = getSharedPreferences("default", MODE_PRIVATE)
            val usuarios = sharedPreferences.getStringSet("usuarios", mutableSetOf())!!
            val usuario = usuarios.find {
                val usuarioLoginData = Json.decodeFromString(UsuarioLoginData.serializer(), it)
                usuarioLoginData.userName == nameEditText.text.toString() && usuarioLoginData.password == passwordEditText.text.toString()
            }

            if (usuario != null) {////
                val user = Json.decodeFromString(UsuarioLoginData.serializer(), usuario)
                val intent = Intent(this, MainActivity::class.java)
                sharedPreferences.edit {
                    putString("currentUserId", user.idUsuario)
                }
                intent.putExtra("IdUsuario", user.idUsuario)
                intent.putExtra("isAdmin", user.isAdmin)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                return@OnClickListener
            }
            Toast.makeText(this, getString(R.string.userOrPasswordIncorrect), Toast.LENGTH_SHORT).show()
        }
    }


    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }





    private fun setUpAdmin(){
        val sharedPreferences = getSharedPreferences("default", MODE_PRIVATE)
        if (sharedPreferences.getStringSet("usuarios", mutableSetOf())!!.isEmpty()){
            val stringJson= Json.encodeToString(UsuarioLoginData.serializer(), usuarioLoginData)
            sharedPreferences.edit {
                putStringSet("usuarios", mutableSetOf(stringJson))
            }
            val userSharePrefences= getSharedPreferences(usuarioLoginData.idUsuario, MODE_PRIVATE)
            userSharePrefences.edit {
                putString("Idusuario", usuarioLoginData.idUsuario)
                putString("nombre", usuarioLoginData.userName)
                putString("idioma", "es")
                putFloat("saldo", 1000f)
                putBoolean("isAdmin", usuarioLoginData.isAdmin)
                putString("password", usuarioLoginData.password)
                putStringSet("historial", mutableSetOf())
            }

        }

    }
}