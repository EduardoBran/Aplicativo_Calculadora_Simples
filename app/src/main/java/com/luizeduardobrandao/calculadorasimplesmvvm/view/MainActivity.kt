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

/*
A arquitetura MVVM (Model–View–ViewModel) organiza seu código em três camadas principais:

1. Model
2. ViewModel
3. View

Vamos ver cada uma aplicada à sua calculadora:

1. Model
Onde fica: no pacote model, na sua classe CalculatorModel.
Responsabilidade principal: toda a lógica de negócio – ou seja, como a calculadora de fato calcula.

- O que ocorre no CalculatorModel
    - Avaliação de expressões: métodos como evaluateExpression() são definidos aqui.
    - Regras de validação: por exemplo, como tratar porcentagem, pontos, sinal de menos.
    - Estados internos puros: mantém apenas dados simples (ex.: lista de tokens, resultados parciais).

Exemplo de mét0do:

fun compute(tokens: List<String>): Double {
  // 1) Parse tokens em números e operadores
  // 2) Aplicar precedência de ×/÷
  // 3) Aplicar +/−
  // 4) Retorna o resultado final
}

Como resultado, o Model não conhece nada de Android, LiveData, Views ou Activities — ele é um
componente puro de Kotlin.

–––

2. ViewModel
Onde fica: no pacote viewmodel, na sua classe CalculatorViewModel.

- Responsabilidade principal:
    - Intermediar Model ⇄ View.
    - Armazenar e expor estado de forma observável (LiveData, StateFlow).
    - Reagir a comandos da View (botões clicados), chamar o Model e propagar resultados para a View.

O que ocorre no CalculatorViewModel

1. Estado observável

// Exposto para a View observar
val expression = MutableLiveData<String>("")      // texto corrente
val result     = MutableLiveData<String>("0")     // resultado formatado

2. Funções de ação

fun onDigit(d: Char) → adiciona dígito à expressão.
fun onOperator(op: Char) → adiciona operador (+, ×, etc).
fun onEqual() → chama CalculatorModel.compute(tokens) e atualiza result.
fun onClear() → limpa expressão e resultado.

3. Conversão

Mantém internamente uma lista de tokens: MutableList<String>.
Quando a View digita “5”, chama viewModel.onDigit('5'), que atualiza tokens e expression.value.
Quando clica “=”, chama viewModel.onEqual(), que passa tokens ao Model e coloca o resultado em result.value.

–––

3. View
Onde fica: no pacote view, na sua MainActivity (ou MainFragment).

- Responsabilidade principal:
    - Renderizar a interface (botões, TextViews).
    - Observar o estado exposto pelo ViewModel e atualizar a UI.
    -Disparar eventos ao ViewModel (cliques nos botões).

O que ocorre na MainActivity

Obtenção do ViewModel

private val viewModel: CalculatorViewModel by viewModels()

Vincular observadores

viewModel.expression.observe(this) { text ->
  binding.textViewFormula.text = text
}
viewModel.result.observe(this) { text ->
  binding.textViewResult.text = text
}

Configurar listeners

binding.btn1.setOnClickListener { viewModel.onDigit('1') }
binding.btnPlus.setOnClickListener { viewModel.onOperator('+') }
binding.btnEqual.setOnClickListener { viewModel.onEqual() }
binding.btnClear.setOnClickListener { viewModel.onClear() }

4. Sem lógica de negócio aqui

- A MainActivity não deve calcular expressões nem entender regras de porcentagem: ela só empurra
  para o ViewModel e atualiza Views.

---

5. Fluxo completo de um clique “5 × 3 =”

Usuário toca “5”
MainActivity chama viewModel.onDigit('5').
No CalculatorViewModel, adiciona "5" em tokens, atualiza expression.value = "5".
A MainActivity observa essa LiveData e escreve “5” no TextView.
Usuário toca “×”
MainActivity chama viewModel.onOperator('×').
CalculatorViewModel adiciona "×" em tokens, atualiza expression.value = "5×".
UI mostra “5×”.
Usuário toca “3”
Chama viewModel.onDigit('3'), tokens vira ["5","×","3"], expression fica “5×3”.
Usuário toca “=”
MainActivity chama viewModel.onEqual().
CalculatorViewModel chama CalculatorModel.compute(listOf("5","×","3"))
Model resolve 5.0 × 3.0 = 15.0 e retorna 15.0.
CalculatorViewModel formata 15.0 para "15" e faz result.value = "15".
UI, observando result, mostra “15” no TextView de resultado.

---

6. Resumo das responsabilidades

Camada	      O que faz
Model	      Lógica de negócio pura (cálculo, validação de tokens)
ViewModel	  Estado observável + orquestração (chama Model, formata dados)
View	      Renderiza UI + dispara eventos (cliques), observa ViewModel


Dessa forma você mantém cada parte bem isolada, fácil de testar e sem “código de negócio”
espalhado pela Activity.
 */