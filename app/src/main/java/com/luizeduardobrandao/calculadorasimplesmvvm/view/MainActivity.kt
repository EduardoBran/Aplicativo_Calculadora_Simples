package com.luizeduardobrandao.calculadorasimplesmvvm.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.luizeduardobrandao.calculadorasimplesmvvm.R
import com.luizeduardobrandao.calculadorasimplesmvvm.databinding.ActivityMainBinding
import com.luizeduardobrandao.calculadorasimplesmvvm.viewmodel.CalculatorViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1) Infla o layout e seta a ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2) Configura LiveData observers e click listeners
        setObservers()
        setListeners()

    }

    // Agrupa todos os observers de LiveData
    private fun setObservers() {
        viewModel.expression.observe(this) { expr ->
            binding.textviewFormula.text = expr
        }

        viewModel.result.observe(this) { res ->
            binding.textviewResultado.text = res
        }
    }

    // Agrupa todos os listeners de clique nos botões
    private fun setListeners() = with(binding) {
        btn0.setOnClickListener { viewModel.onInput("0") }
        btn1.setOnClickListener { viewModel.onInput("1") }
        btn2.setOnClickListener { viewModel.onInput("2") }
        btn3.setOnClickListener { viewModel.onInput("3") }
        btn4.setOnClickListener { viewModel.onInput("4") }
        btn5.setOnClickListener { viewModel.onInput("5") }
        btn6.setOnClickListener { viewModel.onInput("6") }
        btn7.setOnClickListener { viewModel.onInput("7") }
        btn8.setOnClickListener { viewModel.onInput("8") }
        btn9.setOnClickListener { viewModel.onInput("9") }

        btnDot.setOnClickListener { viewModel.onInput(".") }
        btnPercent.setOnClickListener { viewModel.onInput("%") }

        btnAdd.setOnClickListener { viewModel.onInput("+") }
        btnSub.setOnClickListener { viewModel.onInput("-") }
        btnMul.setOnClickListener { viewModel.onInput("x") }
        btnDiv.setOnClickListener { viewModel.onInput("÷") }

        btnDelete.setOnClickListener { viewModel.onDelete() }

        btnClear.setOnClickListener { viewModel.onClear() }

        btnEquals.setOnClickListener { viewModel.onEquals() }
    }
}

// - Faz binding do layout.
// - Observa as LiveData do ViewModel para atualizar o TextView.
// - Configura os onClickListener dos botões para chamar métodos do ViewModel.
// - Por que? A View só “mostra” e “escuta” dados, sem processar nenhuma regra de cálculo.