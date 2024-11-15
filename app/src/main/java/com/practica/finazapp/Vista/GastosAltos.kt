package com.practica.finazapp.Vista

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.ViewModel.GastosViewModel
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.databinding.FragmentGastosAltosBinding


class GastosAltos : Fragment() {

    private var usuarioId: Long = -1
    private lateinit var gastosViewModel: GastosViewModel
    private lateinit var adapter: GastoListaAdapter
    private lateinit var sharedViewModel: SharedViewModel
    private var _binding: FragmentGastosAltosBinding? = null
    private lateinit var recyclerView: RecyclerView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGastosAltosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.ListGastos
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inicializar el adapter vacío
        adapter = GastoListaAdapter(emptyList())
        recyclerView.adapter = adapter

        // Inicializar el SharedViewModel para obtener el usuarioId
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { id ->
            usuarioId = id
        }

        // Inicializar el GastosViewModel
        gastosViewModel = ViewModelProvider(this).get(GastosViewModel::class.java)

        binding.btnOrdenarAscendente.setOnClickListener {
            cargarGastosAscendente()
            ocultarImagen()
        }

        binding.btnOrdenarDescendente.setOnClickListener {
            cargarGastosDescendente()
            ocultarImagen()
        }
    }

    private fun cargarGastosAscendente() {

        gastosViewModel.getGastosOrdenadosAsc(usuarioId).observe(viewLifecycleOwner) { gastos ->
            if (gastos != null) {
                adapter = GastoListaAdapter(gastos)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun cargarGastosDescendente() {
        // Observar los gastos del ViewModel y actualizar el adaptador
        gastosViewModel.getGastosOrdenadosDesc(usuarioId).observe(viewLifecycleOwner) { gastos ->
            if (gastos != null) {
                adapter = GastoListaAdapter(gastos)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }
    private fun ocultarImagen() {
        binding.imagenInicial.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

