package com.practica.finazapp.Vista

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentReporteBinding
import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter
import lecho.lib.hellocharts.model.Axis
import lecho.lib.hellocharts.model.AxisValue
import lecho.lib.hellocharts.model.Line
import lecho.lib.hellocharts.model.LineChartData
import lecho.lib.hellocharts.model.PointValue
import lecho.lib.hellocharts.view.LineChartView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class Reporte : Fragment() {
    private var usuarioId: Long = -1
    private var _binding: FragmentReporteBinding? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var gastosViewModel: GastosViewModel
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño del fragmento utilizando el enlace de datos generado
        _binding = FragmentReporteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Recuperar el ID del usuario del argumento
        usuarioId = arguments?.getLong("usuario_id", -1) ?: -1

        // Devolver la vista raíz del diseño inflado
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            Log.d("FragmentGastos", "id usuario: $usuarioId")
            usuarioId?.let {
                this.usuarioId = it
                Log.d("FragmentReporte", "id usuario: $usuarioId")
                val adelanteSem = binding.btonAdelanteSem
                val atrasSem = binding.btonAtrasSem
                val adelanteMes = binding.btonAdelanteMes
                val atrasMes = binding.btonAtrasMes
                val adelanteAn= binding.btonAdelanteAn
                val atrasAn = binding.btonAtrasAn
                var fechaActual = LocalDate.now()
                Log.d("FragmentReporte", "fecha de referencia: $fechaActual")
                listOf<String>("disponible", "Gastos hormiga", "Alimentos", "Transporte", "Servicios", "Mercado")

                adelanteSem.setOnClickListener {
                    fechaActual = fechaActual.plusWeeks(1)
                    actualizarGrafico(fechaActual, "Semanal")
                }
                atrasSem.setOnClickListener {
                    fechaActual = fechaActual.minusWeeks(1)
                    actualizarGrafico(fechaActual, "Semanal")
                }
                adelanteMes.setOnClickListener {
                    fechaActual = fechaActual.plusMonths(1)
                    actualizarGrafico(fechaActual, "Mensual")
                }
                atrasMes.setOnClickListener {
                    fechaActual = fechaActual.minusMonths(1)
                    actualizarGrafico(fechaActual, "Mensual")
                }
                adelanteAn.setOnClickListener {
                    fechaActual = fechaActual.plusYears(1)
                    actualizarGrafico(fechaActual, "Anual")
                }
                atrasAn.setOnClickListener {
                    fechaActual = fechaActual.minusYears(1)
                    actualizarGrafico(fechaActual, "Anual")
                }
                actualizarGrafico(fechaActual,"Semanal")
                actualizarGrafico(fechaActual, "Mensual")
                actualizarGrafico(fechaActual, "Anual")
            }

        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    fun actualizarGrafico(fechaActual: LocalDate, tiempo: String){
        Log.d("FragmentReporte", "fecha de referencia: $fechaActual, tiempo: $tiempo")
        lateinit var lineChartReporte: LineChartView
        when (tiempo){
            "Semanal" ->{
                lineChartReporte = binding.graficoLineasSemanal
                val fechaInf = periodoDeTiempo(tiempo, fechaActual).first
                val fechaSup = periodoDeTiempo(tiempo, fechaActual).second
                val formato1 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val fechaInfFormateada  = fechaInf.toString().format(formato1)
                val fechaSupFormateada = fechaSup.toString().format(formato1)
                val descSem = binding.descSem
                val diaInf = fechaInf.dayOfMonth
                val diaSup = fechaSup.dayOfMonth
                val mes = extraerMes(fechaSup)
                val anio = fechaActual.year
                descSem.setText("semana del $diaInf-$diaSup de $mes del $anio")
                Log.d("FragmentReporte", "rango: $fechaInfFormateada a $fechaSupFormateada")
                gastosViewModel.getGastosPorFechas(usuarioId, fechaInfFormateada, fechaSupFormateada).observe(viewLifecycleOwner){listaSem ->
                    cargarGraficoLineasSem(lineChartReporte, convertirMapaALista(getDatosPorCategoria(listaSem)))
                    Log.d("FragmentReporte", "lista gastos: $listaSem")
                }
            }
            "Mensual" -> {
                lineChartReporte = binding.graficoLineasMensual
                val fechaInf = periodoDeTiempo(tiempo, fechaActual).first
                val fechaSup = periodoDeTiempo(tiempo, fechaActual).second
                val formato = DateTimeFormatter.ofPattern("yyyy-mm-dd")
                val fechaInfFormateada  = fechaInf.toString().format(formato)
                val fechaSupFormateada = fechaSup.toString().format(formato)
                val descSem = binding.descMes
                val mes = extraerMes(fechaActual)
                val anio = fechaActual.year
                descSem.setText("$mes de $anio")

                Log.d("FragmentReporte", "rango: $fechaInfFormateada a $fechaSupFormateada")
                gastosViewModel.getGastosPorFechas(usuarioId, fechaInfFormateada, fechaSupFormateada).observe(viewLifecycleOwner){listaMes ->
                    cargarGraficoLineasMes(lineChartReporte, convertirMapaALista(getDatosPorCategoria(listaMes)))
                    Log.d("FragmentReporte", "lista gastos: $listaMes")
                }
            }
            "Anual" -> {
                lineChartReporte = binding.graficoLineasAnual
                val fechaInf = periodoDeTiempo(tiempo, fechaActual).first.toString()
                val fechaSup = periodoDeTiempo(tiempo, fechaActual).second.toString()
                val formato = DateTimeFormatter.ofPattern("yyyy-mm-dd")
                val fechaInfFormateada  = fechaInf.format(formato)
                val fechaSupFormateada = fechaSup.format(formato)
                val descSem = binding.descAn
                val anio = fechaActual.year
                descSem.setText("$anio")
                Log.d("FragmentReporte", "rango: $fechaInfFormateada a $fechaSupFormateada")
                gastosViewModel.getGastosPorFechas(usuarioId, fechaInfFormateada, fechaSupFormateada).observe(viewLifecycleOwner){listaAn ->
                    Log.d("FragmentReporte", "lista gastos: $listaAn")
                    cargarGraficoLineasAn(lineChartReporte, convertirMapaALista(getDatosPorCategoria(listaAn)))
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun extraerMes(fecha: LocalDate): String{
        val mes = when(fecha.month){
            Month.JANUARY -> "Enero"
            Month.FEBRUARY -> "Febrero"
            Month.MARCH -> "Marzo"
            Month.APRIL -> "Abril"
            Month.MAY -> "Mayo"
            Month.JUNE -> "Junio"
            Month.JULY -> "Julio"
            Month.AUGUST -> "Agosto"
            Month.SEPTEMBER -> "Septiembre"
            Month.OCTOBER -> "Octubre"
            Month.NOVEMBER -> "Noviembre"
            Month.DECEMBER -> "Diciembre"
            null -> "null"
        }
        return mes
    }

    private fun convertirMapaALista(datosPorCategoria: Map<String, Map<String, Double>>): Map<String, List<Pair<String, Double>>> {
        val listaDatosPorCategoria = mutableMapOf<String, List<Pair<String,Double>>>()

        for ((categoria, datos) in datosPorCategoria) {
            val listaDatos = datos.toList()
            listaDatosPorCategoria.put(categoria, listaDatos)
        }
        return listaDatosPorCategoria
    }
    private fun getDatosPorCategoria(lista: List<Gasto>): Map<String, Map<String, Double>> {
        val datosPorCategoria: MutableMap<String, MutableMap<String, Double>> = mutableMapOf()

        for (dato in lista) {
            val categoria = dato.categoria
            val fecha = dato.fecha
            val valor = dato.valor

            // Si la categoría no existe en el mapa, crear una nueva entrada
            if (!datosPorCategoria.containsKey(categoria)) {
                datosPorCategoria[categoria] = mutableMapOf(fecha to valor)
            } else {
                // Si la categoría existe, verificar si la fecha ya existe
                if (datosPorCategoria[categoria]?.containsKey(fecha) == true) {
                    // Si la fecha ya existe, sumar el valor al valor existente
                    val valorExistente = datosPorCategoria[categoria]?.get(fecha) ?: 0.0
                    datosPorCategoria[categoria]?.put(fecha, valorExistente + valor)
                } else {
                    // Si la fecha no existe, agregar una nueva entrada
                    datosPorCategoria[categoria]?.put(fecha, valor)
                }
            }
        }

        return datosPorCategoria
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun periodoDeTiempo(tiempo: String, fechaActual: LocalDate): Pair<LocalDate, LocalDate> {
        return when (tiempo) {
            "Semanal" -> {
                val inicioSemana = fechaActual.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val finSemana = fechaActual.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                Pair(inicioSemana, finSemana)
            }
            "Mensual" -> {
                val inicioMes = fechaActual.with(TemporalAdjusters.firstDayOfMonth())
                val finMes = fechaActual.with(TemporalAdjusters.lastDayOfMonth())
                Pair(inicioMes, finMes)
            }
            "Anual" -> {
                val inicioAnio = fechaActual.with(TemporalAdjusters.firstDayOfYear())
                val finAnio = fechaActual.with(TemporalAdjusters.lastDayOfYear())
                Pair(inicioAnio, finAnio)
            }
            else -> throw IllegalArgumentException("Tipo de rango de fechas no válido")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarGraficoLineasSem(chartView: LineChartView, datosPorCategoria: Map<String, List<Pair<String, Double>>>) {
        val lineData = LineChartData()

        val lineas = mutableListOf<Line>() // Lista para almacenar todas las líneas

        for ((categoria, datos) in datosPorCategoria) {
            val line = Line()
            line.values = generarPuntosSem(datos)
            line.color = getColorCategoria(categoria)
            line.strokeWidth = 2
            line.pointRadius = 2
            line.isCubic = false
            line.hasLabels()
            lineas.add(line) // Agregar la línea a la lista
        }

        // Configuración del eje X (fecha)
        val axisX = Axis().apply {
            setHasLines(true)
            values = mutableListOf<AxisValue>().apply {
                // Agregar valores de 0 a 365 al eje X
                for (i in 0..7) {
                    val diaSem = when(i){
                        1 -> "Lun"
                        2 -> "Mar"
                        3 -> "Mier"
                        4 -> "Jue"
                        5 -> "Vier"
                        6 -> "Sab"
                        7 -> "Dom"
                        else -> {"?"}
                    }
                    add(AxisValue(i.toFloat()).apply {
                        setLabel(diaSem)
                        setTextSize(10)
                    })
                }
            }
        }

        // Configuración del eje Y (valor)
        val axisY = Axis().apply {
            setHasLines(true)
            val formatter = SimpleAxisValueFormatter()
            formatter.appendedText = "k".toCharArray()
            setFormatter(formatter)
            setMaxLabelChars(5)
        }

        lineData.lines = lineas // Asignar la lista de líneas al LineChartData
        lineData.axisXBottom = axisX
        lineData.axisYLeft = axisY

        chartView.lineChartData = lineData
        val noData = binding.noDataSem
        if(isChartEmpty(chartView)){
            noData.visibility = View.VISIBLE
            chartView.visibility = View.INVISIBLE
        } else {
            // Ocultar el texto  aviso
            noData.visibility = View.GONE
            chartView.visibility = View.VISIBLE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generarPuntosSem(datos: List<Pair<String, Double>>): MutableList<PointValue> {
        val puntos = mutableListOf<PointValue>()

        datos.forEachIndexed { index, (fecha, valor) ->
            val x = diaSemana(fecha)
            val y = (valor/1000).toFloat()
            puntos.add(PointValue(x, y))
        }
        return puntos
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun diaSemana(fecha: String): Float{
        val diaSem = LocalDate.parse(fecha).dayOfWeek
        Log.d("FragmentReporte", "fecha: $fecha, dia de la semana: $diaSem")
        return diaSem.value.toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarGraficoLineasMes(chartView: LineChartView, datosPorCategoria: Map<String, List<Pair<String, Double>>>) {
        val lineData = LineChartData()

        val lineas = mutableListOf<Line>() // Lista para almacenar todas las líneas

        for ((categoria, datos) in datosPorCategoria) {
            val line = Line()
            line.values = generarPuntosMes(organizar(datos))
            line.color = getColorCategoria(categoria)
            line.strokeWidth = 2
            line.pointRadius = 2
            line.isCubic = false
            line.hasLabels()
            lineas.add(line) // Agregar la línea a la lista
        }

        // Configuración del eje X (fecha)
        val axisX = Axis().apply {
            setHasLines(true)
            values = mutableListOf<AxisValue>().apply {
                // Agregar valores de 0 a 31
                for (i in 0..31) {
                    add(AxisValue(i.toFloat()).apply {
                        setTextSize(10)
                        setLabel(i.toString())
                    })
                }
            }
        }

        // Configuración del eje Y (valor)
        val axisY = Axis().apply {
            setHasLines(true)
            val formatter = SimpleAxisValueFormatter()
            formatter.appendedText = "k".toCharArray()
            setFormatter(formatter)
            setMaxLabelChars(5)
        }

        lineData.lines = lineas // Asignar la lista de líneas al LineChartData
        lineData.axisXBottom = axisX
        lineData.axisYLeft = axisY

        chartView.lineChartData = lineData
        val noData = binding.noDataMes
        if(isChartEmpty(chartView)){
            noData.visibility = View.VISIBLE
            chartView.visibility = View.INVISIBLE
        } else {
            // Ocultar el texto  aviso
            noData.visibility = View.GONE
            chartView.visibility = View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun generarPuntosMes(datos: List<Pair<String, Double>>): MutableList<PointValue> {
        val puntos = mutableListOf<PointValue>()

        datos.forEachIndexed { index, (fecha, valor) ->
            val x = diaMes(fecha)
            val y = (valor/1000).toFloat()
            puntos.add(PointValue(x, y))
        }
        return puntos
    }

    fun diaMes(fecha: String): Float{
        val partes = fecha.split("-")
        val dia = partes[2]
        return dia.toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun cargarGraficoLineasAn(chartView: LineChartView, datosPorCategoria: Map<String, List<Pair<String, Double>>>) {
        val lineData = LineChartData()

        val lineas = mutableListOf<Line>() // Lista para almacenar todas las líneas

        for ((categoria, datos) in datosPorCategoria) {
            val line = Line()
            line.values = generarPuntosAn(organizar(datos))
            line.color = getColorCategoria(categoria)
            line.strokeWidth = 2
            line.pointRadius = 2
            line.isCubic = false
            line.hasLabels()
            lineas.add(line) // Agregar la línea a la lista
        }

        // Configuración del eje X (fecha)
        val axisX = Axis().apply {
            setHasLines(true)
            values = mutableListOf<AxisValue>().apply {
                // Agregar valores de 0 a 365 al eje X
                for (i in 0..365) {
                    if (primerDiaMes(i)) {
                        val mesString = when (i) {
                            1 -> "Ene"
                            32 -> "Feb"
                            60 -> "Mar"
                            91 -> "Abr"
                            121 -> "May"
                            152 -> "Jun"
                            182 -> "Jul"
                            213 -> "Ago"
                            244 -> "Sep"
                            274 -> "Oct"
                            305 -> "Nov"
                            335 -> "Dic"
                            else -> {
                                ""
                            }
                        }
                        add(AxisValue(i.toFloat()).apply {
                            setLabel(mesString)
                            setTextSize(10)
                        })
                    }
                }
            }
        }

        // Configuración del eje Y (valor)
        val axisY = Axis().apply {
            setHasLines(true)
            val formatter = SimpleAxisValueFormatter()
            formatter.appendedText = "k".toCharArray()
            setFormatter(formatter)
            setMaxLabelChars(5)
        }

        lineData.lines = lineas // Asignar la lista de líneas al LineChartData
        lineData.axisXBottom = axisX
        lineData.axisYLeft = axisY

        chartView.lineChartData = lineData
        val noData = binding.noDataAn
        if(isChartEmpty(chartView)){
            noData.visibility = View.VISIBLE
            chartView.visibility = View.INVISIBLE
        } else {
            // Ocultar el texto  aviso
            noData.visibility = View.GONE
            chartView.visibility = View.VISIBLE
        }
    }

    fun primerDiaMes(dia: Int): Boolean{
        val primerosDias = listOf<Int>(1, 32,60,91,121,152,182,213,244,274,305,335)
        return dia in primerosDias
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generarPuntosAn(datos: List<Pair<String, Double>>): MutableList<PointValue> {
        val puntos = mutableListOf<PointValue>()

        datos.forEachIndexed { index, (fecha, valor) ->
            val x = diaAnio(fecha)
            val y = valor.toFloat() / 1000  // Dividir los valores entre 1000
            puntos.add(PointValue(x, y))
        }
        return puntos
    }

    fun diaAnio(fecha: String): Float{
        val partes = fecha.split("-")
        val meses = partes[1].toInt()
        val dias = partes[2].toInt()
        var diaDelAnio = dias
        diaDelAnio += when (meses){
            2 -> 30
            3 -> 59
            4 -> 90
            5 -> 120
            6 -> 151
            7 -> 181
            8 -> 212
            9 -> 243
            10 -> 273
            11 ->304
            12 ->334
            else -> {0}
        }
        Log.d("FragmentReporte", "fecha: $fecha, diaAnio: $diaDelAnio")
        return diaDelAnio.toFloat()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun organizar(lista: List<Pair<String, Double>>): List<Pair<String, Double>>{
        return lista.sortedBy {
            LocalDate.parse(it.first, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
    }
    fun getColorCategoria(categoria: String): Int {
        val categoriasColores = mapOf(
            "disponible" to "#87EE2B",
            "Gastos Hormiga" to "#F66B6B",
            "Alimentos" to "#FF66C1",
            "Transporte" to "#339AF0",
            "Servicios" to "#EEB62B",
            "Mercado" to "#FD8435"
        )
        val color = Color.parseColor(categoriasColores[categoria])
        return color // Devuelve el color correspondiente a la categoría
    }

    fun isChartEmpty(lineChartView: LineChartView): Boolean {
        val lineChartData = lineChartView.lineChartData
        if (lineChartData != null) {
            val lines = lineChartData.lines
            if (lines != null && lines.isNotEmpty()) {
                if(lines.size > 1) {
                    for (line in lines) {
                        if (line.values.size > 1) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}