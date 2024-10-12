package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.AlertaViewModel
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentAlertasBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat

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
        val spinnerDescripcion = dialogView.findViewById<Spinner>(R.id.spinnerDescripcion)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val editTextAlertalimite = dialogView.findViewById<EditText>(R.id.editTextAlertalimite)

        // Configurar el Spinner con las opciones de categorías
        val categorias = listOf("disponible", "gastos hormiga", "alimentos", "transporte", "servicios", "mercado")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDescripcion.adapter = adapter

        editTextFecha.setOnClickListener {
            showDatePickerDialog(editTextFecha)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nombreAlarma = editTextNombreAlarma.text.toString().trim()
                val descripcion = spinnerDescripcion.selectedItem.toString().trim()
                val fechaOriginal = editTextFecha.text.toString().trim()
                val limiteStr = editTextAlertalimite.text.toString().trim()

                if (nombreAlarma.isBlank() || fechaOriginal.isBlank() || limiteStr.isBlank()) {
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

                cargarAlertas(alertas, binding.contenedorAlerta)
            }

            // Opcional: Mostrar un Toast general si hay alguna alerta que excede el ingreso total
            val hayExceso = alertas.any { it.valor > ingresoTotal }
            if (hayExceso) {
                Toast.makeText(requireContext(), "Hay alertas que exceden tu ingreso total.", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
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

    @SuppressLint("SetTextI18n")
    private fun cargarAlertas(alertas: List<Alerta>, contenedor: ViewGroup) {
        Log.d("DashboardFragment", "Cargando alertas en contenedor ${contenedor.id}")
        contenedor.removeAllViews()
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        for (alerta in alertas) {
            val descripcionTextView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = alerta.nombre
                setTextAppearance(R.style.TxtNegroMedianoItalic)
            }

            val valorTextView = TextView(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = "${numberFormat.format(alerta.valor)}$"
                setTextAppearance(R.style.TxtNegroMedianoItalic)
            }

            val registroLayout = ConstraintLayout(requireContext()).apply {
                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                addView(descripcionTextView)
                addView(valorTextView)

                val descParams = descripcionTextView.layoutParams as ConstraintLayout.LayoutParams
                descParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                descParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                val valorParams = valorTextView.layoutParams as ConstraintLayout.LayoutParams
                valorParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                valorParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                // Agregar OnClickListener para abrir el diálogo de modificación de alerta
                setOnClickListener {
                    val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_alerta, null)
                    val textViewTitulo = dialogView.findViewById<TextView>(R.id.titulo)
                    val btnEliminarAlerta = dialogView.findViewById<Button>(R.id.btnEliminarAlerta)
                    val editTextValor = dialogView.findViewById<TextInputEditText>(R.id.editTextIngresoLimite)
                    val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
                    val dialogModificarAlerta = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setPositiveButton("Aceptar") { dialog, _ ->
                            val valor = editTextValor.text.toString()
                            val fecha = editTextFecha.text.toString()
                            if (fecha.isBlank()) {
                                Toast.makeText(requireContext(), "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show()
                                return@setPositiveButton
                            }
                            if (valor.isBlank()) {
                                Toast.makeText(requireContext(), "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show()
                                return@setPositiveButton
                            }
                            val parts = fecha.split("/")
                            val dia = parts[0].padStart(2, '0')
                            val mes = parts[1].padStart(2, '0')
                            val anio = parts[2]
                            val fechaFormateada = "${anio}-${mes}-${dia}"

                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    alertaViewModel.modificarAlerta(fechaFormateada , valor.toDouble() , alerta.id)
                                }
                            }
                            dialog.dismiss()
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    textViewTitulo.text = "Modificar alerta '${alerta.nombre}'"
                    val fecha = alerta.fecha
                    val parts = fecha.split("-")
                    val fechaFormateada = "${parts[2]}/${parts[1]}/${parts[0]}"
                    editTextFecha.setText(fechaFormateada )
                    editTextFecha.setOnClickListener {
                        showDatePickerDialog(editTextFecha)
                    }
                    editTextValor.setText(alerta.valor.toString())
                    btnEliminarAlerta.setOnClickListener {
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                alertaViewModel.eliminarAlerta(alerta.id)
                            }
                        }
                        dialogModificarAlerta.dismiss()
                    }

                    dialogModificarAlerta.show()
                }
            }

            contenedor.addView(registroLayout)
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



