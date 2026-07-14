package com.example.app_financeiro.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_financeiro.R
import com.example.app_financeiro.model.Lancamento

class LancamentoRecyclerAdapter(
    private val lista: ArrayList<Lancamento>,
    private val onItemClick: (Lancamento) -> Unit,
    private val onItemLongClick: (Lancamento) -> Unit
) : RecyclerView.Adapter<LancamentoRecyclerAdapter.LancamentoViewHolder>() {

    class LancamentoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val textTipo: TextView =
            itemView.findViewById(R.id.textTipo)

        val textDescricao: TextView =
            itemView.findViewById(R.id.textDescricao)

        val textData: TextView =
            itemView.findViewById(R.id.textData)

        val textValor: TextView =
            itemView.findViewById(R.id.textValor)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LancamentoViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_lancamento_card,
                parent,
                false
            )

        return LancamentoViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LancamentoViewHolder,
        position: Int
    ) {
        val lancamento = lista[position]

        holder.textDescricao.text = lancamento.descricao
        holder.textData.text = lancamento.data

        if (lancamento.tipo == "Receita") {
            holder.textTipo.text = "💰 Receita"
            holder.textValor.text =
                "+ R$ %.2f".format(lancamento.valor)

            holder.textTipo.setTextColor(
                Color.rgb(0, 150, 0)
            )

            holder.textValor.setTextColor(
                Color.rgb(0, 150, 0)
            )
        } else {
            holder.textTipo.text = "💸 Despesa"
            holder.textValor.text =
                "- R$ %.2f".format(lancamento.valor)

            holder.textTipo.setTextColor(
                Color.rgb(200, 0, 0)
            )

            holder.textValor.setTextColor(
                Color.rgb(200, 0, 0)
            )
        }

        // Clique normal: editar.
        holder.itemView.setOnClickListener {
            onItemClick(lancamento)
        }

        // Clique longo: solicitar exclusão.
        holder.itemView.setOnLongClickListener {
            onItemLongClick(lancamento)
            true
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}