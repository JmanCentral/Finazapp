package com.practica.finazapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    private val _ingresoLiveData = MutableLiveData<Boolean>()
    val ingresoLiveData: LiveData<Boolean> get() = _ingresoLiveData


    val idUsuario = MutableLiveData<Long>()

    fun setUsuarioId(data: Long){
        idUsuario.value = data
    }

    fun getUsuarioId(): Any {
        return idUsuario
    }

    fun notifyIngresoAdded() {
        _ingresoLiveData.value = true
    }
}