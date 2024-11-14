package com.practica.finazapp.TesteoViewModels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.Repositorios.AlertaRepository
import com.practica.finazapp.ViewModel.AlertaViewModel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.RETURNS_DEEP_STUBS
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class AlertaViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var alertaViewModel: AlertaViewModel

    @Mock
    private lateinit var alertaRepository: AlertaRepository


    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        alertaRepository = mock(AlertaRepository::class.java)
        alertaViewModel = AlertaViewModel(mock(Application::class.java, RETURNS_DEEP_STUBS), alertaRepository)

    }

    @Test
    fun `test insertAlerta calls repository to insert alert`() {
        val alerta = Alerta(0, "Recordatorio de Pago", "Pagar el alquiler", "2024-11-30", 500.0, 1L)

        alertaViewModel.insertAlerta(alerta)

        verify(alertaRepository).insertAlerta(alerta)
    }

    @Test
    fun `test getAlertasDeEsteMes returns expected list of alerts`() {
        val usuarioId = 1L
        val alertas = listOf(
            Alerta(1, "Pago de servicios", "Pagar luz y agua", "2024-11-15", 120.0, usuarioId),
            Alerta(2, "Pago de tarjeta", "Pagar tarjeta de crédito", "2024-11-20", 300.0, usuarioId)
        )
        val alertasLiveData = MutableLiveData<List<Alerta>>()
        alertasLiveData.value = alertas

        `when`(alertaRepository.getAlertasDeEsteMes(usuarioId)).thenReturn(alertasLiveData)

        val result: LiveData<List<Alerta>> = alertaViewModel.getAlertasDeEsteMes(usuarioId)
        assertNotNull(result)
        assertEquals(alertas, result.value)
    }

    @Test
    fun `test eliminarAlerta calls repository to delete alert`() {
        val alertaId = 1L

        alertaViewModel.eliminarAlerta(alertaId)

        verify(alertaRepository).eliminarAlerta(alertaId)
    }

    @Test
    fun `test modificarAlerta calls repository to modify alert`() {
        val alertaId = 1L
        val nuevaFecha = "2024-12-01"
        val nuevoValor = 600.0

        alertaViewModel.modificarAlerta(nuevaFecha, nuevoValor, alertaId)

        verify(alertaRepository).modificarAlerta(nuevaFecha, nuevoValor, alertaId)
    }
}

