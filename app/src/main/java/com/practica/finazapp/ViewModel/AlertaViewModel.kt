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
    private var repository: AlertaRepository

    init {
        val alertaDao = AppDatabase.getDatabase(application).alertaDao()
        repository = AlertaRepository(alertaDao)
        allAlertas = repository.getAllAlertas()
    }

    constructor( application: Application, repository: AlertaRepository) : this(application){
        this.repository = repository
    }

    fun insertAlerta(alerta: Alerta) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlerta(alerta)
        }
    }

    fun getAlertasDeEsteMes(usuarioId: Long): LiveData<List<Alerta>> {
        return repository.getAlertasDeEsteMes(usuarioId)
    }
    fun eliminarAlerta(id: Long) {

        repository.eliminarAlerta(id)
    }
    fun modificarAlerta( fecha: String, valor: Double, id: Long) {

        repository.modificarAlerta(fecha, valor, id)
    }


}

