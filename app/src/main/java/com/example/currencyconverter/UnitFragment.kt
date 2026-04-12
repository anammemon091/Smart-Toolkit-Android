package com.example.currencyconverter

import android.os.Bundle
import android.content.Context
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment

class UnitFragment : Fragment(R.layout.fragment_unit) {

    private lateinit var inputValue: EditText
    private lateinit var fromSpinner: Spinner
    private lateinit var toSpinner: Spinner
    private lateinit var resultText: TextView
    private lateinit var convertBtn: Button

    private val units = listOf("Meter", "Kilometer", "Centimeter")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputValue = view.findViewById(R.id.inputValue)
        fromSpinner = view.findViewById(R.id.fromUnit)
        toSpinner = view.findViewById(R.id.toUnit)
        resultText = view.findViewById(R.id.resultText)
        convertBtn = view.findViewById(R.id.convertBtn)

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, units)

        fromSpinner.adapter = adapter
        toSpinner.adapter = adapter

        convertBtn.setOnClickListener {
            convertUnit()
        }
    }

    private fun convertUnit() {
        val value = inputValue.text.toString().toDoubleOrNull()

        if (value == null) {
            resultText.text = "Enter valid value"
            return
        }

        val from = fromSpinner.selectedItem.toString()
        val to = toSpinner.selectedItem.toString()

        val result = when (from to to) {
            "Meter" to "Kilometer" -> value / 1000
            "Kilometer" to "Meter" -> value * 1000
            "Meter" to "Centimeter" -> value * 100
            "Centimeter" to "Meter" -> value / 100
            "Kilometer" to "Centimeter" -> value * 100000
            "Centimeter" to "Kilometer" -> value / 100000
            else -> value
        }
        val formattedResult = "$value $from = %.2f $to".format(result)

        resultText.text = formattedResult

// ✅ CALL THE FUNCTION HERE
        saveHistory("UNIT $formattedResult")

        resultText.text = "$value $from = %.2f $to".format(result)
    }
    private fun saveHistory(entry: String) {
        val prefs = requireActivity()
            .getSharedPreferences("history", Context.MODE_PRIVATE)
        val oldData = prefs.getString("data", "") ?: ""

        val newData = if (oldData.isEmpty()) {
            entry
        } else {
            oldData + "\n" + entry
        }

        prefs.edit().putString("data", newData).apply()
    }
}