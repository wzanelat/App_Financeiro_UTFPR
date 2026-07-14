package com.example.app_financeiro.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_financeiro.adapter.LancamentoRecyclerAdapter
import com.example.app_financeiro.database.DatabaseHelper
import com.example.app_financeiro.databinding.ActivityMainBinding
import com.example.app_financeiro.model.Lancamento
import com.example.app_financeiro.repository.LancamentoRepository
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: LancamentoRepository

    private var ultimoLancamentoExcluido: Lancamento? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain)

        inicializarRepository()
        configurarRecyclerView()
        configurarBotaoNovoLancamento()
    }

    override fun onResume() {
        super.onResume()
        carregarDados()
    }

    private fun inicializarRepository() {
        val databaseHelper = DatabaseHelper(this)

        repository = LancamentoRepository(
            db = databaseHelper
        )
    }

    private fun configurarRecyclerView() {
        binding.recyclerLancamentos.layoutManager =
            LinearLayoutManager(this)
    }

    private fun configurarBotaoNovoLancamento() {
        binding.fabNovoLancamento.setOnClickListener {
            val intent = Intent(
                this,
                CadastroActivity::class.java
            )

            startActivity(intent)
        }
    }

    private fun carregarDados() {
        val lista = repository.listar()

        var totalReceitas = 0.0
        var totalDespesas = 0.0

        for (lancamento in lista) {
            if (lancamento.tipo == "Receita") {
                totalReceitas += lancamento.valor
            } else {
                totalDespesas += lancamento.valor
            }
        }

        atualizarResumo(
            totalReceitas = totalReceitas,
            totalDespesas = totalDespesas
        )

        configurarAdapter(lista)
    }

    private fun atualizarResumo(
        totalReceitas: Double,
        totalDespesas: Double
    ) {
        val saldo = totalReceitas - totalDespesas

        binding.textReceitas.text =
            "Receitas: R$ %.2f".format(totalReceitas)

        binding.textDespesas.text =
            "Despesas: R$ %.2f".format(totalDespesas)

        binding.textSaldo.text =
            "Saldo: R$ %.2f".format(saldo)
    }

    private fun configurarAdapter(
        lista: ArrayList<Lancamento>
    ) {
        binding.recyclerLancamentos.adapter =
            LancamentoRecyclerAdapter(
                lista = lista,

                onItemClick = { lancamento ->
                    abrirEdicao(lancamento)
                },

                onItemLongClick = { lancamento ->
                    confirmarExclusao(lancamento)
                }
            )
    }

    private fun abrirEdicao(lancamento: Lancamento) {
        val intent = Intent(
            this,
            CadastroActivity::class.java
        )

        intent.putExtra(
            "LANCAMENTO_ID",
            lancamento.id
        )

        startActivity(intent)
    }

    private fun confirmarExclusao(lancamento: Lancamento) {
        AlertDialog.Builder(this)
            .setTitle("Excluir lançamento")
            .setMessage(
                "Deseja excluir o lançamento \"${lancamento.descricao}\"?"
            )
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Excluir") { _, _ ->
                excluirLancamento(lancamento)
            }
            .show()
    }

    private fun excluirLancamento(lancamento: Lancamento) {
        ultimoLancamentoExcluido = lancamento

        val sucesso = repository.excluir(
            id = lancamento.id
        )

        if (sucesso) {
            carregarDados()

            Snackbar.make(
                binding.root,
                "Lançamento excluído",
                Snackbar.LENGTH_LONG
            )
                .setAction("DESFAZER") {
                    restaurarLancamento()
                }
                .show()
        } else {
            Snackbar.make(
                binding.root,
                "Erro ao excluir lançamento",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun restaurarLancamento() {
        val lancamento = ultimoLancamentoExcluido

        if (lancamento == null) {
            Snackbar.make(
                binding.root,
                "Nenhum lançamento disponível para restaurar",
                Snackbar.LENGTH_SHORT
            ).show()

            return
        }

        val sucesso = repository.inserir(
            descricao = lancamento.descricao,
            valor = lancamento.valor,
            data = lancamento.data,
            tipo = lancamento.tipo
        )

        if (sucesso) {
            ultimoLancamentoExcluido = null
            carregarDados()

            Snackbar.make(
                binding.root,
                "Lançamento restaurado",
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            Snackbar.make(
                binding.root,
                "Não foi possível restaurar o lançamento",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }
}