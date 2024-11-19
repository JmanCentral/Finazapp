package com.practica.finazapp.DAOS

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.Entidades.Ingreso
import com.practica.finazapp.Entidades.Session
import com.practica.finazapp.Entidades.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM Usuario")
    fun getAll(): LiveData<List<Usuario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM Usuario WHERE id = :usuarioId")
    fun getUsuarioPorId(usuarioId: Long): LiveData<Usuario>

    @Query("SELECT * FROM usuario WHERE usuario = :nombreUsuario")
    fun getUsuarioPorUsuario(nombreUsuario: String): LiveData<Usuario?>

    @Query("SELECT id FROM Usuario ORDER BY id DESC LIMIT 1")
    fun getUltimoUsuarioId(): LiveData<Long>

    @Query("UPDATE Usuario SET usuario = :usuario, nombres = :nombres, apellidos = :apellidos WHERE id = :usuarioId")
    fun actualizarUsuario(usuarioId: Long, usuario: String, nombres: String, apellidos: String)

    @Query("DELETE FROM Usuario WHERE id = :usuarioId")
    fun eliminarUsuario(usuarioId: Long)

    @Query("UPDATE Usuario SET contrasena = :contrasena WHERE ID = :usuarioId")
    fun cambiarContrasena(contrasena: String, usuarioId: Long)
}

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(session: Session)

    @Query("SELECT * FROM session LIMIT 1")
    suspend fun getSession(): Session?

    @Query("DELETE FROM session")
    suspend fun deleteSession()
}

@Dao
interface IngresoDao {
    @Query("SELECT * FROM Ingreso")
    fun getAll(): LiveData<List<Ingreso>>

    @Query("SELECT * FROM Ingreso WHERE idUsuario = :usuarioId AND tipo = 'casual' AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now')")
    fun verificacion(usuarioId: Long): LiveData<List<Ingreso>>

    @Query("SELECT * FROM Ingreso WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now') AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now') AND tipo = 'mensual'")
    fun getIngMesDeEsteMes(usuarioId: Long): LiveData<List<Ingreso>>

    @Insert
    fun insert(ingreso: Ingreso)

    @Query("SELECT * FROM Ingreso WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now') AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now') AND tipo = 'casual'")
    fun getIngCasDeEsteMes(usuarioId: Long): LiveData<List<Ingreso>>

    @Query("SELECT SUM(valor) FROM Ingreso WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now')AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now')")
    fun getIngTotalDeEsteMes(usuarioId: Long): LiveData<Double>

    @Query("SELECT * FROM Ingreso WHERE idUsuario = :usuarioId AND tipo = 'mensual' AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = :anio AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = :mes")
    fun getIngresosMensuales(usuarioId: Long, anio: String, mes: String): LiveData<List<Ingreso>>


    @Query("DELETE FROM Ingreso")
    fun truncarIngresos()

    @Query("UPDATE Ingreso SET fecha = :fecha, valor = :valor WHERE id = :id ")
    fun modificarIngreso(fecha:String, valor:Double, id:Long)

    @Query("DELETE FROM Ingreso WHERE id = :id")
    fun eliminarIngreso(id: Long)

    @Query("UPDATE Ingreso set tipo = 'mensual Inactivo' WHERE descripcion = :descripcion")
    fun desactivarIngPasado(descripcion: String)

    @Query("SELECT SUM(valor) FROM Ingreso WHERE idUsuario = :usuarioId AND tipo = :tipo")
    fun getTotalIngresosPorTipo(usuarioId: Long, tipo: String): LiveData<Double>

    @Query("SELECT SUM(valor) FROM Ingreso WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now', '-1 month') AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now', '-1 month')")
    fun getIngTotalMesAnterior(usuarioId: Long): LiveData<Double>

    @Query("SELECT SUM(valor) * 1.05 FROM Ingreso WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now') AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now') AND tipo = 'mensual'")
    fun proyectarIngresosMensuales(usuarioId: Long): LiveData<Double>


}

@Dao
interface GastoDao {


    @Query("SELECT * FROM Gasto")
    fun getAll(): LiveData<List<Gasto>>

    @Insert
    fun insert(gasto: Gasto)

    @Query("SELECT (SELECT SUM(valor) FROM Ingreso WHERE idUsuario = :usuarioId) - IFNULL((SELECT SUM(valor) FROM Gasto WHERE idUsuario = :usuarioId), 0) FROM Ingreso WHERE idUsuario = :usuarioId")
    fun getDisponible(usuarioId: Long): LiveData<Double>


    @Query("SELECT (SELECT SUM(valor) FROM Ingreso WHERE idUsuario = :usuarioId)-(SELECT SUM(valor) FROM Gasto WHERE idUsuario = :usuarioId)")
    suspend fun getDisponible1(usuarioId: Long): Double

    @Query("SELECT * FROM Gasto WHERE idUsuario = :usuarioId AND categoria = :categoria AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now')AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now')")
    fun getGastosMesCategoria(usuarioId: Long, categoria: String):LiveData<List<Gasto>>

    @Query("SELECT SUM(valor) FROM Gasto WHERE idUsuario = :usuarioId AND categoria = :categoria AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now')AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now')")
    fun getValorGastosMesCategoria(usuarioId: Long, categoria: String):LiveData<Double>

