package com.luizeduardobrandao.calculadorasimplesmvvm.viewmodel

class CalculatorViewModel {
}

// - Expõe LiveData<String> ou LiveData<Double> para o resultado e para o display atual.
// - Recebe eventos da View (botões pressionados), chama o CalculatorModel e atualiza o LiveData.
// - Por que? Desacopla a lógica de negócio (Model) da interface (View), respeitando MVVM e
//   sobrevivendo a mudanças de configuração.