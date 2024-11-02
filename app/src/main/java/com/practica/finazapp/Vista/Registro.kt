package com.practica.finazapp.Vista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.practica.finazapp.R
import com.practica.finazapp.Entidades.Usuario
import com.practica.finazapp.ViewModel.UsuarioViewModel
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.DAOS.AppDatabase
import com.practica.finazapp.Entidades.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import androidx.appcompat.app.AlertDialog


class Registro : AppCompatActivity() {

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        // Inicializar el ViewModel
        usuarioViewModel = ViewModelProvider(this).get(UsuarioViewModel::class.java)
        db = AppDatabase.getDatabase(applicationContext)

        val btnVolver = findViewById<TextView>(R.id.btnVolver)
        val txtInputContrasena2 = findViewById<TextInputEditText>(R.id.txtinputContrasena2)
        val txtInputConf = findViewById<TextInputEditText>(R.id.txtinputConf)
        val txtInputNombres = findViewById<TextInputEditText>(R.id.txtinputNombres)
        val txtInputApellidos = findViewById<TextInputEditText>(R.id.txtinputApellidos)
        val txtInputUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val btnRegistro1 = findViewById<Button>(R.id.btnRegistro)
        val txtAdvertencia = findViewById<TextView>(R.id.Advertencia)

        // Expresión regular para validar la contraseña
        val regex =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}\$")

        mostrarDialogoNombres()
        mostrarDialogoApellidos()

        btnRegistro1.setOnClickListener {
            val nombres = txtInputNombres.text.toString()
            val apellidos = txtInputApellidos.text.toString()
            val usuario = txtInputUsuario.text.toString()
            val contrasena = txtInputContrasena2.text.toString()

            // Verificar que todos los campos estén llenos
            if (nombres.isEmpty() || apellidos.isEmpty() || usuario.isEmpty() || contrasena.isEmpty()) {
                txtAdvertencia.text = getString(R.string.todos_los_campos_son_obligatorios)
                return@setOnClickListener
            }

            // Verificar que las contraseñas coincidan
            if (contrasena != txtInputConf.text.toString()) {
                txtAdvertencia.text = getString(R.string.las_contrase_as_no_coinciden)
                return@setOnClickListener
            }

            // Verificar que la contraseña cumpla con los requisitos
            if (!regex.matches(contrasena)) {
                txtAdvertencia.text = getString(R.string.requerimientos_cont)
                return@setOnClickListener
            }

            // Crear el nuevo usuario
            val nuevoUsuario = Usuario(
                nombres = nombres,
                apellidos = apellidos,
                usuario = usuario,
                contrasena = contrasena,
                correo = "",
                telefono = "",
                documento = ""
            )



            insertarUsuario(nuevoUsuario)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }


    }

    // AlertDialog para los Nombres
    private fun mostrarDialogoNombres() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Uso de los Nombres")
        builder.setMessage("Tus nombres serán usados de forma segura en la aplicación únicamente para identificarte y mejorar tu experiencia de usuario.")
        builder.setPositiveButton("Entendido") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    // AlertDialog para los Apellidos
    private fun mostrarDialogoApellidos() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Uso de los Apellidos")
        builder.setMessage("Tus apellidos serán usados de forma segura en la aplicación únicamente para fines de personalización y gestión de tu cuenta.")
        builder.setPositiveButton("Entendido") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }




    private fun insertarUsuario(usuario: Usuario) {
        val txtAdvertencia = findViewById<TextView>(R.id.Advertencia)
        Log.d("RegistroActivity", "iniciando insercion")

        // Encriptar la contraseña con bcrypt
        val contrasenaEncriptada = BCrypt.hashpw(usuario.contrasena, BCrypt.gensalt())

        // Crear un nuevo objeto Usuario con la contraseña encriptada
        val usuarioConContrasenaEncriptada = usuario.copy(contrasena = contrasenaEncriptada)

        usuarioViewModel.getUsuarioPorUsuario(usuario.usuario).observe(this) { usuarioExistente ->
            if (usuarioExistente != null) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(2000L) // Esperar la cantidad de tiempo especificada
                    txtAdvertencia.text = getString(R.string.error_usuario_duplicado)
                    Log.d("RegistroActivity", "Usuario duplicado")
                }
            } else {
                Log.d(
                    "RegistroActivity",
                    "Insertando nuevo usuario: $usuarioConContrasenaEncriptada"
                )
                // Usuario no existe en la base de datos, insertarlo
                lifecycleScope.launch {
                    usuarioViewModel.insertUsuario(usuarioConContrasenaEncriptada)
                    Log.d("RegistroActivity", "Usuario insertado correctamente")
                }
                // Observa el LiveData del último ID de usuario
                usuarioViewModel.getUltimoUsuarioId().observe(this) { ultimoUsuarioId ->
                    if (ultimoUsuarioId != null) {


                        // Guardar la sesión en la base de datos
                        lifecycleScope.launch {
                            // Eliminar cualquier sesión existente
                            db.sessionDao().deleteSession()

                            // Insertar la nueva sesión
                            val session = Session(userId = ultimoUsuarioId)
                            db.sessionDao().insert(session)
                        }
                        // Redirigir a la actividad Dashboard con el ID del usuario
                        val intent = Intent(this@Registro, MainActivity2::class.java)
                        intent.putExtra("usuario_id", ultimoUsuarioId)
                        startActivity(intent)
                    } else {
                        Log.d("error", "error con el id del usuario")
                    }
                }
            }
        }
    }

}