package com.example.currencyconverter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.currencyconverter.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private var currentInput = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val buttons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9,
            binding.btnPlus, binding.btnMinus,
            binding.btnMultiply, binding.btnDivide,
            binding.btnDot
        )

        for (btn in buttons) {
            btn.setOnClickListener {
                val value = (it as Button).text.toString()
                currentInput += value
                binding.inputText.text = currentInput
            }
        }

        // Clear
        binding.btnClear.setOnClickListener {
            currentInput = ""
            binding.inputText.text = ""
        }

        // Equals
        binding.btnEquals.setOnClickListener {
            if (currentInput.isNotEmpty()) {
                try {
                    val result = eval(currentInput)
                    binding.inputText.text = result.toString()
                    currentInput = result.toString()
                } catch (e: Exception) {
                    binding.inputText.text = "Error"
                }
            }
        }
    }

    // Baaki eval function wahi rahega jo aapka tha...
    private fun eval(expression: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < expression.length) expression[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                return parseExpression()
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+'.code) -> x += parseTerm()
                        eat('-'.code) -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos

                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else {
                    while (ch in '0'.code..'9'.code || ch == '.'.code) nextChar()
                    val part = expression.substring(startPos, pos)
                    x = if (part.isNotEmpty()) part.toDouble() else 0.0
                }

                return x
            }
        }.parse()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}