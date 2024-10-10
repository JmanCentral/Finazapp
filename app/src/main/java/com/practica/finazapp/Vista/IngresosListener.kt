package com.practica.finazapp.Vista

import com.practica.finazapp.Entidades.Ingreso

interface IngresosListener {
    fun onIngresosCargados(totalIngresos: Double, ingresosMensuales: List<Ingreso>, ingresosCasuales: List<Ingreso>)
}
