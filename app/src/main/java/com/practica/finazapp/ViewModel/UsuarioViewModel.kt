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
    private var repository: UsuarioRepository
    init {
        val userDao = AppDatabase.getDatabase(application).usuarioDao()
        repository = UsuarioRepository(userDao)
        allUsuarios = repository.getAllUsuarios()
    }

    constructor(application: Application, repository: UsuarioRepository) : this(application) {
        this.repository = repository
    }

    // Función para insertar un nuevo usuario
    fun insertUsuario(usuario: Usuario) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUsuario(usuario)
        }
    }

    fun getUsuarioPorId(id: Long): LiveData<Usuario> {

        return repository.getUsuarioPorId(id)
    }


    fun getUsuarioPorUsuario(nombreUsuario: String): LiveData<Usuario?> {
        return repository.getUsuarioPorUsuario(nombreUsuario)
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
