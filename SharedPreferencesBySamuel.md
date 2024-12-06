Ola

Al inicio de cada activity hay que obtener la info del usuario actual

```kotlin
val isAdmin = intent.getBooleanExtra("isAdmin",false);
val idUsuario = intent.getStringExtra("IdUsuario");
```

Despues se genera la instancia de SharedPreferences

```kotlin
val sharedPreferences = getSharedPreferences(idUsuario, MODE_PRIVATE)
```

Despues ya podemos declaran en variables cada una de sus propiedades para usarla(solo lectura)

```kotlin
val username = sharedPreferences.getString("username", "")
val password = sharedPreferences.getString("password", "")
val idioma = sharedPreferences.getString("idioma","")
val saldo = sharedPreferences.getFloat("saldo",0.0f)
val esAdmin = sharedPreferences.getBoolean("esAdmin",false)
val historial = sharedPreferences.getStringSet("historial",mutableSetOf())
```

**Modificar info de usuario**

```kotlin
sharedPreferences.edit { 
    putFloat("saldo", 300f)
}
```

