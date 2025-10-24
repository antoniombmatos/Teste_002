package ipt.pt.dam2025.teste_002.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ipt.pt.dam2025.teste_002.databinding.FragmentCalculatorBinding
import java.util.ArrayDeque
import kotlin.math.round

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    private var expression = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9,
            binding.btnPlus, binding.btnMinus, binding.btnMul, binding.btnDiv
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                // acrescenta espaço para tornar o parsing mais simples (opcional)
                expression += button.text.toString()
                binding.txtExpression.text = expression
            }
        }

        binding.btnEquals.setOnClickListener {
            try {
                val result = evalExpression(expression)
                binding.txtResult.text = result
            } catch (e: Exception) {
                binding.txtResult.text = "Erro"
            }
        }

        binding.btnClear.setOnClickListener {
            expression = ""
            binding.txtExpression.text = ""
            binding.txtResult.text = ""
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Avalia uma expressão simples com + - * / e parêntesis.
     * Usa shunting-yard para gerar RPN e depois avalia.
     * Retorna String formatada (remove .0 quando for inteiro).
     */
    private fun evalExpression(expr: String): String {
        if (expr.isBlank()) throw IllegalArgumentException("Empty expression")

        // Tokenize (números, operadores, parêntesis)
        val tokens = mutableListOf<String>()
        var i = 0
        val s = expr.replace(" ", "")
        while (i < s.length) {
            val c = s[i]
            when {
                c.isDigit() || c == '.' -> {
                    val start = i
                    i++
                    while (i < s.length && (s[i].isDigit() || s[i] == '.')) i++
                    tokens.add(s.substring(start, i))
                    continue
                }
                c == '+' || c == '-' || c == '*' || c == '/' || c == '(' || c == ')' -> {
                    // tratar sinal negativo: se '-' no começo ou depois de '(' ou outro operador, é parte do número
                    if (c == '-') {
                        val prev = if (tokens.isEmpty()) null else tokens.last()
                        if (prev == null || prev in listOf("+", "-", "*", "/", "(")) {
                            // sinal negativo: consumir número a seguir
                            val start = i
                            i++
                            // se próximo for dígito ou ponto
                            if (i < s.length && (s[i].isDigit() || s[i] == '.')) {
                                i++
                                while (i < s.length && (s[i].isDigit() || s[i] == '.')) i++
                                tokens.add(s.substring(start, i)) // inclui o '-'
                                continue
                            } else {
                                // '-' isolado (improvável), trata como operador
                                tokens.add("-")
                                continue
                            }
                        }
                    }
                    tokens.add(c.toString())
                    i++
                }
                else -> throw IllegalArgumentException("Caractere inválido: $c")
            }
        }

        // Shunting-yard -> RPN
        val output = mutableListOf<String>()
        val ops = ArrayDeque<String>()
        fun precedence(op: String) = when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> 0
        }
        for (tk in tokens) {
            when {
                tk.toDoubleOrNull() != null -> output.add(tk) // número
                tk == "+" || tk == "-" || tk == "*" || tk == "/" -> {
                    while (ops.isNotEmpty() && ops.peek() != "(" &&
                        precedence(ops.peek()) >= precedence(tk)) {
                        output.add(ops.pop())
                    }
                    ops.push(tk)
                }
                tk == "(" -> ops.push(tk)
                tk == ")" -> {
                    while (ops.isNotEmpty() && ops.peek() != "(") {
                        output.add(ops.pop())
                    }
                    if (ops.isEmpty() || ops.peek() != "(") throw IllegalArgumentException("Parêntesis desbalanceados")
                    ops.pop() // remove '('
                }
                else -> throw IllegalArgumentException("Token inválido: $tk")
            }
        }
        while (ops.isNotEmpty()) {
            if (ops.peek() == "(" || ops.peek() == ")") throw IllegalArgumentException("Parêntesis desbalanceados")
            output.add(ops.pop())
        }

        // Avaliar RPN
        val stack = ArrayDeque<Double>()
        for (tk in output) {
            val num = tk.toDoubleOrNull()
            if (num != null) {
                stack.push(num)
            } else {
                // operador
                if (stack.size < 2) throw IllegalArgumentException("Expressão inválida")
                val b = stack.pop()
                val a = stack.pop()
                val res = when (tk) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> {
                        if (b == 0.0) throw ArithmeticException("Divisão por zero")
                        a / b
                    }
                    else -> throw IllegalArgumentException("Operador desconhecido: $tk")
                }
                stack.push(res)
            }
        }
        if (stack.size != 1) throw IllegalArgumentException("Expressão inválida")
        val result = stack.pop()

        // Format: se inteiro, mostra sem .0, senão arredonda a 8 decimais removendo zeros finais
        val rounded = (round(result * 1e8) / 1e8)
        val asLong = rounded.toLong()
        return if (rounded == asLong.toDouble()) asLong.toString() else rounded.toString()
    }
}
