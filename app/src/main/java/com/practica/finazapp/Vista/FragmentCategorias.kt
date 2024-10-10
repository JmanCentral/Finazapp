package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentCategoriasBinding
import com.practica.finazapp.ui.Estilos.CustomSpinnerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat

class FragmentCategorias : Fragment(), OnItemClickListener1 {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var ingresoViewModel: IngresoViewModel
    private var disponible: Double = 0.0
    private var _binding: FragmentCategoriasBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriasBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        ingresoViewModel = ViewModelProvider(this)[IngresoViewModel::class.java]


        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentGastos", "id usuario: $usuarioId")
            usuarioId?.let {
                this.usuarioId = it
                cargarDatos()
                val bloqueTransporte = binding.bloqueTransporte
                val bloqueGastosVarios = binding.bloqueGastosVarios
                val bloqueMercado = binding.bloqueMercado
                val bloqueServicios = binding.bloqueServicios
                val bloqueAlimentos = binding.bloqueAlimentos
                val recyclrerViewTransporte = binding.recyclerViewTransporte
                val recyclrerViewGastosVarios = binding.recyclerViewGastosVarios
                val recyclrerViewMercado = binding.recyclerViewMercado
                val recyclrerViewServicios = binding.recyclerViewServicios
                val recyclrerViewAlimentos = binding.recyclerViewAlimentos


                bloqueTransporte.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewTransporte, "Transporte")
                }
                bloqueMercado.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewMercado, "Mercado")
                }
                bloqueServicios.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewServicios, "Servicios")
                }
                bloqueAlimentos.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewAlimentos, "Alimentos")
                }
                bloqueGastosVarios.setOnClickListener {
                    mostrarListaDeGastos(recyclrerViewGastosVarios, "Gastos Hormiga")
                }


            }
        }
        val btnNuevoGasto = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        btnNuevoGasto.setOnClickListener {

            ingresoViewModel.getIngTotalDeEsteMes(usuarioId).observe(viewLifecycleOwner) { totalIngresos ->
                if (totalIngresos == null || totalIngresos == 0.0) {
                    // No hay ingresos registrados, mostrar un mensaje y evitar el registro de gastos
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Aviso")
                    builder.setMessage("Debe poner ingresos antes de registrar un gasto.")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.create().show()

                } else {

                    val dialogView = layoutInflater.inflate(R.layout.dialog_nuevo_gasto, null)
                    val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
                    val items = resources.getStringArray(R.array.categorias).toList()
                    val adapter = CustomSpinnerAdapter(requireContext(), items)
                    spinnerCategoria.adapter = adapter
                    val editTextCantidad = dialogView.findViewById<EditText>(R.id.editTextCantidad)
                    val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
                    val editTextDescripcion =
                        dialogView.findViewById<EditText>(R.id.editTextDescripcion)

                    editTextFecha.setOnClickListener {
                        showDatePickerDialog(editTextFecha)
                    }

                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setPositiveButton("Guardar") { dialog, _ ->
                            val categoria = spinnerCategoria.selectedItem.toString()
                            val cantidad = editTextCantidad.text.toString()
                            val fechaOriginal = editTextFecha.text.toString()
                            val descripcion = editTextDescripcion.text.toString()

                            // Validar que los campos obligatorios no estén vacíos
                            if (categoria.isNotBlank() && cantidad.isNotBlank() && fechaOriginal.isNotBlank() && descripcion.isNotBlank()) {
                                try {
                                    // Validar que la cantidad sea un número válido
                                    val valor = cantidad.toDouble()

                                    // Realizar la conversión de fecha y guardar el nuevo gasto
                                    val parts = fechaOriginal.split("/")
                                    val dia = parts[0].padStart(2, '0')
                                    val mes = parts[1].padStart(2, '0')
                                    val anio = parts[2]
                                    val fecha = "${anio}-${mes}-${dia}"
                                    val nuevoGasto = Gasto(
                                        categoria = categoria,
                                        fecha = fecha,
                                        valor = valor,
                                        descripcion = descripcion,
                                        idUsuario = usuarioId
                                    )
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            gastosViewModel.insertGasto(nuevoGasto)
                                        }
                                    }
                                    dialog.dismiss()
                                } catch (e: NumberFormatException) {
                                    // Manejar el caso en que la cantidad no sea un número válido
                                    Toast.makeText(
                                        requireContext(),
                                        "La cantidad ingresada no es válida",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Mostrar un mensaje de error si algún campo obligatorio está vacío
                                Toast.makeText(
                                    requireContext(),
                                    "Por favor complete todos los campos",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()

                    dialog.show()
                }
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
                // Aquí obtienes la fecha seleccionada y la estableces en el EditText
                val fechaSeleccionada = "$dayOfMonth1/${monthOfYear + 1}/$year1"
                editTextFecha.setText(fechaSeleccionada)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun disponible() {

    }


    @SuppressLint("SetTextI18n")
    private fun cargarDatos() {

        gastosViewModel.getDisponible(usuarioId).observe(viewLifecycleOwner) { disp ->
            val numberFormat = NumberFormat.getInstance()
            numberFormat.maximumFractionDigits = 2
            if (disp != null) {
                disponible = disp
                val disponibleTextView = binding.cantidadDisponible
                disponibleTextView.setText("${numberFormat.format(disponible)}$")
                val barraDisponible = binding.barraDisponible
                cargarBarraDisp(disponible, barraDisponible)
            }
        }
        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Gastos Hormiga")
            .observe(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val gastosVariosTextView = binding.cantidadGastosVarios
                    gastosVariosTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraGastosVarios = binding.barraGastosVarios
                    cargarBarra(cantidadCategoria, barraGastosVarios)
                }
            }

        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Alimentos")
            .observe(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val AlimentosTextView = binding.cantidadAlimentos
                    AlimentosTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraAlimentos = binding.barraAlimentos
                    cargarBarra(cantidadCategoria, barraAlimentos)
                }
            }

        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Transporte")
            .observe(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val TransporteTextView = binding.cantidadTransporte
                    TransporteTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraTransporte = binding.barraTransporte
                    cargarBarra(cantidadCategoria, barraTransporte)
                }
            }

        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Servicios")
            .observe(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val ServiciosTextView = binding.cantidadServicios
                    ServiciosTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraServicios = binding.barraServicios
                    cargarBarra(cantidadCategoria, barraServicios)
                }
            }

        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Mercado")
            .observe(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    val numberFormat = NumberFormat.getInstance()
                    numberFormat.maximumFractionDigits = 2
                    val cantidadCategoria = cantidad
                    val MercadoTextView = binding.cantidadMercado
                    MercadoTextView.setText("${numberFormat.format(cantidadCategoria)}$")
                    val barraMercado = binding.barraMercado
                    cargarBarra(cantidadCategoria, barraMercado)
                }
            }

    }

    private fun mostrarListaDeGastos(recyclerView: RecyclerView, categoria: String) {
        gastosViewModel.getGastosMesCategoria(usuarioId, categoria)
            .observe(viewLifecycleOwner) { gastosCat ->
                // Crear un adaptador para el RecyclerView
                val adapter = GastoAdapter(gastosCat)
                adapter.setOnItemClickListener1(this)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                recyclerView.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    setAdapter(adapter)
                }
                if (recyclerView.visibility == View.GONE) {
                    recyclerView.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.GONE
                }

            }
    }

    override fun onItemClick(gasto: Gasto) {

        val dialogView = layoutInflater.inflate(R.layout.dialog_modificar_gasto, null)
        val spinnerCategoria = dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
        val items = resources.getStringArray(R.array.categorias).toList()
        val adapter = CustomSpinnerAdapter(requireContext(), items)
        spinnerCategoria.adapter = adapter
        val editTextCantidad = dialogView.findViewById<EditText>(R.id.editTextCantidad)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val editTextDescripcion = dialogView.findViewById<EditText>(R.id.editTextDescripcion)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminarGasto)
        editTextCantidad.setText(gasto.valor.toString())
        val parts = gasto.fecha.split("-")
        val fechaFormateada = "${parts[2]}/${parts[1]}/${parts[0]}"
        editTextFecha.setText(fechaFormateada)
        editTextDescripcion.setText(gasto.descripcion)
        val posicionCategoria = items.indexOf(gasto.categoria)
        if (posicionCategoria != -1) {
            spinnerCategoria.setSelection(posicionCategoria)
        }

        editTextFecha.setOnClickListener {
            showDatePickerDialog(editTextFecha)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                val categoria = spinnerCategoria.selectedItem.toString()
                val cantidad = editTextCantidad.text.toString()
                val fechaOriginal = editTextFecha.text.toString()
                val descripcion = editTextDescripcion.text.toString()

                // Validar que los campos obligatorios no estén vacíos
                if (categoria.isNotBlank() && cantidad.isNotBlank() && fechaOriginal.isNotBlank() && descripcion.isNotBlank()) {
                    try {
                        // Validar que la cantidad sea un número válido
                        val valor = cantidad.toDouble()

                        // Realizar la conversión de fecha y guardar los cambios
                        val partes = fechaOriginal.split("/")
                        val dia = partes[0].padStart(2, '0')
                        val mes = partes[1].padStart(2, '0')
                        val anio = partes[2]
                        val fecha = "${anio}-${mes}-${dia}"
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                gastosViewModel.modificarGasto(
                                    id = gasto.id,
                                    categoria = categoria,
                                    valor = valor,
                                    descripcion = descripcion,
                                    fecha = fecha
                                )
                            }
                        }
                        dialog.dismiss()
                    } catch (e: NumberFormatException) {
                        // Manejar el caso en que la cantidad no sea un número válido
                        Toast.makeText(requireContext(), "La cantidad ingresada no es válida", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Mostrar un mensaje de error si algún campo obligatorio está vacío
                    Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                }
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        btnEliminar.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    gastosViewModel.deleteGasto(gasto.id)
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    fun cargarBarra(cantidad: Double, barra: View) {
        gastosViewModel.getValorGastosMes(usuarioId).observe(viewLifecycleOwner) { gastosMes ->
            if (gastosMes != null) {
                gastosViewModel.getDisponible(usuarioId).observe(viewLifecycleOwner) { disponible ->
                    if (disponible != null) {
                        val barraGris = binding.barraGrisDisponible
                        val cien = barraGris.width
                        val total = disponible + gastosMes
                        val layoutParams = barra.layoutParams as ConstraintLayout.LayoutParams
                        layoutParams.width = ((cien*cantidad)/total).toInt()
                        barra.layoutParams = layoutParams
                        barra.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    fun cargarBarraDisp(cantidad: Double, barra: View) {
        gastosViewModel.getValorGastosMes(usuarioId).observe(viewLifecycleOwner) { gastosMes ->
            if (gastosMes > 0) {
                if( cantidad >= 0) {
                    val barraGris = binding.barraGrisDisponible
                    val cien = barraGris.width
                    val total = gastosMes+cantidad
                    val layoutParams = barra.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.width = ((cien*cantidad)/total).toInt()
                    barra.layoutParams = layoutParams
                    barra.visibility = View.VISIBLE
                }
            }
        }
    }


    // Función para obtener el color correspondiente a cada categoría
    fun obtenerColorCategoria(categoria: String): Int {
        val categoriasColores = mapOf(
            "disponible" to "#87EE2B",
            "Gastos Hormiga" to "#F66B6B",
            "Alimentos" to "#FF66C1",
            "Transporte" to "#339AF0",
            "Servicios" to "#EEB62B",
            "Mercado" to "#FD8435"
        )
        val color = Color.parseColor(categoriasColores[categoria])
        return color
    }
}