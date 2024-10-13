package com.practica.finazapp.Vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practica.finazapp.ViewModel.AlertaViewModel
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.IngresoViewModel
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentProyeccionesBinding


class FragmentProyecciones : Fragment() {

    private var usuarioId: Long = -1
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var ingresoViewModel: IngresoViewModel
    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var alertaViewModel: AlertaViewModel

    private var _binding: FragmentProyeccionesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        _binding = FragmentProyeccionesBinding.inflate(inflater, container, false)
        return binding.root
    }


}