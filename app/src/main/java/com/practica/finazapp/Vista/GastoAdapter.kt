package com.practica.finazapp.Vista

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.R
import java.text.NumberFormat


class GastoAdapter(private val gastos: List<Gasto>) :
    RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    private var listener: OnItemClickListener? = null
    private var listener1: OnItemClickListener1? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val currentGasto = gastos[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        holder.descripcionTextView.text = currentGasto.descripcion
        holder.valorTextView.text = "${numberFormat.format(currentGasto.valor)}$"



        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentGasto)
        }

    }


    private fun mostrarDialogo(context: Context, descripcion: String) {
        AlertDialog.Builder(context)
            .setTitle("Aviso de Gasto Alto")
            .setMessage("El gasto en \"$descripcion\" es mayor a 500$. ¿Deseas revisarlo?")
            .setPositiveButton("Revisar") { dialog, _ ->
                // Acción al pulsar "Revisar"
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun setOnItemClickListener1(listener1: OnItemClickListener1) {
        this.listener1 = listener1
    }

    override fun getItemCount() = gastos.size

    inner class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descripcionTextView: TextView = itemView.findViewById(R.id.txtDescripcion)
        val valorTextView: TextView = itemView.findViewById(R.id.txtValor)
    }
}