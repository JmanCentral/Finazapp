package com.practica.finazapp.Repositorios

import androidx.lifecycle.LiveData
import com.practica.finazapp.DAOS.AlertaDao
import com.practica.finazapp.Entidades.Alerta

class AlertaRepository(private val alertaDao: AlertaDao) {

    // Obtener todas las alertas (LiveData para observar los cambios en la UI)
    val todasLasAlertas: LiveData<List<Alerta>> = alertaDao.getAll()

    // Obtener todas las alertas de un usuario específico
    fun obtenerAlertasPorUsuario(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaDao.getAlertasPorUsuario(usuarioId)
    }

    // Insertar una nueva alerta (en un hilo en segundo plano)
    suspend fun insertarAlerta(alerta: Alerta) {
        alertaDao.insert(alerta)
    }

    // Obtener las alertas de este año para un usuario específico
    fun obtenerAlertasDeEsteAno(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaDao.getAlertasDeEsteAno(usuarioId)
    }

    // Obtener las alertas del mes actual para un usuario específico
    fun obtenerAlertasDeEsteMes(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaDao.getAlertasDeEsteMes(usuarioId)
    }

    // Eliminar una alerta por su ID (en un hilo en segundo plano)
    suspend fun eliminarAlerta(id: Long) {
        alertaDao.eliminarAlerta(id)
    }

    // Modificar una alerta (en un hilo en segundo plano)
    suspend fun modificarAlerta(nombre: String, descripcion: String, fecha: String, valor: Double, id: Long) {
        alertaDao.modificarAlerta(nombre, descripcion, fecha, valor, id)
    }

    // Truncar (eliminar todas las alertas)
    suspend fun truncarAlertas() {
        alertaDao.truncarAlertas()
    }

    // Obtener el valor total de las alertas de este mes
    fun obtenerValorTotalDeEsteMes(usuarioId: Long): LiveData<Double> {
        return alertaDao.getValorTotalAlertasDeEsteMes(usuarioId)
    }
}