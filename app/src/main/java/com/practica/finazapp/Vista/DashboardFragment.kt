package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentDashboardBinding
import com.practica.finazapp.ui.Estilos.CustomSpinnerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.ViewModel.AlertaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lecho.lib.hellocharts.model.*
import java.text.NumberFormat
import android.graphics.Color as Color1

class DashboardFragment : Fragment(), OnItemClickListener {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var ingresoViewModel: IngresoViewModel
    private lateinit var alertaViewModel: AlertaViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private var disponible: Double = 0.0
    private lateinit var notificationHelper: NotificationHelper
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val notificacionesEnviadas = mutableSetOf<Long>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        ingresoViewModel = ViewModelProvider(this)[IngresoViewModel::class.java]
        alertaViewModel = ViewModelProvider(this)[AlertaViewModel::class.java]

        notificationHelper = NotificationHelper(requireContext())


        // Inicializar SharedPreferences
        sharedPreferences =
            requireContext().getSharedPreferences("notificaciones", Context.MODE_PRIVATE)

        // Cargar los IDs ya notificados
        val ids = sharedPreferences.getStringSet("alertas_notificadas", emptySet()) ?: emptySet()
        notificacionesEnviadas.addAll(ids.mapNotNull { it.toLongOrNull() })

        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentGastos", "id usuario: $usuarioId")
            usuarioId?.let {
                this.usuarioId = it
                cargarDatos()
                verificarAlertasExcedidas()

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


            ingresoViewModel.getIngTotalDeEsteMes(usuarioId)
                .observe(viewLifecycleOwner) { totalIngresos ->


                    lifecycleScope.launch {

                        val saldoDisponible = try {
                            gastosViewModel.getDisponibleDirecto(usuarioId)
                        } catch (e: Exception) {
                            0.0
                        }

                        // Validar si no hay ingresos
                        if (totalIngresos == null || totalIngresos == 0.0) {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setTitle("Aviso")
                            builder.setMessage("Debes poner ingresos antes de registrar un gasto.")
                            builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                            builder.create().show()
                        } else {
                            // Si hay ingresos y saldo disponible, proceder con el gasto
                            val dialogView =
                                layoutInflater.inflate(R.layout.dialog_nuevo_gasto, null)
                            val spinnerCategoria =
                                dialogView.findViewById<Spinner>(R.id.spinnerCategoria)
                            val items = resources.getStringArray(R.array.categorias).toList()
                            val adapter = CustomSpinnerAdapter(requireContext(), items)
                            spinnerCategoria.adapter = adapter
                            val editTextCantidad =
                                dialogView.findViewById<EditText>(R.id.editTextCantidad)
                            val editTextFecha =
                                dialogView.findViewById<EditText>(R.id.editTextFecha)
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


                                    if (categoria.isNotBlank() && cantidad.isNotBlank() && fechaOriginal.isNotBlank() && descripcion.isNotBlank()) {
                                        try {

                                            val valor = cantidad.toDouble()

                                            if (valor > saldoDisponible) {
                                                val builder = AlertDialog.Builder(requireContext())
                                                builder.setTitle("Aviso")
                                                builder.setMessage("No tienes saldo suficiente para realizar este gasto. En FinazAPP pensamos en ti, elimina gastos que no necesitas.")
                                                builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                                builder.create().show()
                                            } else {
                                                val parts = fechaOriginal.split("/")
                                                val dia = parts[0].padStart(2, '0')
                                                val mes = parts[1].padStart(2, '0')
                                                val anio = parts[2]
                                                val fecha = "${anio}-${mes}-${dia}"

                                                // Crear y guardar el nuevo gasto
                                                val nuevoGasto = Gasto(
                                                    categoria = categoria,
                                                    fecha = fecha,
                                                    valor = valor,
                                                    descripcion = descripcion,
                                                    idUsuario = usuarioId
                                                )

                                                lifecycleScope.launch {
                                                    withContext(Dispatchers.IO) {
                                                        // Inserción del gasto solo si la validación pasó
                                                        gastosViewModel.insertGasto(nuevoGasto)
                                                    }
                                                }

                                                Toast.makeText(
                                                    requireContext(),
                                                    "Gasto agregado correctamente",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                dialog.dismiss()
                                                verificarAlertasExcedidas()
                                            }
                                        } catch (e: NumberFormatException) {
                                            // Manejar error si la cantidad no es válida
                                            Toast.makeText(
                                                requireContext(),
                                                "La cantidad ingresada no es válida",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Por favor complete todos los campos",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                                .create()

                            dialog.show()
                        }
                    }
                }
        }
    }

        private fun verificarAlertasExcedidas() {
        // Obtener el ingreso total del mes
        ingresoViewModel.getIngTotalDeEsteMes(usuarioId).observe(viewLifecycleOwner) { ingresoTotal ->
            if (ingresoTotal == null) {
                Log.d("DashboardFragment", "No hay ingresos registrados para este mes.")
                return@observe
            }

            // Obtener las alertas del mes
            alertaViewModel.getAlertasDeEsteMes(usuarioId).observe(viewLifecycleOwner) { alertas ->
                if (alertas.isEmpty()) {
                    Log.d("DashboardFragment", "No hay alertas configuradas para este mes.")
                } else {
                    // Recorrer todas las alertas
                    for (alerta in alertas) {
                        // Verificar si la alerta está asociada a una categoría del Spinner (de las alertas)
                        val categoriasSpinner = listOf("disponible", "Gastos Hormiga", "Alimentos", "Transporte", "Servicios", "Mercado")

                        // Comprobar si la descripción de la alerta está en las categorías del spinner
                        if (categoriasSpinner.contains(alerta.descripcion)) {
                            if (alerta.descripcion == "disponible") {
                                // Si la alerta es para "disponible", obtenemos el gasto total
                                gastosViewModel.getValorGastosMes(usuarioId).observe(viewLifecycleOwner) { totalGastos ->
                                    // Verificamos si el gasto total excede el valor de la alerta
                                    if (totalGastos > alerta.valor) {
                                        // Verificar si ya se envió una notificación para esta alerta
                                        if (!notificacionesEnviadas.contains(alerta.id)) {
                                            val mensaje = "La alerta '${alerta.nombre}' para el gasto disponible ha sido excedida. Límite: ${alerta.valor}, Gasto total: $totalGastos"
                                            notificationHelper.sendNotification("Gasto Excedido", mensaje)
                                            notificacionesEnviadas.add(alerta.id)
                                            Log.d("DashboardFragment", "Notificación enviada para alerta: ${alerta.nombre}")
                                        }
                                    }
                                }
                            } else {
                                // Si no es la categoría "disponible", usamos el gasto por categoría
                                gastosViewModel.getValorGastosMesCategoria(usuarioId, alerta.descripcion).observe(viewLifecycleOwner) { gastoCategoria ->
                                    gastoCategoria?.let { totalGastos ->
                                        // Verificar si el gasto en esa categoría supera el valor de la alerta
                                        if (totalGastos > alerta.valor) {
                                            // Verificar si ya se envió una notificación para esta alerta
                                            if (!notificacionesEnviadas.contains(alerta.id)) {
                                                val mensaje = "La alerta '${alerta.nombre}' para la categoría '${alerta.descripcion}' ha sido excedida. Límite: ${alerta.valor}, Gasto: $totalGastos"
                                                notificationHelper.sendNotification("Gasto Excedido", mensaje)
                                                notificacionesEnviadas.add(alerta.id)
                                                Log.d("DashboardFragment", "Notificación enviada para alerta: ${alerta.nombre}")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    fun onBackPressed() {
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

        gastosViewModel.getValorGastosMes(usuarioId).observe(viewLifecycleOwner) { gastosMes ->
            if (gastosMes != null) {
                val numberFormat = NumberFormat.getInstance()
                numberFormat.maximumFractionDigits = 2
                val cantidadGastos = gastosMes
                val gastadosTextView = binding.TxtGastoTotal
                gastadosTextView.setText("${numberFormat.format(cantidadGastos)}$")
                cargarDona()
            }
        }

        ingresoViewModel.getIngTotalDeEsteMes(usuarioId).observe(viewLifecycleOwner) { ingresoMensual ->
            if (ingresoMensual != null) {
                // Luego cargamos los gastos por categoría y los comparamos
                verificarPorcentajeDeGastos(ingresoMensual)
            }
        }
    }

    private fun verificarPorcentajeDeGastos(ingresoMensual: Double) {
        // Gastos Hormiga
        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Gastos Hormiga")
            .observe(viewLifecycleOwner) { cantidadGastosVarios ->
                cantidadGastosVarios?.let {
                    val porcentajeGastosVarios = (cantidadGastosVarios / ingresoMensual) * 100
                    if (porcentajeGastosVarios > obtenerLimitePorCategoria("Gastos Hormiga", ingresoMensual)) {
                        mostrarAdvertencia("Gastos Hormiga", porcentajeGastosVarios)
                    }
                }
            }

        // Alimentos
        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Alimentos")
            .observe(viewLifecycleOwner) { cantidadAlimentos ->
                cantidadAlimentos?.let {
                    val porcentajeAlimentos = (cantidadAlimentos / ingresoMensual) * 100
                    if (porcentajeAlimentos > obtenerLimitePorCategoria("Alimentos", ingresoMensual)) {
                        mostrarAdvertencia("Alimentos", porcentajeAlimentos)
                    }
                }
            }

        // Transporte
        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Transporte")
            .observe(viewLifecycleOwner) { cantidadTransporte ->
                cantidadTransporte?.let {
                    val porcentajeTransporte = (cantidadTransporte / ingresoMensual) * 100
                    if (porcentajeTransporte > obtenerLimitePorCategoria("Transporte", ingresoMensual)) {
                        mostrarAdvertencia("Transporte", porcentajeTransporte)
                    }
                }
            }

        // Servicios
        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Servicios")
            .observe(viewLifecycleOwner) { cantidadServicios ->
                cantidadServicios?.let {
                    val porcentajeServicios = (cantidadServicios / ingresoMensual) * 100
                    if (porcentajeServicios > obtenerLimitePorCategoria("Servicios", ingresoMensual)) {
                        mostrarAdvertencia("Servicios", porcentajeServicios)
                    }
                }
            }

        // Mercado
        gastosViewModel.getValorGastosMesCategoria(usuarioId, "Mercado")
            .observe(viewLifecycleOwner) { cantidadMercado ->
                cantidadMercado?.let {
                    val porcentajeMercado = (cantidadMercado / ingresoMensual) * 100
                    if (porcentajeMercado > obtenerLimitePorCategoria("Mercado", ingresoMensual)) {
                        mostrarAdvertencia("Mercado", porcentajeMercado)
                    }
                }
            }
    }

    private fun obtenerLimitePorCategoria(categoria: String, ingresoMensual: Double): Double {
        return when (ingresoMensual) {
            in 1_000_000.0..1_500_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 5.0
                    "Alimentos" -> 30.0
                    "Servicios" -> 20.0
                    "Transporte" -> 15.0
                    "Mercado" -> 25.0
                    else -> 100.0
                }
            }
            in 1_500_000.0..2_500_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 6.0
                    "Alimentos" -> 28.0  // Ajustado
                    "Servicios" -> 18.0
                    "Transporte" -> 17.0
                    "Mercado" -> 29.0
                    else -> 100.0
                }
            }
            in 2_500_000.0..4_000_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 8.0
                    "Alimentos" -> 25.0  // Ajustado
                    "Servicios" -> 15.0
                    "Transporte" -> 15.0
                    "Mercado" -> 34.0
                    else -> 100.0
                }
            }
            in 4_000_000.0..6_000_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 10.0
                    "Alimentos" -> 22.0  // Ajustado
                    "Servicios" -> 12.0
                    "Transporte" -> 15.0
                    "Mercado" -> 38.0
                    else -> 100.0
                }
            }
            in 6_000_000.0..10_000_000.0 -> {
                when (categoria) {
                    "Gastos Hormiga" -> 12.0
                    "Alimentos" -> 20.0  // Ajustado
                    "Servicios" -> 10.0
                    "Transporte" -> 14.0
                    "Mercado" -> 42.0
                    else -> 100.0
                }
            }
            else -> {
                when (categoria) {
                    "Gastos Hormiga" -> 15.0
                    "Alimentos" -> 18.0  // Ajustado
                    "Servicios" -> 8.0
                    "Transporte" -> 10.0
                    "Mercado" -> 49.0
                    else -> 100.0
                }
            }
        }
    }

    // Método para mostrar el mensaje de advertencia
    private fun mostrarAdvertencia(categoria: String, porcentaje: Double) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Advertencia de Gastos")
        builder.setMessage("Has gastado el $porcentaje% en $categoria, lo cual supera el límite recomendado por entidades financieras como la Superintendencia Financiera de Colombia. Te sugerimos revisar tus gastos para mantener un mejor control de tu presupuesto.")

        // Botón de Aceptar
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss() // Cierra el diálogo al hacer clic en "Aceptar"
        }

        // Crear y mostrar el diálogo
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun mostrarListaDeGastos(recyclerView: RecyclerView, categoria: String) {
        gastosViewModel.getGastosMesCategoria(usuarioId, categoria)
            .observe(viewLifecycleOwner) { gastosCat ->

                val adapter = GastoAdapter(gastosCat)
                adapter.setOnItemClickListener(this)
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
        spinnerCategoria.isEnabled = false
        val items = resources.getStringArray(R.array.categorias).toList()
        val adapter = CustomSpinnerAdapter(requireContext(), items)
        spinnerCategoria.adapter = adapter
        val editTextCantidad = dialogView.findViewById<EditText>(R.id.editTextCantidad)
        val editTextFecha = dialogView.findViewById<EditText>(R.id.editTextFecha)
        val editTextDescripcion = dialogView.findViewById<EditText>(R.id.editTextDescripcion)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnEliminarGasto)

        // Configurar los campos con los datos existentes del gasto
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

                // Validar campos obligatorios
                if (categoria.isNotBlank() && cantidad.isNotBlank() && fechaOriginal.isNotBlank() && descripcion.isNotBlank()) {
                    try {
                        val valor = cantidad.toDouble()

                        // Comprobar saldo disponible antes de guardar
                        lifecycleScope.launch {
                            val saldoDisponible = withContext(Dispatchers.IO) {
                                gastosViewModel.getDisponibleDirecto(gasto.idUsuario) // Reemplaza con el método correcto si es necesario
                            }

                            if (valor > saldoDisponible) {
                                // Mostrar mensaje de error si el gasto excede el saldo disponible
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        requireContext(),
                                        "El valor ingresado excede el saldo disponible.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                // Proceder a actualizar el gasto si el saldo es suficiente
                                val partes = fechaOriginal.split("/")
                                val dia = partes[0].padStart(2, '0')
                                val mes = partes[1].padStart(2, '0')
                                val anio = partes[2]
                                val fecha = "${anio}-${mes}-${dia}"

                                withContext(Dispatchers.IO) {
                                    gastosViewModel.modificarGasto(
                                        id = gasto.id,
                                        categoria = categoria,
                                        valor = valor,
                                        descripcion = descripcion,
                                        fecha = fecha
                                    )
                                }
                                withContext(Dispatchers.Main) {
                                    dialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        "Gasto actualizado correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(requireContext(), "La cantidad ingresada no es válida", Toast.LENGTH_SHORT).show()
                    }
                } else {
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


    fun cargarBarraDisp(cantidad: Double?, barra: View) {
        if (cantidad != null && cantidad >= 0) {
            gastosViewModel.getValorGastosMes(usuarioId).observe(viewLifecycleOwner) { gastosMes ->
                if (gastosMes != null && gastosMes > 0) {
                    val barraGris = binding.barraGrisDisponible
                    val cien = barraGris.width
                    val total = gastosMes + cantidad
                    val layoutParams = barra.layoutParams as ConstraintLayout.LayoutParams
                    layoutParams.width = ((cien * cantidad) / total).toInt()
                    barra.layoutParams = layoutParams
                    barra.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun cargarDona() {
        gastosViewModel.getDisponible(usuarioId).observe(viewLifecycleOwner) { disponible ->
            gastosViewModel.getValorGastosMesCategoria(usuarioId, "Gastos Hormiga")
                .observe(viewLifecycleOwner) { cantGastosVarios ->
                    gastosViewModel.getValorGastosMesCategoria(usuarioId, "Alimentos")
                        .observe(viewLifecycleOwner) { cantAlimentos ->
                            gastosViewModel.getValorGastosMesCategoria(usuarioId, "Transporte")
                                .observe(viewLifecycleOwner) { cantTransporte ->
                                    gastosViewModel.getValorGastosMesCategoria(
                                        usuarioId,
                                        "Servicios"
                                    ).observe(viewLifecycleOwner) { cantServicios ->
                                        gastosViewModel.getValorGastosMesCategoria(
                                            usuarioId,
                                            "Mercado"
                                        ).observe(viewLifecycleOwner) { cantMercado ->
                                            val pieChart = binding.dona1
                                            val pieData = mutableListOf<SliceValue>()
                                            if (cantAlimentos != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantAlimentos.toFloat(),
                                                        obtenerColorCategoria("Alimentos")
                                                    )
                                                )
                                            }
                                            if (cantGastosVarios != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantGastosVarios.toFloat(),
                                                        obtenerColorCategoria("Gastos Hormiga")
                                                    )
                                                )
                                            }
                                            if (cantTransporte != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantTransporte.toFloat(),
                                                        obtenerColorCategoria("Transporte")
                                                    )
                                                )
                                            }
                                            if (cantServicios != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantServicios.toFloat(),
                                                        obtenerColorCategoria("Servicios")
                                                    )
                                                )
                                            }
                                            if (cantMercado != null) {
                                                pieData.add(
                                                    SliceValue(
                                                        cantMercado.toFloat(),
                                                        obtenerColorCategoria("Mercado")
                                                    )
                                                )
                                            }
                                            if (disponible != null && disponible >= 0) {
                                                pieData.add(
                                                    SliceValue(
                                                        disponible.toFloat(),
                                                        obtenerColorCategoria("disponible")
                                                    )
                                                )
                                            }
                                            val pieChartData = PieChartData(pieData)
                                            pieChartData.setHasCenterCircle(true)
                                            pieChartData.setCenterCircleScale(0.8f)
                                            pieChartData.setCenterCircleColor(Color1.WHITE)
                                            pieChart.pieChartData = pieChartData
                                        }
                                    }
                                }
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
        val color = Color1.parseColor(categoriasColores[categoria])
        return color
    }
}

