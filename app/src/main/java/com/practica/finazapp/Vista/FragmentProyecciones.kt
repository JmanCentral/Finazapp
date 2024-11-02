package com.practica.finazapp.Vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
    private lateinit var ingresosViewModel: IngresoViewModel
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
        ingresosViewModel = ViewModelProvider(this)[IngresoViewModel::class.java]
        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            this.usuarioId = usuarioId
            MostrarRecomendaciones()
        }
    }

    private fun MostrarRecomendaciones() {

        gastosViewModel.getGastoMasAlto(usuarioId).observe(viewLifecycleOwner) { gastoMasAlto ->

            gastoMasAlto?.let {
                // Asegúrate de tener el TextView del CardView que mostrará el gasto más alto
                val textViewGastoMasAlto = view?.findViewById<TextView>(R.id.textViewGastoMasAlto)

                // Crea el mensaje con los datos del gasto
                val mensaje =
                    "El gasto más alto es '${it.descripcion}' de la categoría '${it.categoria}' con un valor de \$${it.valor}. Te recomendamos reducir ese gasto significativamente."

                // Actualiza el TextView con el mensaje
                textViewGastoMasAlto?.text = mensaje
            }
        }

        gastosViewModel.getGastoMasBajo(usuarioId).observe(viewLifecycleOwner) { gastoMasBajo ->
            gastoMasBajo?.let {

                val textViewGastoMasBajo = view?.findViewById<TextView>(R.id.textViewGastoMasBajo)
                val mensaje =
                    "El gasto más bajo es '${it.descripcion}' de la categoría '${it.categoria}' con un valor de \$${it.valor}. Te recomendamos que sigas así de juicioso."
                textViewGastoMasBajo?.text = mensaje

            }

        }

        gastosViewModel.getPromedioGastosMes(usuarioId).observe(viewLifecycleOwner) { promedio ->
            promedio?.let {
                val textViewGastoPromedio = view?.findViewById<TextView>(R.id.textpromedio)
                val mesActual = obtenerMesActual()
                val mensaje = "El promedio de gastos del mes de $mesActual es de \$${
                    String.format(
                        "%.2f",
                        it
                    )
                }"
                textViewGastoPromedio?.text = mensaje
            }
        }

        gastosViewModel.getPorcentajesGastosSobreIngresos(usuarioId)
            .observe(viewLifecycleOwner) { porcentaje  ->
                porcentaje?.let {
                    val textViewGastoPorcentaje = view?.findViewById<TextView>(R.id.textRecurrente)

                    // Calcular el mensaje con el porcentaje y el comentario

                    val mensaje = when {
                        it < 30 -> "Tus gastos están por debajo del 30% de tus ingresos: ${String.format("%.2f%%", it)}. ¡Excelente gestión!"
                        it in 30.0..50.0  -> "Tus gastos representan entre el 30% y el 50% de tus ingresos: ${String.format("%.2f%%", it)}. Considera revisar tus gastos."
                        else -> "Tus gastos superan el 50% de tus ingresos: ${String.format("%.2f%%", it)}. Te recomendamos ajustar tu presupuesto."
                    }
                    // Actualizar el TextView con el mensaje completo
                    textViewGastoPorcentaje?.text = mensaje
                }
            }

        // Observa el ingreso total del mes actual
        ingresosViewModel.getIngTotalDeEsteMes(usuarioId).observe(viewLifecycleOwner) { totalIngresosMesActual ->
            // Observa el ingreso total del mes anterior
            ingresosViewModel.getIngTotalMesAnterior(usuarioId).observe(viewLifecycleOwner) { totalIngresosMesAnterior ->

                // Verifica que ambos valores no sean nulos antes de realizar la comparación
                if (totalIngresosMesActual != null && totalIngresosMesAnterior != null) {
                    val textViewTotalIngresos = view?.findViewById<TextView>(R.id.textIngresos)

                    val mensaje = if (totalIngresosMesAnterior > totalIngresosMesActual) {
                        "Ingresos del mes actual: $${String.format("%.2f", totalIngresosMesActual)}.\n" +
                                "El ingreso del mes anterior fue mayor. ¡Es necesario mejorar este mes!"
                    } else {
                        "Ingresos del mes actual: $${String.format("%.2f", totalIngresosMesActual)}.\n" +
                                "Vas mejorando en tus ingresos. ¡Buen trabajo!"
                    }

                    textViewTotalIngresos?.text = mensaje
                }
            }
        }



    }

    fun obtenerMesActual(): String {
        val formatoMes = SimpleDateFormat("MMMM", Locale.getDefault())
        return formatoMes.format(Date())
    }
}

