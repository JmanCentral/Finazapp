package com.practica.finazapp.Repositorios

import androidx.lifecycle.LiveData
import com.practica.finazapp.DAOS.IngresoDao
import com.practica.finazapp.Entidades.Ingreso

class IngresoRepository(private val ingresoDao: IngresoDao) {
    fun getAllIngresos(): LiveData<List<Ingreso>> = ingresoDao.getAll()

    fun insertIngreso(ingreso: Ingreso){
        ingresoDao.insert(ingreso)
    }

    fun getIngMesDeEsteMes(usuarioId: Long): LiveData<List<Ingreso>>{
        return ingresoDao.getIngMesDeEsteMes(usuarioId)
    }
    fun getIngCasDeEsteMes(usuarioId: Long): LiveData<List<Ingreso>>{
        return ingresoDao.getIngCasDeEsteMes(usuarioId)
    }

    fun getIngTotalDeEsteMes(usuarioId: Long): LiveData<Double>{
        return ingresoDao.getIngTotalDeEsteMes(usuarioId)
    }

    fun verificacion(usuarioId: Long): LiveData<List<Ingreso>>{
        return ingresoDao.verificacion(usuarioId)
    }

    fun getIngresosMensuales(usuarioId: Long, anio: String, mes:String): LiveData<List<Ingreso>>{
        return ingresoDao.getIngresosMensuales(usuarioId,anio,mes)
    }

    fun truncarIngresos(){
        ingresoDao.truncarIngresos()
    }

    fun modificarIngreso(fecha: String, valor: Double, id: Long){
        ingresoDao.modificarIngreso(fecha, valor, id)
    }

    fun eliminarIngreso(id:Long){
        ingresoDao.eliminarIngreso(id)
    }

    fun desactivarIngPasado(descripcion: String){
        ingresoDao.desactivarIngPasado(descripcion)
    }

    fun getTotalIngresosPorTipo(usuarioId: Long, tipo: String): LiveData<Double> {
        return ingresoDao.getTotalIngresosPorTipo(usuarioId, tipo)
    }

    fun getIngTotalMesAnterior(usuarioId: Long): LiveData<Double> {
        return ingresoDao.getIngTotalMesAnterior(usuarioId)
    }


}