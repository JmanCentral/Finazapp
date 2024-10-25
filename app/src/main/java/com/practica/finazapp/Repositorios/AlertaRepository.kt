package com.practica.finazapp.Repositorios

import androidx.lifecycle.LiveData
import com.practica.finazapp.DAOS.AlertaDao
import com.practica.finazapp.Entidades.Alerta

class AlertaRepository(private val alertaDao: AlertaDao) {

    fun getAllAlertas(): LiveData<List<Alerta>> {
        return alertaDao.getAll()
    }
    fun getAlertasPorUsuario(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaDao.getAlertasPorUsuario(usuarioId)
    }
    fun getAlertasDeEsteAno(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaDao.getAlertasDeEsteAno(usuarioId)
    }

    fun getAlertasDeEsteMes(usuarioId: Long): LiveData<List<Alerta>> {
        return alertaDao.getAlertasDeEsteMes(usuarioId)
    }
    fun eliminarAlerta(id: Long) {
        alertaDao.eliminarAlerta(id)
    }

    fun modificarAlerta(fecha: String, valor: Double, id: Long) {
        alertaDao.modificarAlerta(fecha, valor, id)
    }
    fun truncarAlertas(){
        alertaDao.truncarAlertas()
    }
    fun getValorTotalAlertasDeEsteMes(usuarioId: Long): LiveData<Double> {
        return alertaDao.getValorTotalAlertasDeEsteMes(usuarioId)
    }

    fun insertAlerta(alerta: Alerta) {
        alertaDao.insert(alerta)
    }

}
