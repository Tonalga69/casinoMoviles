package com.example.casino

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistorialAdapter(private val historialList: List<String>) :
    RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    // ViewHolder para manejar cada elemento del RecyclerView
    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtRegistro: TextView = itemView.findViewById(R.id.textviewResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_list_item, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        holder.txtRegistro.text = historialList[position]
    }

    override fun getItemCount(): Int = historialList.size
}
