package com.practica.finazapp.TesteoViewModels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.practica.finazapp.Entidades.Usuario
import com.practica.finazapp.Repositorios.UsuarioRepository
import com.practica.finazapp.ViewModel.UsuarioViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class UsuarioViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var usuarioViewModel: UsuarioViewModel
    @Mock
    private lateinit var usuarioRepository: UsuarioRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        usuarioRepository = mock(UsuarioRepository::class.java)
        usuarioViewModel = UsuarioViewModel(mock(Application::class.java, RETURNS_DEEP_STUBS), usuarioRepository)// Simulando el contexto
        usuarioViewModel = spy(usuarioViewModel)
    }

    @Test
    fun `test insertUsuario calls repository insert`() = runBlocking {
        val usuario = Usuario(1, "testUser", "Test", "User", "123456", "test@example.com", "1234567890" , "1234567890")

        usuarioViewModel.insertUsuario(usuario)

        verify(usuarioRepository, times(1)).insertUsuario(usuario)
    }

    @Test
    fun `test getUsuarioPorId returns expected user`() {
        val usuarioId = 1L
        val usuario = Usuario(usuarioId, "testUser", "Test", "User", "123456", "test@example.com", "1234567890" , "1234567890")
        val usuarioLiveData = MutableLiveData<Usuario>()
        usuarioLiveData.value = usuario

        `when`(usuarioRepository.getUsuarioPorId(usuarioId)).thenReturn(usuarioLiveData)

        val result: LiveData<Usuario> = usuarioViewModel.getUsuarioPorId(usuarioId)
        assertNotNull(result)
        assertEquals(usuario, result.value)
    }

    @Test
    fun `test getUsuarioPorUsuario returns expected user`() {
        val nombreUsuario = "testUser"
        val usuario = Usuario(1, nombreUsuario, "Test", "User", "123456", "test@example.com", "1234567890" , "1234567890")
        val usuarioLiveData = MutableLiveData<Usuario?>()
        usuarioLiveData.value = usuario

        `when`(usuarioRepository.getUsuarioPorUsuario(nombreUsuario)).thenReturn(usuarioLiveData)

        val result: LiveData<Usuario?> = usuarioViewModel.getUsuarioPorUsuario(nombreUsuario)
        assertNotNull(result)
        assertEquals(usuario, result.value)
    }

    @Test
    fun `test getUltimoUsuarioId returns expected id`() {
        val ultimoId = 10L
        val idLiveData = MutableLiveData<Long>()
        idLiveData.value = ultimoId

        `when`(usuarioRepository.getUltimoUsuarioId()).thenReturn(idLiveData)

        val result: LiveData<Long> = usuarioViewModel.getUltimoUsuarioId()
        assertNotNull(result)
        assertEquals(ultimoId, result.value)
    }

    @Test
    fun `test actualizarUsuario calls repository actualizarUsuario`() {
        val usuarioId = 1L
        val usuario = "newUser"
        val nombres = "New"
        val apellidos = "User"
        val documento = "654321"
        val email = "new@example.com"
        val numeroTel = "0987654321"

        usuarioViewModel.actualizarUsuario(usuarioId, usuario, nombres, apellidos, documento, email, numeroTel)

        verify(usuarioRepository, times(1)).actualizarUsuario(usuarioId, usuario, nombres, apellidos, documento, email, numeroTel)
    }

    @Test
    fun `test eliminarUsuario calls repository eliminarUsuario`() {
        val usuarioId = 1L

        usuarioViewModel.eliminarUsuario(usuarioId)

        verify(usuarioRepository, times(1)).eliminarUsuario(usuarioId)
    }
}
