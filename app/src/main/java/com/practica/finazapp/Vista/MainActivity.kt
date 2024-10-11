package com.practica.finazapp.Vista

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.practica.finazapp.DAOS.AppDatabase
import com.practica.finazapp.R
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Crear el canal de notificaciones
        crearCanalNotificacion(this)

        // Inicializar la base de datos
        db = AppDatabase.getDatabase(applicationContext)

        // Verificar si hay una sesión activa
        lifecycleScope.launch {
            val session = db.sessionDao().getSession()
            if (session != null) {
                // Existe una sesión activa, navegar al Dashboard
                val intent = Intent(this@MainActivity, Dashboard::class.java)
                intent.putExtra("usuario_id", session.userId)
                startActivity(intent)
                finish()
            } else {
                // No hay sesión, permanecer en la pantalla de bienvenida
                // Opcional: puedes agregar animaciones o retrasos aquí
            }
        }

        // Configurar el botón de login
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            this.finish()
        }


    }

    fun crearCanalNotificacion(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "CanalGastosAltos"
            val descripcion = "Notificaciones para gastos altos"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel("GASTOS_ALTOS", nombre, importancia)
            canal.description = descripcion

            val notificationManager: NotificationManager =
                context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}