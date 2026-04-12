package com.example.currencyconverter

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class CalculatorFragment : Fragment(R.layout.fragment_calculator) {

    private lateinit var inputText: TextView
    private var currentInput = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputText = view.findViewById(R.id.inputText)

        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnPlus, R.id.btnMinus,
            R.id.btnMultiply, R.id.btnDivide,
            R.id.btnDot
        )

        for (id in buttons) {
            view.findViewById<Button>(id).setOnClickListener {
                val value = (it as Button).text.toString()
                currentInput += value
                inputText.text = currentInput
            }
        }

        // Clear
        view.findViewById<Button>(R.id.btnClear).setOnClickListener {
            currentInput = ""
            inputText.text = ""
        }

        // Equals
        view.findViewById<Button>(R.id.btnEquals).setOnClickListener {
            try {
                val result = eval(currentInput)
                inputText.text = result.toString()
                currentInput = result.toString()
            } catch (e: Exception) {
                inputText.text = "Error"
            }
        }
    }

    // Simple expression evaluator
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
                val x = parseExpression()
                return x
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
                    x = expression.substring(startPos, pos).toDouble()
                }

                return x
            }
        }.parse()

    }
}