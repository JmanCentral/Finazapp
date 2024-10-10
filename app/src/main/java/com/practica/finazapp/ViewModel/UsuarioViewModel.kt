package com.practica.finazapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.DAOS.AppDatabase
import com.practica.finazapp.Entidades.Usuario
import com.practica.finazapp.Repositorios.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsuarioViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData para almacenar la lista de usuarios
    private val allUsuarios: LiveData<List<Usuario>>
    private val repository: UsuarioRepository
    init {
        val userDao = AppDatabase.getDatabase(application).usuarioDao()
        repository = UsuarioRepository(userDao)
        allUsuarios = repository.getAllUsuarios()
    }

    // Función para insertar un nuevo usuario
    fun insertUsuario(usuario: Usuario) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUsuario(usuario)
        }
    }

    fun getUsuarioPorId(id: Long): LiveData<Usuario> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            val usuario = repository.getUsuarioPorId(id)
            emitSource(usuario)
        }
    }


    fun getUsuarioPorUsuario(nombreUsuario: String): LiveData<Usuario?> {
        return liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            // Observa el LiveData del repositorio
            val usuarioLiveData = repository.getUsuarioPorUsuario(nombreUsuario)
            // Emite el valor cuando esté disponible
            emitSource(usuarioLiveData)
        }
    }


    fun getUltimoUsuarioId(): LiveData<Long> {
        return repository.getUltimoUsuarioId()
    }

    fun actualizarUsuario(usuarioId: Long, usuario: String, nombres: String, apellidos: String, documento: String, email: String, numeroTel: String) =
        repository.actualizarUsuario(
            usuarioId, usuario, nombres, apellidos, documento, email, numeroTel
        )


    fun eliminarUsuario(usuarioId: Long) = repository.eliminarUsuario(usuarioId)

    fun cambiarContrasena(contrasena: String, usuarioId: Long) = repository.cambiarContrasena(contrasena,usuarioId)


}