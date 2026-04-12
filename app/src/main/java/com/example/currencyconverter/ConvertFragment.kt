package com.example.currencyconverter

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageButton

class ConvertFragment : Fragment(R.layout.fragment_convert) {

    private lateinit var amountInput: EditText
    private lateinit var fromCurrency: Spinner
    private lateinit var toCurrency: Spinner
    private lateinit var resultText: TextView
    private lateinit var convertBtn: Button
    private lateinit var swapBtn: ImageButton
    private val currencyList = listOf("USD", "PKR", "EUR", "INR", "GBP")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        amountInput = view.findViewById(R.id.amountInput)
        fromCurrency = view.findViewById(R.id.fromCurrency)
        toCurrency = view.findViewById(R.id.toCurrency)
        resultText = view.findViewById(R.id.resultText)
        convertBtn = view.findViewById(R.id.convertBtn)
        swapBtn = view.findViewById(R.id.swapBtn)

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            currencyList)

        fromCurrency.adapter = adapter
        toCurrency.adapter = adapter

        // Default selection
        fromCurrency.setSelection(0) // USD
        toCurrency.setSelection(1)   // PKR

        convertBtn.setOnClickListener { convertCurrency() }
        swapBtn.setOnClickListener { swapCurrencies() }
        val sharedPrefs = requireActivity()
            .getSharedPreferences("settings", Context.MODE_PRIVATE)

        val defaultCurrency = sharedPrefs.getString("default_currency", "USD")

        val position = currencyList.indexOf(defaultCurrency)
        if (position >= 0) {
            fromCurrency.setSelection(position)
        }
    }

    private fun convertCurrency() {
        val amount = amountInput.text.toString().toDoubleOrNull()

        if (amount == null) {
            resultText.text = "Enter valid amount"
            return
        }

        val from = fromCurrency.selectedItem.toString()
        val to = toCurrency.selectedItem.toString()

        resultText.text = "Converting..."

        RetrofitInstance.api.getRates(from)
            .enqueue(object : Callback<CurrencyResponse> {

                override fun onResponse(
                    call: Call<CurrencyResponse>,
                    response: Response<CurrencyResponse>
                ) {
                    if (response.isSuccessful) {
                        val rates = response.body()?.rates
                        val rate = rates?.get(to)

                        if (rate != null && rates != null) {

                            val fromRate = rates[from] ?: 1.0
                            val result = amount * (rate / fromRate)

                            val resultString =
                                "$amount $from = %.2f $to".format(result)

                            resultText.text = resultString

                            saveHistory(resultString)

                        } else {
                            resultText.text = "Conversion not available"
                        }
                    } else {
                        resultText.text = "API Error"
                    }
                }

                override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                    resultText.text = "Check internet connection"
                }
            })
    }

    private fun swapCurrencies() {
        val fromPos = fromCurrency.selectedItemPosition
        val toPos = toCurrency.selectedItemPosition

        fromCurrency.setSelection(toPos)
        toCurrency.setSelection(fromPos)
    }

    private fun saveHistory(entry: String) {
        val sharedPrefs = requireActivity()
            .getSharedPreferences("conversion_history", Context.MODE_PRIVATE)

        val oldHistory = sharedPrefs.getString("history", "") ?: ""
        val newHistory =
            if (oldHistory.isEmpty()) entry else "$oldHistory\n$entry"

        sharedPrefs.edit().putString("history", newHistory).apply()
    }
}