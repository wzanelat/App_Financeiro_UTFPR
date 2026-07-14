package com.example.app_financeiro.repository

import com.example.app_financeiro.database.DatabaseHelper
import com.example.app_financeiro.model.Lancamento

class LancamentoRepository(
    private val db: DatabaseHelper
) {

    fun inserir(
        descricao: String,
        valor: Double,
        data: String,
        tipo: String
    ): Boolean {
        return db.inserirLancamento(
            descricao = descricao,
            valor = valor,
            data = data,
            tipo = tipo
        )
    }

    fun listar(): ArrayList<Lancamento> {
        return db.listarLancamentos()
    }

    fun buscarPorId(id: Int): Lancamento? {
        return db.buscarLancamentoPorId(id)
    }

    fun atualizar(
        id: Int,
        descricao: String,
        valor: Double,
        data: String,
        tipo: String
    ): Boolean {
        return db.atualizarLancamento(
            id = id,
            descricao = descricao,
            valor = valor,
            data = data,
            tipo = tipo
        )
    }

    fun excluir(id: Int): Boolean {
        return db.excluirLancamento(id)
    }
}