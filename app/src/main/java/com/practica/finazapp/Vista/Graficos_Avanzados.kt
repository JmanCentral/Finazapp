package com.practica.finazapp.Vista
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentGraficosAvanzadosBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class Graficos_Avanzados : Fragment() {
    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var ingresoViewModel: IngresoViewModel
    private var disponible: Double = 0.0
    private var _binding: FragmentGraficosAvanzadosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraficosAvanzadosBinding.inflate(inflater, container, false)
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
                cargarLineChart()




            }
        }
    }

    private fun cargarDatos() {
        val categorias = listOf("Gastos Hormiga", "Alimentos", "Transporte", "Servicios", "Mercado")
        val datosGrafico = mutableMapOf<String, Float>()

        // Obtener y almacenar el valor disponible
        gastosViewModel.getDisponible(usuarioId).observe(viewLifecycleOwner) { disp ->
            if (disp != null) {
                disponible = disp.toFloat().toDouble()
                datosGrafico["Disponible"] = disponible.toFloat()
                cargarGraficoAvanzado(datosGrafico)
                cargarGraficoRadar(datosGrafico)
                cargarLineChart()
            }
        }

        // Recorrer las categorías para agregar sus valores al gráfico
        for (categoria in categorias) {
            gastosViewModel.getValorGastosMesCategoria(usuarioId, categoria).observe(viewLifecycleOwner) { cantidad ->
                if (cantidad != null) {
                    datosGrafico[categoria] = cantidad.toFloat() // Guarda los valores en el mapa
                    cargarGraficoAvanzado(datosGrafico) // Recargar gráfico combinado
                    cargarGraficoRadar(datosGrafico) // Recargar gráfico radar
                    cargarLineChart()
                }
            }
        }

        // Obtener el valor total de gastos del mes
        gastosViewModel.getValorGastosMes(usuarioId).observe(viewLifecycleOwner) { gastosMes ->
            if (gastosMes != null) {
                cargarGraficoAvanzado(datosGrafico)
                cargarGraficoRadar(datosGrafico)
                cargarLineChart()
            }
        }
    }


    private fun cargarGraficoAvanzado(datos: Map<String, Float>) {
        val combinedChart = binding.dona2

        val barEntriesList = mutableListOf<BarEntry>()
        val categories = mutableListOf<String>()
        var index = 0f


        val colorMap = mapOf(
            "Gastos Hormiga" to Color.parseColor("#F66B6B"),
            "Alimentos" to Color.parseColor("#FF66C1"),
            "Transporte" to Color.parseColor("#339AF0"),
            "Servicios" to Color.parseColor("#EEB62B"),
            "Mercado" to Color.parseColor("#FD8435"),
            "Disponible" to Color.parseColor("#87EE2B")
        )

        val barDataSets = mutableListOf<BarDataSet>()

        for ((categoria, cantidad) in datos) {
            val barEntries = listOf(BarEntry(index, cantidad))
            val barDataSet = BarDataSet(barEntries, categoria)
            barDataSet.color = colorMap[categoria] ?: Color.BLACK
            barDataSet.setDrawValues(true)
            barDataSet.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return String.format("$%.2f", value)
                }
            }
            barDataSet.valueTextColor = Color.BLACK
            barDataSet.valueTextSize = 12f
            barDataSet.barBorderWidth = 0.9f

            barDataSets.add(barDataSet)
            categories.add(categoria)
            index += 1f
        }

        val barData = BarData(barDataSets as List<IBarDataSet>?)
        barData.barWidth = 0.9f

        val combinedData = CombinedData()
        combinedData.setData(barData)

        val xAxis = combinedChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(categories)
        xAxis.labelCount = categories.size
        xAxis.setAvoidFirstLastClipping(true)
        xAxis.spaceMin = 0.5f
        xAxis.spaceMax = 0.5f
        xAxis.setDrawGridLines(false)


        xAxis.textColor = Color.BLACK
        xAxis.textSize = 12f


        xAxis.axisMinimum = -0.5f
        xAxis.axisMaximum = categories.size - 0.5f
        combinedChart.data = combinedData


        val legend = combinedChart.legend
        legend.isEnabled = true
        legend.form = Legend.LegendForm.SQUARE
        legend.textSize = 12f
        legend.textColor = Color.BLACK
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)

        combinedChart.animateY(1000)
        combinedChart.invalidate()
    }


    private fun cargarGraficoRadar(datos: Map<String, Float>) {
        val radarChart = binding.radarChart

        val radarEntries = mutableListOf<RadarEntry>()
        val labels = mutableListOf<String>()

        for ((categoria, cantidad) in datos) {
            radarEntries.add(RadarEntry(cantidad))
            labels.add(categoria)
        }

        val radarDataSet = RadarDataSet(radarEntries, "Gastos por Categoría")
        radarDataSet.color = Color.BLUE
        radarDataSet.fillColor = Color.BLACK
        radarDataSet.setDrawFilled(true)
        radarDataSet.lineWidth = 2f
        radarDataSet.valueTextColor = Color.BLACK
        radarDataSet.valueTextSize = 12f

        val radarData = RadarData(radarDataSet)
        radarChart.data = radarData

        // Configurar el eje X
        val xAxis = radarChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.textSize = 12f
        xAxis.textColor = Color.BLACK
        xAxis.axisLineColor = Color.BLACK // Cambiar el color del eje X

        // Configurar el eje Y
        val yAxis = radarChart.yAxis
        yAxis.axisMinimum = 0f
        yAxis.axisLineColor = Color.GREEN // Cambiar el color del eje Y

        // Eliminar la leyenda
        radarChart.legend.isEnabled = false

        // Actualizar el gráfico
        radarChart.invalidate()
    }




    private fun cargarLineChart() {
        val lineChart = binding.lineChart


        val fechaInicio = "2024-01-01"
        val fechaFin = "2024-12-31"

        gastosViewModel.getGastosPorFechas(usuarioId, fechaInicio, fechaFin).observe(viewLifecycleOwner) { gastos ->
            if (gastos != null && gastos.isNotEmpty()) {
                val entries = mutableListOf<Entry>()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


                for (gasto in gastos) {
                    val fecha = dateFormat.parse(gasto.fecha)?.time ?: 0L
                    entries.add(Entry(fecha.toFloat(), gasto.valor.toFloat()))
                }


                val lineDataSet = LineDataSet(entries, "Gastos")
                lineDataSet.color = Color.BLUE
                lineDataSet.valueTextColor = Color.BLACK

                // Configurar la línea como cúbica
                lineDataSet.lineWidth = 2f
                lineDataSet.setDrawCircles(true)
                lineDataSet.setDrawValues(true)
                lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

                val lineData = LineData(lineDataSet)

                lineChart.data = lineData
                lineChart.invalidate()


                val xAxis = lineChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return dateFormat.format(Date(value.toLong()))
                    }
                }
                xAxis.setDrawGridLines(false)

                // Configuración adicional del gráfico
                lineChart.description.text = "Gastos por Fecha"
                lineChart.axisRight.isEnabled = false
            }
        }
    }

}









