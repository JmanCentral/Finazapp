package com.practica.finazapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.DAOS.AppDatabase
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.Repositorios.GastoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GastosViewModel(application: Application) :AndroidViewModel(application) {

    private val allGastos: LiveData<List<Gasto>>
    private val repository: GastoRepository

    init {
        val gastoDao = AppDatabase.getDatabase(application).gastoDao()
        repository = GastoRepository(gastoDao)
        allGastos = repository.getAllGastos()
    }


    fun insertGasto(gasto: Gasto) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertGasto(gasto)
        }
    }

    fun getDisponible(usuarioId: Long): LiveData<Double>{
        return repository.getDisponible(usuarioId)
    }

    fun getValorGastosMes(usuarioId: Long): LiveData<Double>{
        return repository.getValorGastosMes(usuarioId)
    }

    fun getValorGastosMesCategoria(usuarioId: Long, categoria:String): LiveData<Double>{
        return repository.getValorGastosMesCategoria(usuarioId, categoria)
    }

    fun getGastosMesCategoria(usuarioId: Long, categoria: String):LiveData<List<Gasto>>{
        return  repository.getGastosMesCategoria(usuarioId, categoria)
    }

    fun deleteGasto(id: Long){
        repository.deleteGasto(id)
    }

    fun modificarGasto(id: Long, categoria: String, valor: Double, descripcion: String, fecha: String){
        repository.modificarGasto(id, categoria, valor, descripcion, fecha)
    }

    fun getGastosPorFechas(idUsuario: Long,fechaInf: String,fechaSup: String): LiveData<List<Gasto>>{
        return repository.getGastosPorFechas(idUsuario,fechaInf,fechaSup)
    }

    fun getGastosOrdenados(usuarioId: Long): LiveData<List<Gasto>> {
        return repository.getGastosOrdenados(usuarioId)
    }

    fun getGastosOrdenadosAsc(usuarioId: Long): LiveData<List<Gasto>> {
        return repository.getGastosOrdenadosAsc(usuarioId)
    }

    fun getGastosOrdenadosDesc(usuarioId: Long): LiveData<List<Gasto>> {
        return repository.getGastosOrdenadosDesc(usuarioId)

    }
}