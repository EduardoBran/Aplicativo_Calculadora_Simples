package com.luizeduardobrandao.calculadorasimplesmvvm.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.luizeduardobrandao.calculadorasimplesmvvm.model.CalculatorModel

// - Expõe LiveData<String> ou LiveData<Double> para o resultado e para o display atual.
// - Recebe eventos da View (botões pressionados), chama o CalculatorModel e atualiza o LiveData.
// - Por que? Desacopla a lógica de negócio (Model) da interface (View), respeitando MVVM e
//   sobrevivendo a mudanças de configuração.
class CalculatorViewModel: ViewModel() {

    private val viewModel = CalculatorModel()

    // LiveData que a View vai observar para exibir a expressão
    private val _expression = MutableLiveData("0")
    val expression: LiveData<String> = _expression

    // LiveData que a View vai observar para exibir o resultado ou "Error"
    private val _result = MutableLiveData("0")
    val result: LiveData<String> = _result


    // Chamado quando o usuário aperta qualquer botão de número, ponto, operador ou %
    fun onInput(token: String) {
        viewModel.input(token)
        _expression.value = viewModel.getExpression()
    }

    // Chamado quando o usuário aperta o botão “⌫”
    fun onDelete() {
        viewModel.deleteLast()
        _expression.value = viewModel.getExpression()
    }

    // Chamado quando o usuário aperta “C”
    fun onClear() {
        viewModel.clear()
        _expression.value = viewModel.getExpression()
        _result.value = "0"
    }

    // Chamado quando o usuário aperta “=”
    fun onEquals() {
        _result.value = viewModel.calculate()
    }
}

