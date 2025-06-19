package com.luizeduardobrandao.calculadorasimplesmvvm.model

// CalculatorModel: mantém o estado da expressão como uma lista de tokens
// e expõe métodos para inserir, apagar, limpar e calcular o resultado.
class CalculatorModel {

    // Cada token é ou um número (ex: "12", "3.5"), um operador ("+", "-", "×", "÷"), "." e "%".
    private val tokens = mutableListOf<String>()

    // Retorna a expressão completa para exibir na View
    fun getExpression(): String {
        return if (tokens.isEmpty()){
            "0"
        } else {
            tokens.joinToString(separator = "")
        }
    }

     // Insere um token segundo a lógica:
     // - Se for dígito ou ponto e o último token também for número, concatena;
     // - Se for operador, substitui o último operador (se houver) ou adiciona.
    fun input(token: String){
        when {
            // verifica se a string token contem exatamente um caractere que seja um dígito de 0 a 9 ou um ponto "."
            token.matches(Regex("[0-9].")) -> {
                // se já existe um token anterior e ele também é um número (ou decimal) concatena com "+"
                if (tokens.isNotEmpty() && tokens.last().matches(Regex("[0-9.]+"))){
                    tokens[tokens.lastIndex] = tokens.last() + token
                }
                else {
                    tokens += token
                }
            }
            // porcentagem
            token == "%" -> {
                // aplica porcentagem ao último número
                if (tokens.isNotEmpty() && tokens.last().matches(Regex("[0-9.]+"))){
                    val num = tokens.removeAt(tokens.lastIndex).toDoubleOrNull() ?: 0.0
                    tokens += (num / 100).toString()
                }
            }
            // operadores
            token in listOf("+", "-", "x", "÷") -> {
                // evita dois operadores seguidos
                if (tokens.isNotEmpty() && tokens.last() in listOf("+", "-", "x", "÷")){
                    tokens[tokens.lastIndex] = token
                }
                else {
                    tokens += token
                }
            }
            else -> {
                // ignora qualquer outro input inválido
            }
        }
    }

    // Remove o último caractere ou, se o token tiver só 1 char, remove o token inteiro
    fun deleteLast(){
        if (tokens.isEmpty()) return

        val last = tokens.last()

        if (last.length > 1 && last.matches(Regex("[0-9.]+"))){
            // retira apenas o último dígito/ponto
            tokens[tokens.lastIndex] = last.dropLast(1)
        }
        else {
            // remove o token inteiro (número ou operador)
            tokens.removeAt(tokens.lastIndex)
        }
    }

    // Limpa toda a expressão
    fun clear() {
        tokens.clear()
    }

    // Avalia a expressão atual e retorna o resultado formatado.
    fun calculate(): String {
        return try {
            val result = evaluateExpression()
            formatResult(result)
        }
        catch (e: Exception) {
            "Error"
        }
    }


    // ————— Métodos internos —————

    // Converte tokens em valores e operadores, aplica "*" e "/" primeiro,
    // e depois "+" e "-", retornando um Double.
    private fun evaluateExpression(): Double {

        // 1) Converte cada token String em um item misto: Double para números, String para operadores.
        //    Exemplo: tokens = ["12", "+", "3", "×", "4"]
        //    → items = [12.0, "+", 3.0, "×", 4.0]
        val items = mutableListOf<Any>()

        for (tok in tokens) {
            when {
                // Se for composto apenas de dígitos e ponto, converte para Double
                tok.matches(Regex("[0-9.]+")) -> items += tok.toDouble()
                // Se for um operador válido, mantém como String
                tok in listOf("+", "-", "×", "÷") -> items += tok
                // Caso contrário, erro
                else -> throw IllegalArgumentException("Token inválido: $tok")
            }
        }

        // 2) Primeiro, resolvemos todas as multiplicações e divisões.
        //    Usamos uma pilha auxiliar (`stack`) e um índice `i` para varrer `items`.
        //    Sempre que vemos "×" ou "÷", retiramos o número anterior da pilha,
        //    lemos o próximo número em `items`, calculamos o resultado e o empilhamos.
        //    Assim acabamos com uma sequência em `stack` que só contém + e - ainda não resolvidos.
        val stack = mutableListOf<Any>()
        var i = 0
        while (i < items.size) {
            val item = items[i]

            // Detecta operador de multiplicação ou divisão
            if (item is String && (item == "x" || item == "÷")){
                // pega o número anterior já empilhado
                val prev = stack.removeAt(stack.lastIndex) as Double
                // lê o próximo número na lista original
                val next = items[i + 1] as Double
                // calcula multiplicação ou divisão
                val opResult = if (item == "x") prev * next else prev / next

                // Empilha o resultado da operação
                stack += opResult
                // Avança o índice em 2 posições (pulando operador e número já processados)
                i += 2
            }
            else {
                // Se não for ×/÷, apenas empilha o item (número, + ou -)
                stack += item
                i++
            }
        }

        // 3) Agora só restam adições e subtrações em `stack`.
        //    Basta percorrer de dois em dois (operador + número) aplicando ao acumulador `result`.
        var result = (stack[0] as Double) // inicie com o primeiro número
        i = 1
        while (i < stack.size) {
            val op = stack[i] as String
            val num = stack[i + 1] as Double

            // Aplica + ou -
            result = when (op) {
                "+" -> result + num
                "-" -> result - num
                else -> throw IllegalArgumentException("Operador inesperado: $op")
            }
            // // salta operador + número
            i += 2
        }
        return result
    }

    // Formata Double removendo .0 e zeros finais.
    private fun formatResult(value: Double): String {
        return if (value % 1.0 == 0.0){
            value.toLong().toString()
        }
        else {
            // até 6 casas decimais, sem zeros à direita
            String.format("%.6f", value).trimEnd('0').trimEnd('0')
        }
    }
}
