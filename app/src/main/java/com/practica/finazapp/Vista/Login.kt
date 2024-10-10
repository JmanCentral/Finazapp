package com.practica.finazapp.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.UsuarioViewModel
import com.google.android.material.textfield.TextInputEditText
import org.mindrot.jbcrypt.BCrypt

class Login : AppCompatActivity() {

    private lateinit var usuarioViewModel: UsuarioViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        usuarioViewModel = ViewModelProvider(this).get(UsuarioViewModel::class.java)

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnRegistrarse = findViewById<TextView>(R.id.btnRegistrarse)
        val txtUsuario = findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val txtContrasena = findViewById<TextInputEditText>(R.id.txtinputContrasena)
        val txtAdvertencia = findViewById<TextView>(R.id.txtAdvertenciaLogin)

        btnIngresar.setOnClickListener {
            usuarioViewModel.getUsuarioPorUsuario(txtUsuario.text.toString()).observe(this) { usuario ->
                if (usuario == null) {
                    txtAdvertencia.text = getString(R.string.el_usuario_no_existe)
                } else {

                    if (!BCrypt.checkpw(txtContrasena.text.toString(), usuario.contrasena)) {
                        txtAdvertencia.text = getString(R.string.la_contrase_a_no_es_correcta_o_no_coincide)
                    } else {
                        val usuarioId = usuario.id
                        val intent = Intent(this, Dashboard::class.java)
                        intent.putExtra("usuario_id", usuarioId)
                        startActivity(intent)
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