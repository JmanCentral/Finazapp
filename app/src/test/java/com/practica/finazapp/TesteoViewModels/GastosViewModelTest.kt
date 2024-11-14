package com.practica.finazapp.TesteoViewModels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.DAOS.CategoriaTotal
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.Repositorios.GastoRepository
import com.practica.finazapp.Repositorios.IngresoRepository
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class GastosViewModelTest {

    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var gastoViewModel: GastosViewModel

    @Mock
    private lateinit var gastoRepository: GastoRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        gastoRepository = mock(GastoRepository::class.java)
        gastoViewModel = GastosViewModel(mock(Application::class.java, RETURNS_DEEP_STUBS),gastoRepository)
    }

    @Test
    fun `test insertGasto calls repository insert method`() = runTest {
        val gasto = Gasto(1, "Compra", "Alimentación", "2024-11-10", 100.0, 1L)

        gastoViewModel.insertGasto(gasto)

        verify(gastoRepository).insertGasto(gasto)
    }

    @Test
    fun `test getDisponible returns expected value`() {
        val usuarioId = 1L
        val disponible = 500.0
        val disponibleLiveData = MutableLiveData<Double>()
        disponibleLiveData.value = disponible

        `when`(gastoRepository.getDisponible(usuarioId)).thenReturn(disponibleLiveData)

        val result: LiveData<Double> = gastoViewModel.getDisponible(usuarioId)
        assertNotNull(result)
        assertEquals(disponible, result.value)
    }

    @Test
    fun `test getValorGastosMes returns expected value`() {
        val usuarioId = 1L
        val valorTotal = 300.0
        val valorLiveData = MutableLiveData<Double>()
        valorLiveData.value = valorTotal

        `when`(gastoRepository.getValorGastosMes(usuarioId)).thenReturn(valorLiveData)

        val result: LiveData<Double> = gastoViewModel.getValorGastosMes(usuarioId)
        assertNotNull(result)
        assertEquals(valorTotal, result.value)
    }

    @Test
    fun `test getValorGastosMesCategoria returns expected value`() {
        val usuarioId = 1L
        val categoria = "Alimentación"
        val valorCategoria = 150.0
        val valorCategoriaLiveData = MutableLiveData<Double>()
        valorCategoriaLiveData.value = valorCategoria

        `when`(gastoRepository.getValorGastosMesCategoria(usuarioId, categoria)).thenReturn(valorCategoriaLiveData)

        val result: LiveData<Double> = gastoViewModel.getValorGastosMesCategoria(usuarioId, categoria)
        assertNotNull(result)
        assertEquals(valorCategoria, result.value)
    }

    @Test
    fun `test getGastosMesCategoria returns expected gastos`() {
        val usuarioId = 1L
        val categoria = "Alimentación"
        val gastos = listOf(
            Gasto(1, "Compra Supermercado", categoria, "2024-11-10", 100.0, usuarioId),
            Gasto(2, "Comida Restaurante", categoria, "2024-11-15", 50.0, usuarioId)
        )
        val gastosLiveData = MutableLiveData<List<Gasto>>()
        gastosLiveData.value = gastos

        `when`(gastoRepository.getGastosMesCategoria(usuarioId, categoria)).thenReturn(gastosLiveData)

        val result: LiveData<List<Gasto>> = gastoViewModel.getGastosMesCategoria(usuarioId, categoria)
        assertNotNull(result)
        assertEquals(gastos, result.value)
    }

    @Test
    fun `test deleteGasto calls repository delete method`() {
        val gastoId = 1L

        gastoViewModel.deleteGasto(gastoId)

        verify(gastoRepository).deleteGasto(gastoId)
    }

    @Test
    fun `test modificarGasto calls repository modify method`() {
        val gastoId = 1L
        val categoria = "Alimentación"
        val valor = 120.0
        val descripcion = "Cena en restaurante"
        val fecha = "2024-11-15"

        gastoViewModel.modificarGasto(gastoId, categoria, valor, descripcion, fecha)

        verify(gastoRepository).modificarGasto(gastoId, categoria, valor, descripcion, fecha)
    }

    @Test
    fun `test getGastosPorFechas returns expected gastos`() {
        val usuarioId = 1L
        val fechaInf = "2024-11-01"
        val fechaSup = "2024-11-30"
        val gastos = listOf(
            Gasto(1, "Compra", "Alimentación", "2024-11-10", 100.0, usuarioId),
            Gasto(2, "Cena", "Alimentación", "2024-11-15", 50.0, usuarioId)
        )
        val gastosLiveData = MutableLiveData<List<Gasto>>()
        gastosLiveData.value = gastos

        `when`(gastoRepository.getGastosPorFechas(usuarioId, fechaInf, fechaSup)).thenReturn(gastosLiveData)

        val result: LiveData<List<Gasto>> = gastoViewModel.getGastosPorFechas(usuarioId, fechaInf, fechaSup)
        assertNotNull(result)
        assertEquals(gastos, result.value)
    }

    @Test
    fun `test getGastosOrdenadosAsc returns ordered gastos in ascending order`() {
        val usuarioId = 1L
        val gastos = listOf(
            Gasto(2, "Comida", "Alimentación", "2024-11-15", 50.0, usuarioId),
            Gasto(1, "Compra", "Alimentación", "2024-11-10", 100.0, usuarioId)
        )
        val gastosLiveData = MutableLiveData<List<Gasto>>()
        gastosLiveData.value = gastos

        `when`(gastoRepository.getGastosOrdenadosAsc(usuarioId)).thenReturn(gastosLiveData)

        val result: LiveData<List<Gasto>> = gastoViewModel.getGastosOrdenadosAsc(usuarioId)
        assertNotNull(result)
        assertEquals(gastos, result.value)
    }

    @Test
    fun `test getGastosOrdenadosDesc returns ordered gastos in descending order`() {
        val usuarioId = 1L
        val gastos = listOf(
            Gasto(1, "Compra", "Alimentación", "2024-11-10", 100.0, usuarioId),
            Gasto(2, "Comida", "Alimentación", "2024-11-15", 50.0, usuarioId)
        )
        val gastosLiveData = MutableLiveData<List<Gasto>>()
        gastosLiveData.value = gastos

        `when`(gastoRepository.getGastosOrdenadosDesc(usuarioId)).thenReturn(gastosLiveData)

        val result: LiveData<List<Gasto>> = gastoViewModel.getGastosOrdenadosDesc(usuarioId)
        assertNotNull(result)
        assertEquals(gastos, result.value)
    }

    @Test
    fun `test getGastoMasAlto returns the highest gasto`() {
        val usuarioId = 1L
        val gastoMasAlto = Gasto(1, "Compra", "Alimentación", "2024-11-10", 100.0, usuarioId)
        val gastoLiveData = MutableLiveData<Gasto>()
        gastoLiveData.value = gastoMasAlto

        `when`(gastoRepository.getGastoMasAlto(usuarioId)).thenReturn(gastoLiveData)

        val result: LiveData<Gasto> = gastoViewModel.getGastoMasAlto(usuarioId)
        assertNotNull(result)
        assertEquals(gastoMasAlto, result.value)
    }

    @Test
    fun `test getGastoMasBajo returns the lowest gasto`() {
        val usuarioId = 1L
        val gastoMasBajo = Gasto(2, "Comida", "Alimentación", "2024-11-15", 50.0, usuarioId)
        val gastoLiveData = MutableLiveData<Gasto>()
        gastoLiveData.value = gastoMasBajo

        `when`(gastoRepository.getGastoMasBajp(usuarioId)).thenReturn(gastoLiveData)

        val result: LiveData<Gasto> = gastoViewModel.getGastoMasBajo(usuarioId)
        assertNotNull(result)
        assertEquals(gastoMasBajo, result.value)
    }

    @Test
    fun `test getPromedioGastosMes returns expected monthly average`() {
        val usuarioId = 1L
        val promedio = 75.0
        val promedioLiveData = MutableLiveData<Double>()
        promedioLiveData.value = promedio

        `when`(gastoRepository.getPromedioGastosMes(usuarioId)).thenReturn(promedioLiveData)

        val result: LiveData<Double> = gastoViewModel.getPromedioGastosMes(usuarioId)
        assertNotNull(result)
        assertEquals(promedio, result.value)
    }

    @Test
    fun `test getPorcentajesGastosSobreIngresos returns expected percentage`() {
        val usuarioId = 1L
        val porcentaje = 50.0
        val porcentajeLiveData = MutableLiveData<Double>()
        porcentajeLiveData.value = porcentaje

        `when`(gastoRepository.getPorcentajesGastosSobreIngresos(usuarioId)).thenReturn(porcentajeLiveData)

        val result: LiveData<Double> = gastoViewModel.getPorcentajesGastosSobreIngresos(usuarioId)
        assertNotNull(result)
        assertEquals(porcentaje, result.value)
    }

    @Test
    fun `test getCategoriasConMasGastos returns categories with highest expenses`() {
        val usuarioId = 1L
        val categorias = listOf(
            CategoriaTotal("Alimentación", 150.0),
            CategoriaTotal("Entretenimiento", 100.0)
        )
        val categoriasLiveData = MutableLiveData<List<CategoriaTotal>>()
        categoriasLiveData.value = categorias

        `when`(gastoRepository.getCategoriasConMasGastos(usuarioId)).thenReturn(categoriasLiveData)

        val result: LiveData<List<CategoriaTotal>> = gastoViewModel.getCategoriasConMasGastos(usuarioId)
        assertNotNull(result)
        assertEquals(categorias, result.value)
    }

    @Test
    fun `test gastopromediodiario returns expected daily average`() {
        val usuarioId = 1L
        val promedioDiario = 25.0
        val promedioLiveData = MutableLiveData<Double>()
        promedioLiveData.value = promedioDiario

        `when`(gastoRepository.getPromedioDiario(usuarioId)).thenReturn(promedioLiveData)

        val result: LiveData<Double> = gastoViewModel.gastopromediodiario(usuarioId)
        assertNotNull(result)
        assertEquals(promedioDiario, result.value)
    }


}