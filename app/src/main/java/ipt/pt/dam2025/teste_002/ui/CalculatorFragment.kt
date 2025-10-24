    package ipt.pt.dam2025.teste_002.ui

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.fragment.app.Fragment
    import com.example.funapp.databinding.FragmentCalculatorBinding
    import javax.script.ScriptEngineManager

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
                    expression += button.text
                    binding.txtExpression.text = expression
                }
            }

            binding.btnEquals.setOnClickListener {
                try {
                    val engine = ScriptEngineManager().getEngineByName("rhino")
                    val result = engine.eval(expression)
                    binding.txtResult.text = result.toString()
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
    }