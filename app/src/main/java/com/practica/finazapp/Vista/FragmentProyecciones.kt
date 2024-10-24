package com.practica.finazapp.Vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.AlertaViewModel
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentProyeccionesBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FragmentProyecciones : Fragment() {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var ingresoViewModel: IngresoViewModel
    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var alertaViewModel: AlertaViewModel

    private var _binding: FragmentProyeccionesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding = FragmentProyeccionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            this.usuarioId = usuarioId
            MostrarRecomendaciones()
        }
    }

    private fun  MostrarRecomendaciones(){

        gastosViewModel.getGastoMasAlto(usuarioId).observe(viewLifecycleOwner) { gastoMasAlto ->

            gastoMasAlto?.let {
                // Asegúrate de tener el TextView del CardView que mostrará el gasto más alto
                val textViewGastoMasAlto = view?.findViewById<TextView>(R.id.textViewGastoMasAlto)

                // Crea el mensaje con los datos del gasto
                val mensaje = "El gasto más alto es '${it.descripcion}' de la categoría '${it.categoria}' con un valor de \$${it.valor}. Te recomendamos reducir ese gasto significativamente."

                // Actualiza el TextView con el mensaje
                textViewGastoMasAlto?.text = mensaje
            }
        }

        gastosViewModel.getGastoMasBajo(usuarioId).observe(viewLifecycleOwner){ gastoMasBajo ->
            gastoMasBajo?.let {

            val textViewGastoMasBajo = view?.findViewById<TextView>(R.id.textViewGastoMasBajo)
            val mensaje = "El gasto más bajo es '${it.descripcion}' de la categoría '${it.categoria}' con un valor de \$${it.valor}. Te recomendamos que sigas así de juicioso."
            textViewGastoMasBajo?.text = mensaje

            }

        }

        gastosViewModel.getPromedioGastosMes(usuarioId).observe(viewLifecycleOwner) { promedio ->
            promedio?.let {
                val textViewGastoPromedio = view?.findViewById<TextView>(R.id.textpromedio)
                val mesActual = obtenerMesActual()
                val mensaje = "El promedio de gastos del mes de $mesActual es de \$${String.format("%.2f", it)}"
                textViewGastoPromedio?.text = mensaje
            }
        }

        gastosViewModel.getGastosRecurrentes(usuarioId).observe(viewLifecycleOwner) { recurrentes ->
            recurrentes?.let {
                val textViewGastoRecurrente = view?.findViewById<TextView>(R.id.textRecurrente)

                if (recurrentes.isNotEmpty()) {
                    val stringBuilder = StringBuilder()

                    recurrentes.forEach { gasto ->
                        stringBuilder.append("Descripción: ${gasto.descripcion}, Valor: \$${gasto.valor}, Fecha: ${gasto.fecha}\n")
                    }

                    textViewGastoRecurrente?.text = stringBuilder.toString()
                } else {
                    textViewGastoRecurrente?.text = "No hay gastos recurrentes este mes."
                }
            }
        }

    }

    fun obtenerMesActual(): String {
        val formatoMes = SimpleDateFormat("MMMM", Locale.getDefault())
        return formatoMes.format(Date())
    }

}

