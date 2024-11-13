package com.practica.finazapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.DAOS.IngresoDao
import com.practica.finazapp.Entidades.Ingreso
import com.practica.finazapp.Repositorios.IngresoRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Mock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals

class IngresoRepositoryTest {

    @Mock
    private lateinit var ingresoDao: IngresoDao
    private lateinit var repository: IngresoRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = IngresoRepository(ingresoDao)
    }

    @Test
    fun `getAllIngresos should return LiveData list of ingresos`() {
        val expectedIngresos = MutableLiveData<List<Ingreso>>()
        `when`(ingresoDao.getAll()).thenReturn(expectedIngresos)

        val result = repository.getAllIngresos()
        assertEquals(expectedIngresos, result)
    }

    @Test
    fun `insertIngreso should call insert method of ingresoDao`() = runBlocking {
        val ingreso = Ingreso(descripcion = "Salario", valor = 1000.0, fecha = "2024-11-01", tipo = "Mensual", idUsuario = 1L)

        repository.insertIngreso(ingreso)
        verify(ingresoDao).insert(ingreso)
    }

    @Test
    fun `getIngMesDeEsteMes should return LiveData of ingresos for current month`() {
        val expectedIngresos = MutableLiveData<List<Ingreso>>()
        `when`(ingresoDao.getIngMesDeEsteMes(1L)).thenReturn(expectedIngresos)

        val result = repository.getIngMesDeEsteMes(1L)
        assertEquals(expectedIngresos, result)
    }

    @Test
    fun `getIngTotalDeEsteMes should return LiveData of total ingresos for current month`() {
        val expectedTotal = MutableLiveData<Double>()
        `when`(ingresoDao.getIngTotalDeEsteMes(1L)).thenReturn(expectedTotal)

        val result = repository.getIngTotalDeEsteMes(1L)
        assertEquals(expectedTotal, result)
    }

    @Test
    fun `modificarIngreso should call modificarIngreso method of ingresoDao`() {
        repository.modificarIngreso("2024-11-01", 1500.0, 1L)
        verify(ingresoDao).modificarIngreso("2024-11-01", 1500.0, 1L)
    }

    @Test
    fun `eliminarIngreso should call eliminarIngreso method of ingresoDao`() {
        repository.eliminarIngreso(1L)
        verify(ingresoDao).eliminarIngreso(1L)
    }

    @Test
    fun `proyectarIngresosMensuales should return LiveData of projected monthly ingresos`() {
        val expectedProjection = MutableLiveData<Double>()
        `when`(ingresoDao.proyectarIngresosMensuales(1L)).thenReturn(expectedProjection)

        val result = repository.proyectarIngresosMensuales(1L)
        assertEquals(expectedProjection, result)
    }
}
