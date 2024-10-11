package com.practica.finazapp.DAOS
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.practica.finazapp.Entidades.Alerta
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.Entidades.Ingreso
import com.practica.finazapp.Entidades.Session
import com.practica.finazapp.Entidades.Usuario

@Database(entities = [Usuario::class, Ingreso::class, Gasto::class, Alerta::class,Session::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun gastoDao(): GastoDao
    abstract fun alertaDao(): AlertaDao
    abstract fun sessionDao(): SessionDao


    companion object {

        @Volatile
        private var INSTANCE : AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}
