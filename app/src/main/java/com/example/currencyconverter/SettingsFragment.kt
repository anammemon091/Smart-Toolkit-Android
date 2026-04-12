package com.example.currencyconverter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    private lateinit var darkModeSwitch: Switch
    private lateinit var currencySpinner: Spinner

    private val currencyList = listOf("USD", "PKR", "EUR", "INR", "GBP")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        darkModeSwitch = view.findViewById(R.id.darkModeSwitch)
        currencySpinner = view.findViewById(R.id.defaultCurrencySpinner)

        val sharedPrefs = requireActivity()
            .getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Spinner setup
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, currencyList)
        currencySpinner.adapter = adapter

        val savedCurrency = sharedPrefs.getString("default_currency", "USD")
        val position = currencyList.indexOf(savedCurrency)
        if (position >= 0) currencySpinner.setSelection(position)

        currencySpinner.onItemSelectedListener = object :
            android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCurrency = currencyList[position]
                sharedPrefs.edit().putString("default_currency", selectedCurrency).apply()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Dark Mode
        val isDarkMode = sharedPrefs.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}