    @Query("SELECT SUM(valor) FROM Gasto WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now')AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now')")
    fun getValorGastosMes(usuarioId: Long): LiveData<Double>

    @Query("DELETE FROM Gasto WHERE id = :id")
    fun deleteGasto(id: Long)

    @Query("UPDATE gasto SET categoria = :categoria, valor = :valor, descripcion = :descripcion, fecha = :fecha WHERE id == :id")
    fun modificarGasto(id: Long, categoria: String, valor: Double, descripcion: String, fecha: String)

    @Query("SELECT * FROM Gasto WHERE idUsuario = :idUsuario AND DATE(fecha) BETWEEN DATE(:fechaInf) AND DATE(:fechaSup)")
    fun getGastosPorFechas(idUsuario: Long, fechaInf: String, fechaSup: String): LiveData<List<Gasto>>

    @Query("SELECT * FROM gasto WHERE idUsuario = :usuarioId ORDER BY valor DESC")
    fun getGastosOrdenados(usuarioId: Long): LiveData<List<Gasto>>

    @Query("SELECT * FROM gasto WHERE idUsuario = :usuarioId ORDER BY valor ASC")
    fun getGastosOrdenadosAsc(usuarioId: Long): LiveData<List<Gasto>>

    @Query("SELECT * FROM gasto WHERE idUsuario = :usuarioId ORDER BY valor DESC")
    fun getGastosOrdenadosDesc(usuarioId: Long): LiveData<List<Gasto>>

    @Query("SELECT * FROM Gasto WHERE idUsuario = :usuarioId ORDER BY valor DESC LIMIT 1")
    fun getGastoMasAlto(usuarioId: Long): LiveData<Gasto>

    @Query("SELECT * FROM Gasto WHERE idUsuario = :usuarioId ORDER BY valor ASC LIMIT 1")
    fun getGastoMasBajo(usuarioId: Long): LiveData<Gasto>

    @Query("SELECT AVG(valor) FROM Gasto WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now') AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now')")
    fun getPromedioGastosMes(usuarioId: Long): LiveData<Double>

    @Query("SELECT descripcion FROM Gasto WHERE idUsuario = :usuarioId GROUP BY descripcion ORDER BY COUNT(descripcion) DESC LIMIT 1")
    fun getDescripcionRecurrente(usuarioId: Long): LiveData<String?>

    @Query("SELECT (SELECT SUM(valor) FROM Gasto WHERE idUsuario = :usuarioId) / (SELECT SUM(valor) FROM Ingreso WHERE idUsuario = :usuarioId) * 100 AS porcentajeGastos")
    fun getPorcentajeGastosSobreIngresos(usuarioId: Long): LiveData<Double>

    @Query("SELECT categoria, SUM(valor) as totalValor FROM Gasto WHERE idUsuario = :usuarioId GROUP BY categoria ORDER BY totalValor DESC")
    fun getCategoriasConMasGastos(usuarioId: Long): LiveData<List<CategoriaTotal>>

    @Query("SELECT categoria, AVG(valor) AS gastoPromedio FROM Gasto WHERE idUsuario = :usuarioId GROUP BY categoria")
    fun getGastoPromedioPorCategoria(usuarioId: Long): LiveData<List<GastoPromedio>>

    @Query("SELECT SUM(valor) / COUNT(DISTINCT fecha) AS gastoPromedioDiario FROM Gasto WHERE idUsuario = :usuarioId")
    fun getGastoPromedioDiarioTotal(usuarioId: Long): LiveData<Double>

}


@Dao
interface AlertaDao {

    // Obtener todas las alertas
    @Query("SELECT * FROM Alerta")
    fun getAll(): LiveData<List<Alerta>>

    // Insertar una nueva alerta
    @Insert
    fun insert(alerta: Alerta)

    // Obtener todas las alertas de un usuario específico
    @Query("SELECT * FROM Alerta WHERE idUsuario = :usuarioId")
    fun getAlertasPorUsuario(usuarioId: Long): LiveData<List<Alerta>>

    // Obtener alertas de este año basadas en la fecha
    @Query("SELECT * FROM Alerta WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now')")
    fun getAlertasDeEsteAno(usuarioId: Long): LiveData<List<Alerta>>

    // Obtener alertas de este mes
    @Query("SELECT * FROM Alerta WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now') AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now')")
    fun getAlertasDeEsteMes(usuarioId: Long): LiveData<List<Alerta>>

    // Eliminar una alerta específica por su ID
    @Query("DELETE FROM Alerta WHERE id = :id")
    fun eliminarAlerta(id: Long)

    // Modificar una alerta específica
    @Query("UPDATE Alerta SET fecha = :fecha, valor = :valor WHERE id = :id")
    fun modificarAlerta(fecha: String, valor: Double, id: Long)

    // Eliminar todas las alertas
    @Query("DELETE FROM Alerta")
    fun truncarAlertas()

    // Obtener la suma de valores de alertas en un mes específico para un usuario
    @Query("SELECT SUM(valor) FROM Alerta WHERE idUsuario = :usuarioId AND SUBSTR(fecha, 1, INSTR(fecha, '-') - 1) = strftime('%Y', 'now') AND SUBSTR(fecha, INSTR(fecha, '-') + 1, 2) = strftime('%m', 'now')")
    fun getValorTotalAlertasDeEsteMes(usuarioId: Long): LiveData<Double>




}



