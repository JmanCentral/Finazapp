package com.practica.finazapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.DAOS.AppDatabase
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.Repositorios.AlertaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertaViewModel(application: Application) : AndroidViewModel(application) {

    private val allAlertas: LiveData<List<Alerta>>
    private val repository: AlertaRepository

    init {
        val alertaDao = AppDatabase.getDatabase(application).alertaDao()
        repository = AlertaRepository(alertaDao)
        allAlertas = repository.getAllAlertas()
    }
    fun getAllAlertas(): LiveData<List<Alerta>> {
        return allAlertas
    }
    fun insertAlerta(alerta: Alerta) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlerta(alerta)
        }
    }
    fun getAlertasPorUsuario(usuarioId: Long): LiveData<List<Alerta>> {
        return repository.getAlertasPorUsuario(usuarioId)
    }
    fun getAlertasDeEsteAno(usuarioId: Long): LiveData<List<Alerta>> {
        return repository.getAlertasDeEsteAno(usuarioId)
    }
    fun getAlertasDeEsteMes(usuarioId: Long): LiveData<List<Alerta>> {
        return repository.getAlertasDeEsteMes(usuarioId)
    }
    fun eliminarAlerta(id: Long) {
        viewModelScope.launch {
            repository.eliminarAlerta(id)
        }
    }
    fun modificarAlerta(nombre: String, descripcion: String, fecha: String, valor: Double, id: Long) {
        viewModelScope.launch {
            repository.modificarAlerta(nombre, descripcion, fecha, valor, id)
        }
    }
    fun truncarAlertas() {
        viewModelScope.launch {
            repository.truncarAlertas()
        }
    }


}
