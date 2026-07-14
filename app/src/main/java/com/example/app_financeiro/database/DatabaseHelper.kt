package com.example.app_financeiro.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.app_financeiro.model.Lancamento

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "financeiro.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE lancamentos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                descricao TEXT NOT NULL,
                valor REAL NOT NULL,
                data TEXT NOT NULL,
                tipo TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS lancamentos")
        onCreate(db)
    }

    fun inserirLancamento(
        descricao: String,
        valor: Double,
        data: String,
        tipo: String
    ): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("descricao", descricao)
            put("valor", valor)
            put("data", data)
            put("tipo", tipo)
        }

        val resultado = db.insert("lancamentos", null, values)
        db.close()

        return resultado != -1L
    }

    fun listarLancamentos(): ArrayList<Lancamento> {
        val lista = ArrayList<Lancamento>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM lancamentos ORDER BY id DESC",
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Lancamento(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        descricao = it.getString(
                            it.getColumnIndexOrThrow("descricao")
                        ),
                        valor = it.getDouble(
                            it.getColumnIndexOrThrow("valor")
                        ),
                        data = it.getString(
                            it.getColumnIndexOrThrow("data")
                        ),
                        tipo = it.getString(
                            it.getColumnIndexOrThrow("tipo")
                        )
                    )
                )
            }
        }

        db.close()
        return lista
    }

    fun buscarLancamentoPorId(id: Int): Lancamento? {
        val db = readableDatabase

        val cursor = db.query(
            "lancamentos",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        val lancamento = cursor.use {
            if (it.moveToFirst()) {
                Lancamento(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    descricao = it.getString(
                        it.getColumnIndexOrThrow("descricao")
                    ),
                    valor = it.getDouble(
                        it.getColumnIndexOrThrow("valor")
                    ),
                    data = it.getString(
                        it.getColumnIndexOrThrow("data")
                    ),
                    tipo = it.getString(
                        it.getColumnIndexOrThrow("tipo")
                    )
                )
            } else {
                null
            }
        }

        db.close()
        return lancamento
    }

    fun atualizarLancamento(
        id: Int,
        descricao: String,
        valor: Double,
        data: String,
        tipo: String
    ): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put("descricao", descricao)
            put("valor", valor)
            put("data", data)
            put("tipo", tipo)
        }

        val linhasAlteradas = db.update(
            "lancamentos",
            values,
            "id = ?",
            arrayOf(id.toString())
        )

        db.close()
        return linhasAlteradas > 0
    }

    fun excluirLancamento(id: Int): Boolean {
        val db = writableDatabase

        val linhasExcluidas = db.delete(
            "lancamentos",
            "id = ?",
            arrayOf(id.toString())
        )

        db.close()

        return linhasExcluidas > 0
    }
}