package com.example.casino

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class ResultadosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultados)

        val dineroGanado = intent.getFloatExtra("dineroGanado", 0.0f)
        val dineroPerdido = intent.getFloatExtra("dineroPerdido", 0.0f)
        val saldo = intent.getFloatExtra("saldo", 0.0f)
        val resultadosPartidos =
            intent.getStringArrayListExtra("resultadosPartidos") ?: arrayListOf()

        val textViewGanado = findViewById<TextView>(R.id.textViewGanado)
        val textViewPerdido = findViewById<TextView>(R.id.textViewPerdido)
        val textViewSaldo = findViewById<TextView>(R.id.saldo)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewResultados)

        textViewSaldo.text = getString(R.string.saldo, saldo.toString())
        textViewGanado.text = getString(R.string.dinero_ganado, dineroGanado.toString())
        textViewPerdido.text = getString(R.string.dinero_perdido, dineroPerdido.toString())

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ResultadosAdapter(resultadosPartidos)


    }
}

class ResultadosAdapter(private val resultados: List<String>) :
    RecyclerView.Adapter<ResultadosAdapter.ViewHolder>() {
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val textView = android.widget.TextView(parent.context).apply {
            textSize = 18f
            setPadding(8, 8, 8, 8)
        }
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = resultados[position]
    }

    override fun getItemCount(): Int = resultados.size
}