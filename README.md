# Casino Fortuna

Este repositorio es una demostración del uso de **`kotlinx.serialization`** para la serialización y deserialización de datos en formato JSON, utilizando **`SharedPreferences`** como almacenamiento local en una aplicación Android.  

## Estructura del proyecto

```
root
├── Models          // Clases definidas según el diagrama UML
├── MainActivity    // Lógica principal de la aplicación
```

## Uso de Shared Preferences

### Almacenamiento y recuperación de datos de usuarios

El sistema utiliza **`SharedPreferences`** para guardar y recuperar datos de usuarios en formato JSON. A continuación, se describe el flujo principal:

1. **Inicialización de SharedPreferences**  
   ```kotlin
   val sharedPreferences: SharedPreferences = context.getSharedPreferences("Usuarios", Context.MODE_PRIVATE)
   ```

2. **Obtención de datos**  
   Los datos se almacenan como un conjunto de cadenas (`Set<String>`) en formato JSON:
   ```kotlin
   val users = sharedPreferences.getStringSet("usuarios", mutableSetOf())
   ```

3. **Conversión de JSON a objetos**  
   Cada cadena JSON se convierte en una instancia de la clase `UsuarioLoginData`. Esta clase contiene los siguientes atributos:
   - `username`: Nombre de usuario.
   - `password`: Contraseña.
   - `idUsuario`: identificador.
   Ejemplo de deserialización:
   ```kotlin
   for (user in users) {
       val usuario = Json.decodeFromString<UsuarioLoginData>(user)
       println(usuario)
   }
   ```

### Almacenamiento y recuperación de datos internos de usuarios

Se gestiona información interna específica para cada usuario mediante un identificador único (`IdUsuario`):

1. **Inicialización de SharedPreferences**  
   ```kotlin
   val sharedPreferences: SharedPreferences = context.getSharedPreferences(IdUsuario, Context.MODE_PRIVATE)
   ```

2. **Obtención de datos**  
   Los datos internos se almacenan como un conjunto de cadenas (`Set<String>`) en formato JSON:
   ```kotlin

   val IDusuario = sharedPreferences.getString("IDusuario", "")
    val nombre = sharedPreferences.getString("nombre", "")
    val contraseña = sharedPreferences.getString("contraseña", "")
    val idioma = sharedPreferences.getString("idioma", "")
    val saldo = sharedPreferences.getFloat("saldo", 0.0f)
    val esAdmin = sharedPreferences.getBoolean("esAdmin", false)
   val historial = sharedPreferences.getStringSet("historial", mutableSetOf())
   ```

3. **Conversión de JSON a objetos**  
   Cada cadena JSON se convierte en una instancia de la clase `Historial`.  
   Ejemplo de deserialización:
   ```kotlin
   for (item in historial) {
       val historial = Json.decodeFromString<Historial>(item)
       println(historial)
   }
   ```

## Tecnologías y bibliotecas utilizadas

- [Kotlin](https://kotlinlang.org/)  
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)  
- **SharedPreferences**: Almacenamiento de datos persistente en Android.

## Referencias

- [Documentación oficial de kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
- [Documentación oficial de SharedPreferences en Android](https://developer.android.com/training/data-storage/shared-preferences)

---
