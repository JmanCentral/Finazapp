package com.practica.finazapp.Vista


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.practica.finazapp.R

class FragmentAlertas : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragmento
        return inflater.inflate(R.layout.fragment_alertas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Vincular el botón usando su ID
        val btnAlertaNueva: View = view.findViewById(R.id.btn_alerta_nueva)
        val botonCirculo: ImageButton = view.findViewById(R.id.botonCirculo)

        // Configurar el listener para el botón completo (ConstraintLayout)
        btnAlertaNueva.setOnClickListener {
            // Acción a realizar cuando se hace clic en el contenedor del botón
            Toast.makeText(requireContext(), "Botón de Alerta Nueva clickeado (ConstraintLayout)", Toast.LENGTH_SHORT).show()
        }

        // Configurar el listener específicamente para el ImageButton (opcional)
        botonCirculo.setOnClickListener {
            // Acción a realizar cuando se hace clic en el ImageButton
            Toast.makeText(requireContext(), "Botón de Alerta Nueva clickeado (ImageButton)", Toast.LENGTH_SHORT).show()
        }

        // Opcional: Configurar el listener para el TextView si también es interactivo
        val textoNuevoIngMes: View = view.findViewById(R.id.textoNuevoIngMes)
        textoNuevoIngMes.setOnClickListener {
            // Acción a realizar cuando se hace clic en el TextView
            Toast.makeText(requireContext(), "Texto de Alerta Nueva clickeado", Toast.LENGTH_SHORT).show()
        }
    }
}

