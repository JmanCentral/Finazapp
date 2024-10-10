package com.practica.finazapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.Repositorios.AlertaRepository
import kotlinx.coroutines.launch

class AlertaViewModel(private val alertaRepository: AlertaRepository) : ViewModel() {

    // LiveData para observar todas las alertas
    val todasLasAlertas: LiveData<List<Alerta>> = alertaRepository.todasLasAlertas

    // Función para obtener las alertas por usuario
    fun obtenerAlertasPorUsuario(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaRepository.obtenerAlertasPorUsuario(usuarioId)
    }

    // Función para insertar una nueva alerta
    fun insertarAlerta(alerta: Alerta) = viewModelScope.launch {
        alertaRepository.insertarAlerta(alerta)
    }

    // Función para eliminar una alerta por su ID
    fun eliminarAlerta(id: Long) = viewModelScope.launch {
        alertaRepository.eliminarAlerta(id)
    }

    // Función para modificar una alerta
    fun modificarAlerta(nombre: String, descripcion: String, fecha: String, valor: Double, id: Long) = viewModelScope.launch {
        alertaRepository.modificarAlerta(nombre, descripcion, fecha, valor, id)
    }

    // Función para truncar (eliminar todas las alertas)
    fun truncarAlertas() = viewModelScope.launch {
        alertaRepository.truncarAlertas()
    }

    // Obtener las alertas de este año para un usuario específico
    fun obtenerAlertasDeEsteAno(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaRepository.obtenerAlertasDeEsteAno(usuarioId)
    }

    // Obtener las alertas de este mes para un usuario específico
    fun obtenerAlertasDeEsteMes(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaRepository.obtenerAlertasDeEsteMes(usuarioId)
    }

    // Función para obtener el valor total de las alertas de este mes
    fun obtenerValorTotalDeEsteMes(usuarioId: Long): LiveData<Double> {
        return alertaRepository.obtenerValorTotalDeEsteMes(usuarioId)
    }
}
