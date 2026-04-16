package com.example.currencyconverter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.currencyconverter.databinding.FragmentConvertBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConvertFragment : Fragment() {

    private var _binding: FragmentConvertBinding? = null
    private val binding get() = _binding!!

    private val currencyList = listOf("USD", "PKR", "EUR", "INR", "GBP")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConvertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            currencyList)

        binding.fromCurrency.adapter = adapter
        binding.toCurrency.adapter = adapter

        // Default selection
        binding.fromCurrency.setSelection(0) // USD
        binding.toCurrency.setSelection(1)   // PKR

        binding.convertBtn.setOnClickListener { convertCurrency() }
        binding.swapBtn.setOnClickListener { swapCurrencies() }

        val sharedPrefs = requireActivity()
            .getSharedPreferences("settings", Context.MODE_PRIVATE)

        val defaultCurrency = sharedPrefs.getString("default_currency", "USD")

        val position = currencyList.indexOf(defaultCurrency)
        if (position >= 0) {
            binding.fromCurrency.setSelection(position)
        }
    }

    private fun convertCurrency() {
        val amount = binding.amountInput.text.toString().toDoubleOrNull()

        if (amount == null) {
            binding.resultText.text = "Enter valid amount"
            return
        }

        val from = binding.fromCurrency.selectedItem.toString()
        val to = binding.toCurrency.selectedItem.toString()

        binding.resultText.text = "Converting..."

        RetrofitInstance.api.getRates(from)
            .enqueue(object : Callback<CurrencyResponse> {
                override fun onResponse(
                    call: Call<CurrencyResponse>,
                    response: Response<CurrencyResponse>
                ) {
                    if (response.isSuccessful) {
                        val rates = response.body()?.rates
                        val rate = rates?.get(to)

                        if (rate != null) {
                            val result = amount * rate
                            val resultString = "$amount $from = %.2f $to".format(result)
                            binding.resultText.text = resultString
                            saveHistory(resultString)
                        } else {
                            binding.resultText.text = "Conversion not available"
                        }
                    } else {
                        binding.resultText.text = "API Error"
                    }
                }

                override fun onFailure(call: Call<CurrencyResponse>, t: Throwable) {
                    binding.resultText.text = "Check internet connection"
                }
            })
    }

    private fun swapCurrencies() {
        val fromPos = binding.fromCurrency.selectedItemPosition
        val toPos = binding.toCurrency.selectedItemPosition

        binding.fromCurrency.setSelection(toPos)
        binding.toCurrency.setSelection(fromPos)
    }

    private fun saveHistory(entry: String) {
        val sharedPrefs = requireActivity()
            .getSharedPreferences("conversion_history", Context.MODE_PRIVATE)

        val oldHistory = sharedPrefs.getString("history", "") ?: ""
        val newHistory = if (oldHistory.isEmpty()) entry else "$oldHistory\n$entry"

        sharedPrefs.edit().putString("history", newHistory).apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}