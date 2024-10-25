package com.practica.finazapp.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.UsuarioViewModel
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.DAOS.AppDatabase
import com.practica.finazapp.Entidades.Session
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class Login : AppCompatActivity() {

    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar ViewModel y Base de Datos
        usuarioViewModel = ViewModelProvider(this).get(UsuarioViewModel::class.java)
        db = AppDatabase.getDatabase(applicationContext)

        // Referencias a los elementos de la interfaz
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)
        val txtUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val txtContrasena = findViewById<TextInputEditText>(R.id.txtinputContrasena)
        val txtAdvertencia = findViewById<TextView>(R.id.txtAdvertenciaLogin)

        btnIngresar.setOnClickListener {
            val usuarioInput = txtUsuario.text.toString()
            val contrasenaInput = txtContrasena.text.toString()

            usuarioViewModel.getUsuarioPorUsuario(usuarioInput).observe(this) { usuario ->
                if (usuario == null) {
                    txtAdvertencia.text = getString(R.string.el_usuario_no_existe)
                } else {
                    if (!BCrypt.checkpw(contrasenaInput, usuario.contrasena)) {
                        txtAdvertencia.text = getString(R.string.la_contrase_a_no_es_correcta_o_no_coincide)
                    } else {
                        val usuarioId = usuario.id

                        // Guardar la sesión en la base de datos
                        lifecycleScope.launch {
                            // Eliminar cualquier sesión existente
                            db.sessionDao().deleteSession()

                            // Insertar la nueva sesión
                            val session = Session(userId = usuarioId)
                            db.sessionDao().insert(session)
                        }

                        // Navegar al Dashboard
                        val intent = Intent(this, Dashboard::class.java)
                        intent.putExtra("usuario_id", usuarioId)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}
