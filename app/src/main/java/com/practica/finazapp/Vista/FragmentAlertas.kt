package com.practica.finazapp.Vista

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.AlertaViewModel
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentAlertasBinding

class FragmentAlertas : Fragment(), AlertasListener {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var ingresoViewModel: IngresoViewModel
    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var alertaViewModel: AlertaViewModel

    private var _binding: FragmentAlertasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertasBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ingresoViewModel = ViewModelProvider(this)[IngresoViewModel::class.java]
        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        alertaViewModel = ViewModelProvider(this)[AlertaViewModel::class.java]

        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentAlertas", "id usuario: $usuarioId")
            usuarioId?.let {
                this.usuarioId = it
                cargarAlertas()  // Cargar alertas existentes
            }
        }

        val btnNuevaAlerta = binding.btnalertanueva

        btnNuevaAlerta.setOnClickListener {
            mostrarDialogoNuevaAlerta()
        }
    }

    private fun mostrarDialogoNuevaAlerta() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_nueva_alerta, null)
        val editTextNombreAlarma = dialogView.findViewById<TextInputEditText>(R.id.editTextNombreAlarma)
        val editTextDescripcion = dialogView.findViewById<EditText>(R.id.editTextDescripcion)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val editTextAlertalimite = dialogView.findViewById<EditText>(R.id.editTextAlertalimite)

        editTextFecha.setOnClickListener {
            showDatePickerDialog(editTextFecha)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nombreAlarma = editTextNombreAlarma.text.toString().trim()
                val descripcion = editTextDescripcion.text.toString().trim()
                val fechaOriginal = editTextFecha.text.toString().trim()
                val limiteStr = editTextAlertalimite.text.toString().trim()

                if (nombreAlarma.isBlank() || fechaOriginal.isBlank() || descripcion.isBlank() || limiteStr.isBlank()) {
                    Toast.makeText(requireContext(), "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val limite: Double
                try {
                    limite = limiteStr.toDouble()
                } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Ingrese un valor válido para el límite", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val fecha = convertirFecha(fechaOriginal)

                // Obtener el ingreso total del mes y proceder con la comparación
                ingresoViewModel.getIngTotalDeEsteMes(usuarioId).observe(viewLifecycleOwner) { ingresoTotal ->
                    ingresoTotal?.let {
                        if (limite > it) {
                            Toast.makeText(requireContext(), "El valor de la alerta supera el ingreso total.", Toast.LENGTH_LONG).show()
                        } else {
                            // Guardar la nueva alerta si no excede el ingreso total
                            val nuevaAlerta = Alerta(
                                nombre = nombreAlarma,
                                descripcion = descripcion,
                                fecha = fecha,
                                valor = limite,
                                idUsuario = usuarioId
                            )
                            alertaViewModel.insertAlerta(nuevaAlerta)
                            Toast.makeText(requireContext(), "Alerta guardada correctamente.", Toast.LENGTH_SHORT).show()
                            cargarAlertas() // Actualizar la lista de alertas
                        }
                    } ?: run {
                        Toast.makeText(requireContext(), "No se pudo obtener el ingreso total.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun convertirFecha(fechaOriginal: String): String {
        // Convierte la fecha de formato "dd/MM/yyyy" a "yyyy-MM-dd"
        val parts = fechaOriginal.split("/")
        val dia = parts[0].padStart(2, '0')
        val mes = parts[1].padStart(2, '0')
        val anio = parts[2]
        return "$anio-$mes-$dia"
    }

    override fun onAlertasCargadas(alertas: List<Alerta>, ingresoTotal: Double) {
        if (alertas.isEmpty()) {
            Toast.makeText(requireContext(), "No hay alertas configuradas para este mes.", Toast.LENGTH_SHORT).show()
            binding.contenedorAlerta.removeAllViews()
        } else {
            // Limpiar el contenedor antes de cargar nuevas alertas
            binding.contenedorAlerta.removeAllViews()

            for (alerta in alertas) {
                // Crear un TextView o algún componente visual para mostrar cada alerta
                val alertaView = TextView(requireContext()).apply {
                    text = "Alerta: ${alerta.nombre} - Límite: ${alerta.valor} - Fecha: ${alerta.fecha}"
                    textSize = 16f
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }

                // Verificar si el valor de la alerta supera el ingreso total
                if (alerta.valor > ingresoTotal) {
                    alertaView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red)) // Mostrar en rojo si excede el límite
                    // Evitar múltiples Toasts para cada alerta excedida
                    // Puedes optar por mostrar un único Toast fuera del ciclo si hay al menos una alerta excedida
                }

                // Añadir la alerta al contenedor
                binding.contenedorAlerta.addView(alertaView)
            }

            // Opcional: Mostrar un Toast general si hay alguna alerta que excede el ingreso total
            val hayExceso = alertas.any { it.valor > ingresoTotal }
            if (hayExceso) {
                Toast.makeText(requireContext(), "Hay alertas que exceden tu ingreso total.", Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cargarAlertas() {
        // Obtener los ingresos totales del mes del usuario
        ingresoViewModel.getIngTotalDeEsteMes(usuarioId).observe(viewLifecycleOwner) { ingresoTotal ->
            // Si el ingreso total es null, significa que no hay ingresos para el mes actual
            if (ingresoTotal == null) {
                Toast.makeText(requireContext(), "No hay ingresos registrados para este mes.", Toast.LENGTH_SHORT).show()
                binding.contenedorAlerta.removeAllViews()
                return@observe
            }

            // Obtener las alertas del usuario actual
            alertaViewModel.getAlertasDeEsteMes(usuarioId).observe(viewLifecycleOwner) { alertas ->
                // Notificar al listener con las alertas y el ingreso total
                onAlertasCargadas(alertas, ingresoTotal)
            }
        }
    }

    private fun showDatePickerDialog(editTextFecha: EditText) {
        // Obtener la fecha actual
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Crear y mostrar el DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year1, monthOfYear, dayOfMonth1 ->
                val fechaSeleccionada = "$dayOfMonth1/${monthOfYear + 1}/$year1"
                editTextFecha.setText(fechaSeleccionada)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



