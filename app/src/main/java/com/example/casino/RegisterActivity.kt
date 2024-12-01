package com.example.casino

import android.content.Intent
import android.os.Bundle
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

class RegisterActivity : AppCompatActivity() {
    lateinit var nameEditText : EditText
    lateinit var passwordEditText : EditText
    lateinit var cleanButton: Button
    lateinit var registerButton: Button
    lateinit var goToLoginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        nameEditText = findViewById(R.id.editTextUserName)
        passwordEditText = findViewById(R.id.editTextPassword)
        cleanButton = findViewById(R.id.buttonClean)
        registerButton = findViewById(R.id.buttonRegister)
        goToLoginButton = findViewById(R.id.buttonGoToLogin)
        cleanButton.setOnClickListener {
            nameEditText.setText("")
            passwordEditText.setText("")
        }
        registerButton.setOnClickListener {
            register()
        }



    }

    private fun register() {
        if(nameEditText.text.toString().isNotEmpty() && passwordEditText.text.toString().isNotEmpty()) {
            val sharedPreferences = getSharedPreferences("default", MODE_PRIVATE)
            val usuarios = sharedPreferences.getStringSet("usuarios", mutableSetOf())!!
            val usuario = usuarios.find {
                val usuarioLoginData = Json.decodeFromString(UsuarioLoginData.serializer(), it)
                usuarioLoginData.userName == nameEditText.text.toString()
            }
            if(usuario!=null){
                Toast.makeText(this, getString(R.string.userAlreadyRegistered), Toast.LENGTH_SHORT).show()
                return;
            }
            val usuarioLoginData = UsuarioLoginData(nameEditText.text.toString(), passwordEditText.text.toString(), (usuarios.size+1).toString(), false)
            val usuariosMutable = usuarios.toMutableSet()
            usuariosMutable.add(Json.encodeToString(UsuarioLoginData.serializer(), usuarioLoginData))
            sharedPreferences.edit {
                putStringSet("usuarios", usuariosMutable)
            }
            val userSharePrefences= getSharedPreferences(usuarioLoginData.idUsuario, MODE_PRIVATE)
            userSharePrefences.edit {
                putString("Idusuario", usuarioLoginData.idUsuario)
                putString("nombre", usuarioLoginData.userName)
                putString("idioma", "es")
                putFloat("saldo", 0f)
                putBoolean("isAdmin", false)
                putString("password", usuarioLoginData.password)
                putStringSet("historial", mutableSetOf())
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("IdUsuario", usuarioLoginData.idUsuario)
            intent.putExtra("isAdmin", usuarioLoginData.isAdmin)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)


        } else {
            Toast.makeText(this, getString(R.string.fillAllFields), Toast.LENGTH_SHORT).show()
        }
    }
}