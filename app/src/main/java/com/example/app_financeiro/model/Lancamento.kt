package com.example.app_financeiro.model

data class Lancamento(
    val id: Int,
    val descricao: String,
    val valor: Double,
    val data: String,
    val tipo: String
)