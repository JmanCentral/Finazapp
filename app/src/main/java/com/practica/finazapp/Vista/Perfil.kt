package com.practica.finazapp.Vista

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.practica.finazapp.R
import com.practica.finazapp.ViewModel.SharedViewModel
import com.practica.finazapp.ViewModel.UsuarioViewModel
import com.practica.finazapp.databinding.FragmentPerfilBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Perfil : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var usuarioViewModel: UsuarioViewModel

    // Declaración de la variable de enlace
    private var _binding: FragmentPerfilBinding? = null

    // Esta propiedad es válida solo entre onCreateView y onDestroyView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el diseño del fragmento utilizando el enlace de datos generado
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Devolver la vista raíz del diseño inflado
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usuarioViewModel = ViewModelProvider(this).get(UsuarioViewModel::class.java)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        sharedViewModel.idUsuario.observe(viewLifecycleOwner) { usuarioId ->
            if (usuarioId != null) {
                usuarioViewModel.getUsuarioPorId(usuarioId).observe(viewLifecycleOwner) { usuario ->
                    usuario?.let {
                        binding.txtinputUsuario.setText(it.usuario)
                        binding.txtinputNombres.setText(it.nombres)
                        binding.txtinputApellidos.setText(it.apellidos)
                        binding.txtinputDoc.setText(it.documento)
                        binding.txtinputMail.setText(it.correo)
                        binding.txtinputTel.setText(it.telefono)
                    }
                }
            }
        }


        val usuarioInputField = view.findViewById<TextInputEditText>(R.id.txtinputUsuario)
        val nombreInputField = view.findViewById<TextInputEditText>(R.id.txtinputNombres)
        val apellidoInputField = view.findViewById<TextInputEditText>(R.id.txtinputApellidos)
        val docInputField = view.findViewById<TextInputEditText>(R.id.txtinputDoc)
        val mailInputField = view.findViewById<TextInputEditText>(R.id.txtinputMail)
        val telInputField = view.findViewById<TextInputEditText>(R.id.txtinputTel)

        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)
        val btnCambiarCon = view.findViewById<Button>(R.id.btnCambiarCon)
        val btnBorrarPerf = view.findViewById<Button>(R.id.btnBorrarPerf)

        btnGuardar.setOnClickListener {
            // Verifica si alguno de los campos está vacío
            val camposVacios = mutableListOf<String>()
            if (usuarioInputField.text.isNullOrBlank()) {
                camposVacios.add("Usuario")
            }
            if (docInputField.text.isNullOrBlank()) {
                camposVacios.add("Documento")
            }
            if (apellidoInputField.text.isNullOrBlank()) {
                camposVacios.add("Apellidos")
            }
            if (nombreInputField.text.isNullOrBlank()) {
                camposVacios.add("Nombres")
            }

            // Si hay campos vacíos, muestra un mensaje
            if (camposVacios.isNotEmpty()) {
                val camposFaltantes = camposVacios.joinToString(", ")
                Toast.makeText(
                    requireContext(),
                    "Se deben llenar los campos: $camposFaltantes",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val usuario = usuarioInputField.text.toString()
                val nombres = nombreInputField.text.toString()
                val apellidos = apellidoInputField.text.toString()
                val documento = docInputField.text.toString()
                val email = mailInputField.text.toString()
                val numeroTel = telInputField.text.toString()

                sharedViewModel.idUsuario.value?.let { usuarioId ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            // Ejecutar la operación de base de datos en un hilo separado
                            usuarioViewModel.actualizarUsuario(
                                usuarioId,
                                usuario,
                                nombres,
                                apellidos,
                                documento,
                                email,
                                numeroTel
                            )
                        }
                    }
                }

                Toast.makeText(requireContext(), "Datos guardados exitosamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        btnBorrarPerf.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Confirmación")
            builder.setMessage("¿Estás seguro de que deseas borrar tu perfil?")
            builder.setPositiveButton("Aceptar") { dialog, _ ->
                sharedViewModel.idUsuario.value?.let { usuarioId ->
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            usuarioViewModel.eliminarUsuario(usuarioId)
                        }
                    }
                }
                val intent = Intent(requireContext(), Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
                dialog.dismiss() // Cierra el diálogo después de aceptar
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss() // Cierra el diálogo si se hace clic en Cancelar
            }
            val dialog = builder.create()
            dialog.show()
        }

        btnCambiarCon.setOnClickListener {
            sharedViewModel.idUsuario.value?.let { usuarioId ->

                usuarioViewModel.getUsuarioPorId(usuarioId).observe(viewLifecycleOwner) { usuario ->
                    val dialogView = layoutInflater.inflate(R.layout.dialog_cambiar_con, null)
                    val conActual = dialogView.findViewById<TextInputEditText>(R.id.txtinputConActual)
                    val nuevaCon = dialogView.findViewById<TextInputEditText>(R.id.txtinputNuevaCon)
                    val confCon = dialogView.findViewById<TextInputEditText>(R.id.txtinputConfCon)
                    val tvError = dialogView.findViewById<TextView>(R.id.tvError)
                    var hayError = false
                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(dialogView)
                        .setPositiveButton("Confirmar") { dialog, _ ->
                            val contrasena = conActual.text.toString()
                            val nuevaContrasena = nuevaCon.text.toString()
                            val confirmarContrasena = confCon.text.toString()
                            val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,}\$")
                            if (contrasena != usuario.contrasena) {
                                hayError = true
                                val toast = Toast.makeText(
                                    requireContext(),
                                    "La contraseña no es correcta",
                                    Toast.LENGTH_LONG
                                ) // in Activity
                                toast.show()
                            }
                            if (!regex.matches(nuevaContrasena)) {
                                tvError.text = getString(R.string.requerimientos_cont)
                                tvError.visibility = View.VISIBLE
                                val toast = Toast.makeText(
                                    requireContext(),
                                    "Requerimientos de contraseña no cumplidos",
                                    Toast.LENGTH_LONG
                                ) // in Activity
                                toast.show()
                                Log.d("MyApp", "Error: Requerimientos de contraseña no cumplidos")
                                        hayError = true
                            } else if (nuevaContrasena != confirmarContrasena) {
                                tvError.text =
                                    getString(R.string.las_contrase_as_no_coinciden)
                                tvError.visibility = View.VISIBLE
                                val toast = Toast.makeText(
                                    requireContext(),
                                    getString(R.string.las_contrase_as_no_coinciden),
                                    Toast.LENGTH_SHORT
                                ) // in Activity
                                toast.show()
                                Log.d("MyApp", "Error: Las contraseñas no coinciden")
                                hayError = true
                            } else if (!hayError) {
                                sharedViewModel.idUsuario.value?.let { usuarioId ->
                                    lifecycleScope.launch {
                                        withContext(Dispatchers.IO) {
                                            usuarioViewModel.cambiarContrasena(
                                                nuevaContrasena,
                                                usuarioId
                                            )
                                        }
                                    }
                                }
                                val toast = Toast.makeText(
                                    requireContext(),
                                    "la contraseña ha sido cambiada exitosamente",
                                    Toast.LENGTH_SHORT
                                ) // in Activity
                                toast.show()
                                Log.d(
                                    "MyApp",
                                    "La contraseña ha sido cambiada exitosamente"
                                )
                            }
                        }
                            .setNegativeButton("Cancelar") { dialog, _ ->
                                dialog.dismiss()
                            }
                                .create()
                            dialog.show()
                            dialog.setOnDismissListener {
                                if (hayError) {
                                    hayError = false
                                }
                            }
                        }
            }
        }
    }
        override fun onDestroyView() {
                super.onDestroyView()
                // Limpiar la referencia al enlace de datos para evitar fugas de memoria
                _binding = null
        }
}