package com.example.currencyconverter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.currencyconverter.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Lists for Spinners
    private val currencyList = listOf("USD", "PKR", "EUR", "INR", "GBP")
    private val unitList = listOf("Meter", "Kilometer", "Millimeter")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefs = requireActivity()
            .getSharedPreferences("settings", Context.MODE_PRIVATE)

        // --- 1. Currency Spinner Setup ---
        val currAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, currencyList)
        binding.defaultCurrencySpinner.adapter = currAdapter

        val savedCurrency = sharedPrefs.getString("default_currency", "USD")
        binding.defaultCurrencySpinner.setSelection(currencyList.indexOf(savedCurrency))

        binding.defaultCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                sharedPrefs.edit().putString("default_currency", currencyList[pos]).apply()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // --- 2. Unit Spinner Setup ---
        val unitAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, unitList)
        binding.defaultUnitSpinner.adapter = unitAdapter

        val savedUnit = sharedPrefs.getString("default_unit", "Meter")
        binding.defaultUnitSpinner.setSelection(unitList.indexOf(savedUnit))

        binding.defaultUnitSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                sharedPrefs.edit().putString("default_unit", unitList[pos]).apply()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}