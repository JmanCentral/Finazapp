package com.practica.finazapp.TesteoViewModels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.Entidades.Ingreso
import com.practica.finazapp.Repositorios.IngresoRepository
import com.practica.finazapp.ViewModel.IngresoViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class IngresoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var ingresoViewModel: IngresoViewModel

    @Mock
    private lateinit var ingresoRepository: IngresoRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        ingresoRepository = mock(IngresoRepository::class.java)
        ingresoViewModel = IngresoViewModel(mock(Application::class.java, RETURNS_DEEP_STUBS), ingresoRepository)
    }

    @Test
    fun `test insertIngreso calls repository insert`() = runBlocking {
        val ingreso = Ingreso(1, "Salario", 1500.0, "2023-11-01", "Mensual", 1L)

        ingresoViewModel.insertIngreso(ingreso)

        verify(ingresoRepository, times(1)).insertIngreso(ingreso)
    }

    @Test
    fun `test getIngMesDeEsteMes returns expected ingresos`() {
        val usuarioId = 1L
        val ingresos = listOf(
            Ingreso(1, "Salario", 1500.0, "2023-11-01", "Mensual", usuarioId),
            Ingreso(2, "Freelance", 500.0, "2023-11-02", "Casual", usuarioId)
        )
        val ingresosLiveData = MutableLiveData<List<Ingreso>>()
        ingresosLiveData.value = ingresos

        `when`(ingresoRepository.getIngMesDeEsteMes(usuarioId)).thenReturn(ingresosLiveData)

        val result: LiveData<List<Ingreso>> = ingresoViewModel.getIngMesDeEsteMes(usuarioId)
        assertNotNull(result)
        assertEquals(ingresos, result.value)
    }

    @Test
    fun `test modificarIngreso calls repository modificarIngreso`() = runBlocking {
        val ingresoId = 1L
        val newValor = 2000.0
        val newFecha = "2023-12-01"

        ingresoViewModel.modificarIngreso(newFecha, newValor, ingresoId)

        verify(ingresoRepository, times(1)).modificarIngreso(newFecha, newValor, ingresoId)
    }

    @Test
    fun `test eliminarIngreso calls repository eliminarIngreso`() = runBlocking {
        val ingresoId = 1L

        ingresoViewModel.eliminarIngreso(ingresoId)

        verify(ingresoRepository, times(1)).eliminarIngreso(ingresoId)
    }

    @Test
    fun `test getProyectar returns expected value`() {
        val usuarioId = 1L
        val proyectadoValor = 5000.0
        val proyectadoLiveData = MutableLiveData<Double>()
        proyectadoLiveData.value = proyectadoValor

        `when`(ingresoRepository.proyectarIngresosMensuales(usuarioId)).thenReturn(proyectadoLiveData)

        val result: LiveData<Double> = ingresoViewModel.getProyectar(usuarioId)
        assertNotNull(result)
        assertEquals(proyectadoValor, result.value)
    }

    @Test
    fun `test getIngCasDeEsteMes returns expected ingresos`() {
        val usuarioId = 1L
        val ingresos = listOf(
            Ingreso(1, "Ingreso Casual 1", 100.0, "2024-11-01", "Casual", usuarioId),
            Ingreso(2, "Ingreso Casual 2", 200.0, "2024-11-15", "Casual", usuarioId)
        )
        val ingresosLiveData = MutableLiveData<List<Ingreso>>()
        ingresosLiveData.value = ingresos

        `when`(ingresoRepository.getIngCasDeEsteMes(usuarioId)).thenReturn(ingresosLiveData)

        val result: LiveData<List<Ingreso>> = ingresoViewModel.getIngCasDeEsteMes(usuarioId)
        assertNotNull(result)
        assertEquals(ingresos, result.value)
    }

    @Test
    fun `test getIngTotalDeEsteMes returns expected total`() {
        val usuarioId = 1L
        val total = 300.0
        val totalLiveData = MutableLiveData<Double>()
        totalLiveData.value = total

        `when`(ingresoRepository.getIngTotalDeEsteMes(usuarioId)).thenReturn(totalLiveData)

        val result: LiveData<Double> = ingresoViewModel.getIngTotalDeEsteMes(usuarioId)
        assertNotNull(result)
        assertEquals(total, result.value)
    }

    @Test
    fun `test verificacion returns expected ingresos`() {
        val usuarioId = 1L
        val ingresos = listOf(
            Ingreso(1, "Verificado Ingreso 1", 150.0, "2024-11-10", "Mensual", usuarioId),
            Ingreso(2, "Verificado Ingreso 2", 250.0, "2024-11-20", "Casual", usuarioId)
        )
        val ingresosLiveData = MutableLiveData<List<Ingreso>>()
        ingresosLiveData.value = ingresos

        `when`(ingresoRepository.verificacion(usuarioId)).thenReturn(ingresosLiveData)

        val result: LiveData<List<Ingreso>> = ingresoViewModel.verificacion(usuarioId)
        assertNotNull(result)
        assertEquals(ingresos, result.value)
    }

    @Test
    fun `test getIngresosMensuales returns expected ingresos for month`() {
        val usuarioId = 1L
        val anio = "2024"
        val mes = "11"
        val ingresos = listOf(
            Ingreso(1, "Ingreso Mensual 1", 120.0, "2024-11-01", "Mensual", usuarioId),
            Ingreso(2, "Ingreso Mensual 2", 180.0, "2024-11-15", "Mensual", usuarioId)
        )
        val ingresosLiveData = MutableLiveData<List<Ingreso>>()
        ingresosLiveData.value = ingresos

        `when`(ingresoRepository.getIngresosMensuales(usuarioId, anio, mes)).thenReturn(ingresosLiveData)

        val result: LiveData<List<Ingreso>> = ingresoViewModel.getIngresosMensuales(usuarioId, anio, mes)
        assertNotNull(result)
        assertEquals(ingresos, result.value)
    }


}
