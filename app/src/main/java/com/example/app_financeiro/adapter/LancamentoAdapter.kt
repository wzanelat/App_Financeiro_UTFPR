package com.example.app_financeiro.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.app_financeiro.R
import com.example.app_financeiro.model.Lancamento

class LancamentoAdapter(
    context: Context,
    private val lancamentos: ArrayList<Lancamento>
) : ArrayAdapter<Lancamento>(context, 0, lancamentos) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_lancamento, parent, false)

        val lancamento = lancamentos[position]

        val textDescricao = view.findViewById<TextView>(R.id.textDescricao)
        val textData = view.findViewById<TextView>(R.id.textData)
        val textValor = view.findViewById<TextView>(R.id.textValor)

        textDescricao.text = lancamento.descricao
        textData.text = "${lancamento.tipo} • ${lancamento.data}"

        if (lancamento.tipo == "Receita") {
            textValor.text = "+ R$ %.2f".format(lancamento.valor)
            textValor.setTextColor(Color.rgb(0, 150, 0))
        } else {
            textValor.text = "- R$ %.2f".format(lancamento.valor)
            textValor.setTextColor(Color.rgb(200, 0, 0))
        }

        return view
    }
}