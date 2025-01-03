package com.practica.finazapp.Repositorios

import androidx.lifecycle.LiveData
import com.practica.finazapp.DAOS.CategoriaTotal
import com.practica.finazapp.DAOS.GastoDao
import com.practica.finazapp.DAOS.GastoPromedio
import com.practica.finazapp.Entidades.Gasto

class GastoRepository(private val gastoDao: GastoDao) {


    fun getAllGastos(): LiveData<List<Gasto>> = gastoDao.getAll()

    fun insertGasto(gasto: Gasto){
        gastoDao.insert(gasto)
    }

    fun getDisponible(usuarioId: Long): LiveData<Double> = gastoDao.getDisponible(usuarioId)

    suspend fun getDisponible1(usuarioId: Long): Double {
        return gastoDao.getDisponible1(usuarioId) ?: 0.0
    }

    fun getValorGastosMes(usuarioId: Long): LiveData<Double> = gastoDao.getValorGastosMes(usuarioId)

    fun getValorGastosMesCategoria(usuarioId: Long, categoria:String): LiveData<Double> = gastoDao.getValorGastosMesCategoria(usuarioId, categoria)

    fun getGastosMesCategoria(usuarioId: Long, categoria: String):LiveData<List<Gasto>> = gastoDao.getGastosMesCategoria(usuarioId, categoria)

    fun deleteGasto(id: Long){
        gastoDao.deleteGasto(id)
    }
    fun modificarGasto(id: Long, categoria: String, valor: Double, descripcion: String, fecha: String){
        gastoDao.modificarGasto(id, categoria, valor, descripcion, fecha)
    }

    fun getGastosPorFechas(idUsuario: Long,fechaInf: String,fechaSup: String): LiveData<List<Gasto>>{
        return gastoDao.getGastosPorFechas(idUsuario,fechaInf,fechaSup)
    }

    fun getGastosOrdenados(usuarioId: Long): LiveData<List<Gasto>> {
        return gastoDao.getGastosOrdenados(usuarioId)
    }

    fun getGastosOrdenadosAsc(usuarioId: Long): LiveData<List<Gasto>> {
        return gastoDao.getGastosOrdenadosAsc(usuarioId)
    }
    fun getGastosOrdenadosDesc(usuarioId: Long): LiveData<List<Gasto>> {
        return gastoDao.getGastosOrdenadosDesc(usuarioId)
    }

    fun getGastoMasAlto(usuarioId: Long): LiveData<Gasto> {
        return gastoDao.getGastoMasAlto(usuarioId)
    }
    fun getGastoMasBajp(usuarioId: Long): LiveData<Gasto> {
        return gastoDao.getGastoMasBajo(usuarioId)
    }

    fun getPromedioGastosMes(usuarioId: Long): LiveData<Double> {
        return gastoDao.getPromedioGastosMes(usuarioId)
    }

    fun getDescripcionRecurrente(usuarioId: Long): LiveData<String?>{
        return gastoDao.getDescripcionRecurrente(usuarioId)
    }

    fun getPorcentajesGastosSobreIngresos(usuarioId: Long): LiveData<Double> {
        return gastoDao.getPorcentajeGastosSobreIngresos(usuarioId)
    }

    fun getCategoriasConMasGastos(usuarioId: Long): LiveData<List<CategoriaTotal>> {
        return gastoDao.getCategoriasConMasGastos(usuarioId)
    }

    fun getpromediodiario(usuarioId: Long): LiveData<List<GastoPromedio>> {
        return gastoDao.getGastoPromedioPorCategoria(usuarioId)
    }
    fun getPromedioDiario(usuarioId: Long): LiveData<Double> {
        return gastoDao.getGastoPromedioDiarioTotal(usuarioId)
    }

}
