package com.example.app_financeiro.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.app_financeiro.R
import com.example.app_financeiro.database.DatabaseHelper
import com.example.app_financeiro.model.ModoCadastro
import com.example.app_financeiro.repository.LancamentoRepository
import java.util.Calendar

class CadastroActivity : AppCompatActivity() {

    private lateinit var repository: LancamentoRepository

    private lateinit var editDescricao: EditText
    private lateinit var editValor: EditText
    private lateinit var editData: EditText
    private lateinit var radioReceita: RadioButton
    private lateinit var radioDespesa: RadioButton
    private lateinit var btnSalvar: Button
    private lateinit var textTituloCadastro: TextView

    private var modoCadastro = ModoCadastro.NOVO
    private var lancamentoId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        inicializarRepository()
        inicializarComponentes()
        configurarDatePicker()
        verificarModoCadastro()
        configurarBotaoSalvar()
    }

    private fun inicializarRepository() {
        val databaseHelper = DatabaseHelper(this)

        repository = LancamentoRepository(
            db = databaseHelper
        )
    }

    private fun inicializarComponentes() {
        editDescricao = findViewById(R.id.editDescricao)
        editValor = findViewById(R.id.editValor)
        editData = findViewById(R.id.editData)

        radioReceita = findViewById(R.id.radioReceita)
        radioDespesa = findViewById(R.id.radioDespesa)

        btnSalvar = findViewById(R.id.btnSalvar)
        textTituloCadastro = findViewById(R.id.textTituloCadastro)
    }

    private fun configurarDatePicker() {
        editData.setOnClickListener {
            val calendario = Calendar.getInstance()

            val ano = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val dataFormatada = "%02d/%02d/%04d".format(
                        dayOfMonth,
                        month + 1,
                        year
                    )

                    editData.setText(dataFormatada)
                },
                ano,
                mes,
                dia
            ).show()
        }
    }

    private fun verificarModoCadastro() {
        lancamentoId = intent.getIntExtra(
            "LANCAMENTO_ID",
            -1
        )

        if (lancamentoId != -1) {
            modoCadastro = ModoCadastro.EDICAO
            carregarLancamento()
        } else {
            modoCadastro = ModoCadastro.NOVO

            textTituloCadastro.text = "Novo lançamento"
            btnSalvar.text = "Salvar lançamento"
        }
    }

    private fun carregarLancamento() {
        val lancamento = repository.buscarPorId(
            id = lancamentoId
        )

        if (lancamento == null) {
            Toast.makeText(
                this,
                "Lançamento não encontrado",
                Toast.LENGTH_SHORT
            ).show()

            finish()
            return
        }

        textTituloCadastro.text = "Editar lançamento"
        btnSalvar.text = "Salvar alterações"

        editDescricao.setText(lancamento.descricao)
        editValor.setText(lancamento.valor.toString())
        editData.setText(lancamento.data)

        if (lancamento.tipo == "Receita") {
            radioReceita.isChecked = true
        } else {
            radioDespesa.isChecked = true
        }
    }

    private fun configurarBotaoSalvar() {
        btnSalvar.setOnClickListener {
            salvarLancamento()
        }
    }

    private fun salvarLancamento() {
        val descricao = editDescricao.text
            .toString()
            .trim()

        val valorTexto = editValor.text
            .toString()
            .trim()

        val data = editData.text
            .toString()
            .trim()

        if (
            descricao.isEmpty() ||
            valorTexto.isEmpty() ||
            data.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Preencha todos os campos",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val valor = valorTexto
            .replace(",", ".")
            .toDoubleOrNull()

        if (valor == null || valor <= 0.0) {
            Toast.makeText(
                this,
                "Informe um valor válido",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val tipo = if (radioReceita.isChecked) {
            "Receita"
        } else {
            "Despesa"
        }

        val sucesso = when (modoCadastro) {
            ModoCadastro.NOVO -> {
                repository.inserir(
                    descricao = descricao,
                    valor = valor,
                    data = data,
                    tipo = tipo
                )
            }

            ModoCadastro.EDICAO -> {
                repository.atualizar(
                    id = lancamentoId,
                    descricao = descricao,
                    valor = valor,
                    data = data,
                    tipo = tipo
                )
            }
        }

        tratarResultado(sucesso)
    }

    private fun tratarResultado(sucesso: Boolean) {
        if (sucesso) {
            val mensagem = when (modoCadastro) {
                ModoCadastro.NOVO ->
                    "Lançamento salvo com sucesso"

                ModoCadastro.EDICAO ->
                    "Lançamento atualizado com sucesso"
            }

            Toast.makeText(
                this,
                mensagem,
                Toast.LENGTH_SHORT
            ).show()

            finish()
        } else {
            Toast.makeText(
                this,
                "Não foi possível salvar o lançamento",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
