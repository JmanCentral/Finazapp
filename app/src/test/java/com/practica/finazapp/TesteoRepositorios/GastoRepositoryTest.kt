package com.practica.finazapp.TesteoRepositorios

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
        val gasto = Gasto(idUsuario = 1L, valor = 200.0, categoria = "Alimentos", descripcion = "Compra de supermercado", fecha = "2024-11-15")

        repository.insertGasto(gasto)
        verify(gastoDao).insert(gasto)
    }

    @Test
    fun `getDisponible should return LiveData disponible amount`() {
        val usuarioId = 1L
        val expectedDisponible = MutableLiveData<Double>()
        `when`(gastoDao.getDisponible(usuarioId)).thenReturn(expectedDisponible)

        val result = repository.getDisponible(usuarioId)
        assertEquals(expectedDisponible, result)
    }

    @Test
    fun `getValorGastosMes should return LiveData total value of gastos for the month`() {
        val usuarioId = 1L
        val expectedTotal = MutableLiveData<Double>()
        `when`(gastoDao.getValorGastosMes(usuarioId)).thenReturn(expectedTotal)

        val result = repository.getValorGastosMes(usuarioId)
        assertEquals(expectedTotal, result)
    }

    @Test
    fun `getValorGastosMesCategoria should return LiveData total value of gastos for a category`() {
        val usuarioId = 1L
        val categoria = "Transporte"
        val expectedTotal = MutableLiveData<Double>()
        `when`(gastoDao.getValorGastosMesCategoria(usuarioId, categoria)).thenReturn(expectedTotal)

        val result = repository.getValorGastosMesCategoria(usuarioId, categoria)
        assertEquals(expectedTotal, result)
    }

    @Test
    fun `getGastosMesCategoria should return LiveData list of gastos for a category`() {
        val usuarioId = 1L
        val categoria = "Alimentos"
        val expectedGastos = MutableLiveData<List<Gasto>>()
        `when`(gastoDao.getGastosMesCategoria(usuarioId, categoria)).thenReturn(expectedGastos)

        val result = repository.getGastosMesCategoria(usuarioId, categoria)
        assertEquals(expectedGastos, result)
    }

    @Test
    fun `deleteGasto should call deleteGasto method of gastoDao`() {
        val id = 1L

        repository.deleteGasto(id)
        verify(gastoDao).deleteGasto(id)
    }

    @Test
    fun `modificarGasto should call modificarGasto method of gastoDao`() {
        val id = 1L
        val categoria = "Servicios"
        val valor = 150.0
        val descripcion = "Pago de factura"
        val fecha = "2024-11-18"

        repository.modificarGasto(id, categoria, valor, descripcion, fecha)
        verify(gastoDao).modificarGasto(id, categoria, valor, descripcion, fecha)
    }

    @Test
    fun `getGastosPorFechas should return LiveData list of gastos within date range`() {
        val usuarioId = 1L
        val fechaInf = "2024-11-01"
        val fechaSup = "2024-11-30"
        val expectedGastos = MutableLiveData<List<Gasto>>()
        `when`(gastoDao.getGastosPorFechas(usuarioId, fechaInf, fechaSup)).thenReturn(expectedGastos)

        val result = repository.getGastosPorFechas(usuarioId, fechaInf, fechaSup)
        assertEquals(expectedGastos, result)
    }

    @Test
    fun `getGastosOrdenados should return LiveData list of ordered gastos`() {
        val usuarioId = 1L
        val expectedGastos = MutableLiveData<List<Gasto>>()
        `when`(gastoDao.getGastosOrdenados(usuarioId)).thenReturn(expectedGastos)

        val result = repository.getGastosOrdenados(usuarioId)
        assertEquals(expectedGastos, result)
    }

    @Test
    fun `getGastosOrdenadosAsc should return LiveData list of gastos ordered ascending`() {
        val usuarioId = 1L
        val expectedGastos = MutableLiveData<List<Gasto>>()
        `when`(gastoDao.getGastosOrdenadosAsc(usuarioId)).thenReturn(expectedGastos)

        val result = repository.getGastosOrdenadosAsc(usuarioId)
        assertEquals(expectedGastos, result)
    }

    @Test
    fun `getGastosOrdenadosDesc should return LiveData list of gastos ordered descending`() {
        val usuarioId = 1L
        val expectedGastos = MutableLiveData<List<Gasto>>()
        `when`(gastoDao.getGastosOrdenadosDesc(usuarioId)).thenReturn(expectedGastos)

        val result = repository.getGastosOrdenadosDesc(usuarioId)
        assertEquals(expectedGastos, result)
    }

    @Test
    fun `getGastoMasAlto should return LiveData highest gasto`() {
        val usuarioId = 1L
        val expectedGasto = MutableLiveData<Gasto>()
        `when`(gastoDao.getGastoMasAlto(usuarioId)).thenReturn(expectedGasto)

        val result = repository.getGastoMasAlto(usuarioId)
        assertEquals(expectedGasto, result)
    }

    @Test
    fun `getGastoMasBajo should return LiveData lowest gasto`() {
        val usuarioId = 1L
        val expectedGasto = MutableLiveData<Gasto>()
        `when`(gastoDao.getGastoMasBajo(usuarioId)).thenReturn(expectedGasto)

        val result = repository.getGastoMasBajp(usuarioId)
        assertEquals(expectedGasto, result)
    }

    @Test
    fun `getPromedioGastosMes should return LiveData average gastos for the month`() {
        val usuarioId = 1L
        val expectedPromedio = MutableLiveData<Double>()
        `when`(gastoDao.getPromedioGastosMes(usuarioId)).thenReturn(expectedPromedio)

        val result = repository.getPromedioGastosMes(usuarioId)
        assertEquals(expectedPromedio, result)
    }

    @Test
    fun `getGastosRecurrentes should return LiveData list of recurrent gastos`() {
        val usuarioId = 1L
        val expectedGastos = MutableLiveData<List<Gasto>>()
        `when`(gastoDao.getGastosRecurrentes(usuarioId)).thenReturn(expectedGastos)

        val result = repository.getGastosRecurrentes(usuarioId)
        assertEquals(expectedGastos, result)
    }

    @Test
    fun `getPorcentajesGastosSobreIngresos should return LiveData percentage of gastos over ingresos`() {
        val usuarioId = 1L
        val expectedPorcentaje = MutableLiveData<Double>()
        `when`(gastoDao.getPorcentajeGastosSobreIngresos(usuarioId)).thenReturn(expectedPorcentaje)

        val result = repository.getPorcentajesGastosSobreIngresos(usuarioId)
        assertEquals(expectedPorcentaje, result)
    }

    @Test
    fun `getCategoriasConMasGastos should return LiveData list of categories with most gastos`() {
        val usuarioId = 1L
        val expectedCategorias = MutableLiveData<List<CategoriaTotal>>()
        `when`(gastoDao.getCategoriasConMasGastos(usuarioId)).thenReturn(expectedCategorias)

        val result = repository.getCategoriasConMasGastos(usuarioId)
        assertEquals(expectedCategorias, result)
    }

    @Test
    fun `getpromediodiario should return LiveData list of daily average gastos per category`() {
        val usuarioId = 1L
        val expectedPromedio = MutableLiveData<List<GastoPromedio>>()
        `when`(gastoDao.getGastoPromedioPorCategoria(usuarioId)).thenReturn(expectedPromedio)

        val result = repository.getpromediodiario(usuarioId)
        assertEquals(expectedPromedio, result)
    }

    @Test
    fun `getPromedioDiario should return LiveData total daily average gastos`() {
        val usuarioId = 1L
        val expectedPromedio = MutableLiveData<Double>()
        `when`(gastoDao.getGastoPromedioDiarioTotal(usuarioId)).thenReturn(expectedPromedio)

        val result = repository.getPromedioDiario(usuarioId)
        assertEquals(expectedPromedio, result)
    }
}

