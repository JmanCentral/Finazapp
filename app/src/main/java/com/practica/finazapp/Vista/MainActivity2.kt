package com.practica.finazapp.Vista

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.ViewModel.SharedViewModel

class MainActivity2 : AppCompatActivity() {

    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var ingresoViewModel: IngresoViewModel
    private var usuarioId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)

        // Recibir el ID del usuario desde el intent
        usuarioId = intent.getLongExtra("usuario_id", -1)


        // Inicializar ViewModels
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        ingresoViewModel = ViewModelProvider(this)[IngresoViewModel::class.java]

        // Almacenar el ID del usuario en el SharedViewModel
        sharedViewModel.setUsuarioId(usuarioId)

        val btnAhorrar = findViewById<Button>(R.id.my_button)

        // Crear una instancia del fragmento
        val miFragment = Ingresos()

        // Agregar el fragmento al contenedor si no está ya agregado
        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, miFragment)
                .commit()
        }

        var continuarAHorro = false // Bandera para controlar la redirección

        btnAhorrar.setOnClickListener {
            ingresoViewModel.getIngTotalDeEsteMes(usuarioId).observe(this) { totalIngresos ->
                if (totalIngresos != null && totalIngresos > 0) {
                    if (continuarAHorro) {
                        // Redirigir si el usuario ya decidió continuar con la configuración de ahorro
                        val intent = Intent(this, Categorias::class.java)
                        intent.putExtra("usuario_id", usuarioId)
                        startActivity(intent)
                    } else {
                        // Preguntar si el usuario quiere continuar o seguir ingresando más valores
                        AlertDialog.Builder(this)
                            .setTitle("Acción requerida")
                            .setMessage("Ya tienes ingresos. ¿Quieres continuar con el ahorro o agregar más ingresos?")
                            .setPositiveButton("Continuar") { dialog, _ ->
                                continuarAHorro = true // Cambiar la bandera para que al próximo click redirija
                                btnAhorrar.performClick() // Volver a hacer clic en el botón para redirigir
                            }
                            .setNegativeButton("Agregar más ingresos") { dialog, _ ->
                                dialog.dismiss() // Cerrar el diálogo y permitir seguir ingresando valores
                            }
                            .show()
                    }
                } else {
                    // Si no hay ingresos válidos, mostrar el AlertDialog
                    AlertDialog.Builder(this)
                        .setTitle("Información")
                        .setMessage("Debes ingresar el dinero para poder establecer tu ahorro y crear las categorías.")
                        .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
                        .show()

                }
            }
        }

    }

    @Override
    override fun onBackPressed() {

        // No hacemos nada aquí para evitar que el usuario regrese a la actividad anterior
    }


}