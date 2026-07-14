package com.example.app_financeiro.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_financeiro.R
import com.example.app_financeiro.adapter.LancamentoRecyclerAdapter
import com.example.app_financeiro.database.DatabaseHelper

class ExtratoActivity : AppCompatActivity() {

    private lateinit var db: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var textReceitas: TextView
    private lateinit var textDespesas: TextView
    private lateinit var textSaldo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extrato)

        val toolbar = findViewById<Toolbar>(R.id.toolbarExtrato)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Extrato"

        db = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerLancamentos)
        textReceitas = findViewById(R.id.textReceitas)
        textDespesas = findViewById(R.id.textDespesas)
        textSaldo = findViewById(R.id.textSaldo)

        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        carregarDados()
    }

    private fun carregarDados() {
        val lista = db.listarLancamentos()

        var totalReceitas = 0.0
        var totalDespesas = 0.0

        for (lancamento in lista) {
            if (lancamento.tipo == "Receita") {
                totalReceitas += lancamento.valor
            } else {
                totalDespesas += lancamento.valor
            }
        }

        val saldo = totalReceitas - totalDespesas

        textReceitas.text = "Receitas: R$ %.2f".format(totalReceitas)
        textDespesas.text = "Despesas: R$ %.2f".format(totalDespesas)
        textSaldo.text = "Saldo: R$ %.2f".format(saldo)

        val adapter = LancamentoRecyclerAdapter(

            lista = lista,

            onItemClick = { lancamento ->

                val intent = Intent(this, CadastroActivity::class.java)

                intent.putExtra(
                    "LANCAMENTO_ID",
                    lancamento.id
                )

                startActivity(intent)

            },

            onItemLongClick = { _ ->

                // Nesta tela ainda não faremos exclusão.
                // Apenas ignoramos o clique longo.

            }

        )

        recyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}