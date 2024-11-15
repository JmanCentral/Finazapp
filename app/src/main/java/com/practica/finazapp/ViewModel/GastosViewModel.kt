package com.practica.finazapp.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.practica.finazapp.DAOS.AppDatabase
import com.practica.finazapp.DAOS.CategoriaTotal
import com.practica.finazapp.DAOS.GastoPromedio
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.Repositorios.GastoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GastosViewModel(application: Application) :AndroidViewModel(application) {

    private val allGastos: LiveData<List<Gasto>>
    private var repository: GastoRepository

    init {
        val gastoDao = AppDatabase.getDatabase(application).gastoDao()
        repository = GastoRepository(gastoDao)
        allGastos = repository.getAllGastos()
    }

    constructor( application: Application, repository: GastoRepository) : this(application){
        this.repository = repository

    }


    fun insertGasto(gasto: Gasto) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertGasto(gasto)
        }
    }

    fun getDisponible(usuarioId: Long): LiveData<Double>{
        return repository.getDisponible(usuarioId)
    }

    suspend fun getDisponibleDirecto(usuarioId: Long): Double {
        return repository.getDisponible1(usuarioId)
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


    fun getGastosOrdenadosAsc(usuarioId: Long): LiveData<List<Gasto>> {
        return repository.getGastosOrdenadosAsc(usuarioId)
    }

    fun getGastosOrdenadosDesc(usuarioId: Long): LiveData<List<Gasto>> {
        return repository.getGastosOrdenadosDesc(usuarioId)

    }
    fun getGastoMasAlto(usuarioId: Long): LiveData<Gasto> {
        return repository.getGastoMasAlto(usuarioId)
    }

    fun getGastoMasBajo(usuarioId: Long): LiveData<Gasto> {
        return repository.getGastoMasBajp(usuarioId)

    }

    fun getPromedioGastosMes(usuarioId: Long): LiveData<Double> {
        return repository.getPromedioGastosMes(usuarioId)
    }

    fun getGastosRecurrentes(usuarioId: Long): LiveData<String?> {
        return repository.getDescripcionRecurrente(usuarioId)
    }


    fun getPorcentajesGastosSobreIngresos(usuarioId: Long): LiveData<Double> {
        return repository.getPorcentajesGastosSobreIngresos(usuarioId)
    }

    fun getCategoriasConMasGastos(usuarioId: Long): LiveData<List<CategoriaTotal>> {
        return repository.getCategoriasConMasGastos(usuarioId)
    }

    fun gastopromediodiario(usuarioId: Long): LiveData<Double> {
        return repository.getPromedioDiario(usuarioId)

    }


}