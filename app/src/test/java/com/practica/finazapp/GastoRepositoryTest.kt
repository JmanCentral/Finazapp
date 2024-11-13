package com.practica.finazapp

import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.DAOS.CategoriaTotal
import com.practica.finazapp.DAOS.GastoDao
import com.practica.finazapp.DAOS.GastoPromedio
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.Repositorios.GastoRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Mock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals

class GastoRepositoryTest {

    @Mock
    private lateinit var gastoDao: GastoDao
    private lateinit var repository: GastoRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = GastoRepository(gastoDao)
    }

    @Test
    fun `getAllGastos should return LiveData list of gastos`() {
        val expectedGastos = MutableLiveData<List<Gasto>>()
        `when`(gastoDao.getAll()).thenReturn(expectedGastos)

        val result = repository.getAllGastos()
        assertEquals(expectedGastos, result)
    }

    @Test
    fun `insertGasto should call insert method of gastoDao`() = runBlocking {
        val gasto = Gasto(descripcion = "Alquiler", categoria = "Vivienda", fecha = "2024-11-01", valor = 500.0, idUsuario = 1L)

        repository.insertGasto(gasto)
        verify(gastoDao).insert(gasto)
    }

    @Test
    fun `getDisponible should return LiveData of available balance for user`() {
        val expectedBalance = MutableLiveData<Double>()
        `when`(gastoDao.getDisponible(1L)).thenReturn(expectedBalance)

        val result = repository.getDisponible(1L)
        assertEquals(expectedBalance, result)
    }

    @Test
    fun `getValorGastosMes should return LiveData of total gastos for current month`() {
        val expectedTotalGastos = MutableLiveData<Double>()
        `when`(gastoDao.getValorGastosMes(1L)).thenReturn(expectedTotalGastos)

        val result = repository.getValorGastosMes(1L)
        assertEquals(expectedTotalGastos, result)
    }

    @Test
    fun `deleteGasto should call deleteGasto method of gastoDao`() {
        repository.deleteGasto(1L)
        verify(gastoDao).deleteGasto(1L)
    }

    @Test
    fun `modificarGasto should call modificarGasto method of gastoDao`() {
        repository.modificarGasto(1L, "Vivienda", 600.0, "Alquiler Noviembre", "2024-11-01")
        verify(gastoDao).modificarGasto(1L, "Vivienda", 600.0, "Alquiler Noviembre", "2024-11-01")
    }

    @Test
    fun `getGastoMasAlto should return LiveData of highest gasto for user`() {
        val expectedGasto = MutableLiveData<Gasto>()
        `when`(gastoDao.getGastoMasAlto(1L)).thenReturn(expectedGasto)

        val result = repository.getGastoMasAlto(1L)
        assertEquals(expectedGasto, result)
    }

    @Test
    fun `getCategoriasConMasGastos should return LiveData of categories with highest gastos`() {
        val expectedCategorias = MutableLiveData<List<CategoriaTotal>>()
        `when`(gastoDao.getCategoriasConMasGastos(1L)).thenReturn(expectedCategorias)

        val result = repository.getCategoriasConMasGastos(1L)
        assertEquals(expectedCategorias, result)
    }

    @Test
    fun `getpromediodiario should return LiveData of daily gasto average by category`() {
        val expectedPromedio = MutableLiveData<List<GastoPromedio>>()
        `when`(gastoDao.getGastoPromedioPorCategoria(1L)).thenReturn(expectedPromedio)

        val result = repository.getpromediodiario(1L)
        assertEquals(expectedPromedio, result)
    }
}
