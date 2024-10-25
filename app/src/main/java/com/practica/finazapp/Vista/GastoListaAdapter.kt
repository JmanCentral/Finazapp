package com.practica.finazapp.Vista

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.practica.finazapp.Entidades.Gasto
import com.practica.finazapp.R
import java.text.NumberFormat


class GastoListaAdapter(private val gastos: List<Gasto>) :
    RecyclerView.Adapter<GastoListaAdapter.GastoListaViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoListaViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_lista, parent, false)
        return GastoListaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GastoListaViewHolder, position: Int) {
        val currentGasto = gastos[position]
        val numberFormat = NumberFormat.getInstance()
        numberFormat.maximumFractionDigits = 2

        holder.descripcionTextView.text = currentGasto.descripcion
        holder.categoriaTextView.text = currentGasto.categoria
        holder.fechaTextView.text = currentGasto.fecha
        holder.valorTextView.text = "${numberFormat.format(currentGasto.valor)}$"

        holder.itemView.setOnClickListener {
            listener?.onItemClick(currentGasto)
        }
    }

    override fun getItemCount() = gastos.size

    inner class GastoListaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descripcionTextView: TextView = itemView.findViewById(R.id.Viewdescripcion)
        val categoriaTextView: TextView = itemView.findViewById(R.id.Viewcategoria)
        val fechaTextView: TextView = itemView.findViewById(R.id.Viewfecha)
        val valorTextView: TextView = itemView.findViewById(R.id.Viewvalor)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
