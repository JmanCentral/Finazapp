package com.practica.finazapp

import androidx.lifecycle.MutableLiveData
import com.practica.finazapp.DAOS.UsuarioDao
import com.practica.finazapp.Entidades.Usuario
import com.practica.finazapp.Repositorios.UsuarioRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Mock

class UsuarioRepositoryTest {

    @Mock
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var repository: UsuarioRepository

    @Before
    fun setUp() {
        // Inicializar los mocks
        MockitoAnnotations.openMocks(this)
        repository = UsuarioRepository(usuarioDao)
    }

    @Test
    fun `getAllUsuarios should return LiveData list of usuarios`() {
        // Simular el comportamiento del DAO
        val expectedUsuarios = MutableLiveData<List<Usuario>>()
        `when`(usuarioDao.getAll()).thenReturn(expectedUsuarios)

        // Llamar al método del repositorio y verificar el resultado
        val result = repository.getAllUsuarios()
        assertEquals(expectedUsuarios, result)
    }

    @Test
    fun `insertUsuario should call insert method of usuarioDao`() = runBlocking {
        val usuario = Usuario(usuario = "testUser", contrasena = "1234", nombres = "Test", apellidos = "User", correo = "test@example.com", telefono = "123456789", documento = "12345678")

        // Llamar al método del repositorio
        repository.insertUsuario(usuario)

        // Verificar que el método insert del DAO se llamó con el usuario correcto
        verify(usuarioDao).insert(usuario)
    }

    @Test
    fun `getUsuarioPorId should return LiveData of a single usuario`() {
        val expectedUsuario = MutableLiveData<Usuario>()
        `when`(usuarioDao.getUsuarioPorId(1L)).thenReturn(expectedUsuario)

        val result = repository.getUsuarioPorId(1L)
        assertEquals(expectedUsuario, result)
    }

    @Test
    fun `actualizarUsuario should call actualizarUsuario method of usuarioDao`() {
        repository.actualizarUsuario(1L, "testUser", "Test", "User", "12345678", "test@example.com", "123456789")

        // Verificar que el método actualizarUsuario del DAO se llamó con los argumentos correctos
        verify(usuarioDao).actualizarUsuario(1L, "testUser", "Test", "User", "12345678", "test@example.com", "123456789")
    }

    @Test
    fun `eliminarUsuario should call eliminarUsuario method of usuarioDao`() {
        repository.eliminarUsuario(1L)

        // Verificar que el método eliminarUsuario del DAO se llamó con el argumento correcto
        verify(usuarioDao).eliminarUsuario(1L)
    }

    @Test
    fun `cambiarContrasena should call cambiarContrasena method of usuarioDao`() {
        repository.cambiarContrasena("newPassword", 1L)

        // Verificar que el método cambiarContrasena del DAO se llamó con los argumentos correctos
        verify(usuarioDao).cambiarContrasena("newPassword", 1L)
    }
}
