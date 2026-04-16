package com.example.currencyconverter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.currencyconverter.databinding.FragmentUnitBinding

class UnitFragment : Fragment() {

    private var _binding: FragmentUnitBinding? = null
    private val binding get() = _binding!!

    private val units = listOf("Meter", "Kilometer", "Centimeter")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, units)

        binding.fromUnit.adapter = adapter
        binding.toUnit.adapter = adapter

        binding.convertBtn.setOnClickListener {
            convertUnit()
        }
        val sharedPrefs = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val defaultUnit = sharedPrefs.getString("default_unit", "Meter")
        val position = units.indexOf(defaultUnit)
        binding.fromUnit.setSelection(position)
    }

    private fun convertUnit() {
        val value = binding.inputValue.text.toString().toDoubleOrNull()

        if (value == null) {
            binding.resultText.text = "Enter valid value"
            return
        }

        val from = binding.fromUnit.selectedItem.toString()
        val to = binding.toUnit.selectedItem.toString()

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
        binding.resultText.text = formattedResult

        saveHistory("UNIT: $formattedResult")
    }

    private fun saveHistory(entry: String) {
        val prefs = requireActivity()
            .getSharedPreferences("history", Context.MODE_PRIVATE)
        val oldData = prefs.getString("data", "") ?: ""

        val newData = if (oldData.isEmpty()) {
            entry
        } else {
            "$oldData\n$entry"
        }

        prefs.edit().putString("data", newData).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}