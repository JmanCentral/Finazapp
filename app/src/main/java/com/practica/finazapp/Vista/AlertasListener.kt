package com.practica.finazapp.Vista

import com.practica.finazapp.Entidades.Alerta


interface AlertasListener {

    fun onAlertasCargadas(alertas: List<Alerta> , ingresoTotal: Double)
}
