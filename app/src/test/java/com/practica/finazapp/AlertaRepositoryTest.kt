package com.practica.finazapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.DAOS.AlertaDao
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.Repositorios.AlertaRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Mock
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals

class AlertaRepositoryTest {

    @Mock
    private lateinit var alertaDao: AlertaDao
    private lateinit var repository: AlertaRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = AlertaRepository(alertaDao)
    }

    @Test
    fun `getAllAlertas should return LiveData list of alertas`() {
        val expectedAlertas = MutableLiveData<List<Alerta>>()
        `when`(alertaDao.getAll()).thenReturn(expectedAlertas)

        val result = repository.getAllAlertas()
        assertEquals(expectedAlertas, result)
    }

    @Test
    fun `getAlertasPorUsuario should return LiveData list of alertas for specific user`() {
        val usuarioId = 1L
        val expectedAlertas = MutableLiveData<List<Alerta>>()
        `when`(alertaDao.getAlertasPorUsuario(usuarioId)).thenReturn(expectedAlertas)

        val result = repository.getAlertasPorUsuario(usuarioId)
        assertEquals(expectedAlertas, result)
    }

    @Test
    fun `getAlertasDeEsteAno should return LiveData list of alertas for current year`() {
        val usuarioId = 1L
        val expectedAlertas = MutableLiveData<List<Alerta>>()
        `when`(alertaDao.getAlertasDeEsteAno(usuarioId)).thenReturn(expectedAlertas)

        val result = repository.getAlertasDeEsteAno(usuarioId)
        assertEquals(expectedAlertas, result)
    }

    @Test
    fun `getAlertasDeEsteMes should return LiveData list of alertas for current month`() {
        val usuarioId = 1L
        val expectedAlertas = MutableLiveData<List<Alerta>>()
        `when`(alertaDao.getAlertasDeEsteMes(usuarioId)).thenReturn(expectedAlertas)

        val result = repository.getAlertasDeEsteMes(usuarioId)
        assertEquals(expectedAlertas, result)
    }

    @Test
    fun `eliminarAlerta should call eliminarAlerta method of alertaDao`() {
        val id = 1L
        repository.eliminarAlerta(id)
        verify(alertaDao).eliminarAlerta(id)
    }

    @Test
    fun `modificarAlerta should call modificarAlerta method of alertaDao`() {
        val id = 1L
        val fecha = "2024-11-15"
        val valor = 100.0

        repository.modificarAlerta(fecha, valor, id)
        verify(alertaDao).modificarAlerta(fecha, valor, id)
    }

    @Test
    fun `truncarAlertas should call truncarAlertas method of alertaDao`() {
        repository.truncarAlertas()
        verify(alertaDao).truncarAlertas()
    }

    @Test
    fun `insertAlerta should call insert method of alertaDao`() = runBlocking {
        val alerta = Alerta(nombre = "Aviso de presupuesto", descripcion = "Presupuesto alcanzado", fecha = "2024-11-20", valor = 200.0, idUsuario = 1L)

        repository.insertAlerta(alerta)
        verify(alertaDao).insert(alerta)
    }
}