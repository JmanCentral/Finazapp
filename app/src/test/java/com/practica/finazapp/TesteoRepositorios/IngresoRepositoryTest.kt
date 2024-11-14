package com.practica.finazapp.TesteoRepositorios

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
        val ingreso = Ingreso(descripcion = "Salario", fecha = "2024-11-20", valor = 1000.0, tipo = "Mensual", idUsuario = 1L)

        repository.insertIngreso(ingreso)
        verify(ingresoDao).insert(ingreso)
    }

    @Test
    fun `getIngMesDeEsteMes should return LiveData list of ingresos for current month`() {
        val usuarioId = 1L
        val expectedIngresos = MutableLiveData<List<Ingreso>>()
        `when`(ingresoDao.getIngMesDeEsteMes(usuarioId)).thenReturn(expectedIngresos)

        val result = repository.getIngMesDeEsteMes(usuarioId)
        assertEquals(expectedIngresos, result)
    }

    @Test
    fun `getIngCasDeEsteMes should return LiveData list of casual ingresos for current month`() {
        val usuarioId = 1L
        val expectedIngresos = MutableLiveData<List<Ingreso>>()
        `when`(ingresoDao.getIngCasDeEsteMes(usuarioId)).thenReturn(expectedIngresos)

        val result = repository.getIngCasDeEsteMes(usuarioId)
        assertEquals(expectedIngresos, result)
    }

    @Test
    fun `getIngTotalDeEsteMes should return LiveData total ingresos for current month`() {
        val usuarioId = 1L
        val expectedTotal = MutableLiveData<Double>()
        `when`(ingresoDao.getIngTotalDeEsteMes(usuarioId)).thenReturn(expectedTotal)

        val result = repository.getIngTotalDeEsteMes(usuarioId)
        assertEquals(expectedTotal, result)
    }

    @Test
    fun `verificacion should return LiveData list of ingresos for verification`() {
        val usuarioId = 1L
        val expectedIngresos = MutableLiveData<List<Ingreso>>()
        `when`(ingresoDao.verificacion(usuarioId)).thenReturn(expectedIngresos)

        val result = repository.verificacion(usuarioId)
        assertEquals(expectedIngresos, result)
    }

    @Test
    fun `getIngresosMensuales should return LiveData list of monthly ingresos`() {
        val usuarioId = 1L
        val anio = "2024"
        val mes = "11"
        val expectedIngresos = MutableLiveData<List<Ingreso>>()
        `when`(ingresoDao.getIngresosMensuales(usuarioId, anio, mes)).thenReturn(expectedIngresos)

        val result = repository.getIngresosMensuales(usuarioId, anio, mes)
        assertEquals(expectedIngresos, result)
    }

    @Test
    fun `truncarIngresos should call truncarIngresos method of ingresoDao`() {
        repository.truncarIngresos()
        verify(ingresoDao).truncarIngresos()
    }

    @Test
    fun `modificarIngreso should call modificarIngreso method of ingresoDao`() {
        val fecha = "2024-11-15"
        val valor = 500.0
        val id = 1L

        repository.modificarIngreso(fecha, valor, id)
        verify(ingresoDao).modificarIngreso(fecha, valor, id)
    }

    @Test
    fun `eliminarIngreso should call eliminarIngreso method of ingresoDao`() {
        val id = 1L

        repository.eliminarIngreso(id)
        verify(ingresoDao).eliminarIngreso(id)
    }

    @Test
    fun `desactivarIngPasado should call desactivarIngPasado method of ingresoDao`() {
        val descripcion = "Ingreso pasado"

        repository.desactivarIngPasado(descripcion)
        verify(ingresoDao).desactivarIngPasado(descripcion)
    }

    @Test
    fun `getTotalIngresosPorTipo should return LiveData total ingresos by type`() {
        val usuarioId = 1L
        val tipo = "Mensual"
        val expectedTotal = MutableLiveData<Double>()
        `when`(ingresoDao.getTotalIngresosPorTipo(usuarioId, tipo)).thenReturn(expectedTotal)

        val result = repository.getTotalIngresosPorTipo(usuarioId, tipo)
        assertEquals(expectedTotal, result)
    }

    @Test
    fun `getIngTotalMesAnterior should return LiveData total ingresos for previous month`() {
        val usuarioId = 1L
        val expectedTotal = MutableLiveData<Double>()
        `when`(ingresoDao.getIngTotalMesAnterior(usuarioId)).thenReturn(expectedTotal)

        val result = repository.getIngTotalMesAnterior(usuarioId)
        assertEquals(expectedTotal, result)
    }

    @Test
    fun `proyectarIngresosMensuales should return LiveData projected monthly ingresos`() {
        val usuarioId = 1L
        val expectedProjection = MutableLiveData<Double>()
        `when`(ingresoDao.proyectarIngresosMensuales(usuarioId)).thenReturn(expectedProjection)

        val result = repository.proyectarIngresosMensuales(usuarioId)
        assertEquals(expectedProjection, result)
    }
}
