package com.practica.finazapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.DAOS.AppDatabase
import com.practica.finazapp.Entidades.Ingreso
import com.practica.finazapp.Repositorios.IngresoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IngresoViewModel(application: Application) :AndroidViewModel(application){

    private val allIngresos: LiveData<List<Ingreso>>
    private val repository: IngresoRepository
    init {
        val ingresoDao = AppDatabase.getDatabase(application).ingresoDao()
        repository = IngresoRepository(ingresoDao)
        allIngresos = repository.getAllIngresos()
    }

    fun insertIngreso(ingreso: Ingreso) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertIngreso(ingreso)
        }
    }
    fun getIngMesDeEsteMes(usuarioId: Long): LiveData<List<Ingreso>> {
        return repository.getIngMesDeEsteMes(usuarioId)
    }
    fun getIngCasDeEsteMes(usuarioId: Long): LiveData<List<Ingreso>> {
        return repository.getIngCasDeEsteMes(usuarioId)
    }

    fun getIngTotalDeEsteMes(usuarioId: Long): LiveData<Double>{
        return repository.getIngTotalDeEsteMes(usuarioId)
    }

    fun getAllIngresos(): LiveData<List<Ingreso>>{
        return repository.getAllIngresos()
    }
    fun verificacion(usuarioId: Long): LiveData<List<Ingreso>>{
        return repository.verificacion(usuarioId)
    }

    fun getIngresosMensuales(usuarioId: Long, anio: String, mes:String): LiveData<List<Ingreso>>{
        return repository.getIngresosMensuales(usuarioId, anio, mes)
    }

    fun truncarIngresos(){
        repository.truncarIngresos()
    }

    fun modificarIngreso(fecha: String, valor:Double, id: Long){
        repository.modificarIngreso(fecha, valor, id)
    }

    fun eliminarIngreso(id: Long){
        repository.eliminarIngreso(id)
    }

    fun desactivarIngPasado(descripcion:String){
        repository.desactivarIngPasado(descripcion)
    }
}