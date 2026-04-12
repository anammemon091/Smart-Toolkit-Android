package com.example.currencyconverter

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView


class HomeFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyCard = view.findViewById<MaterialCardView>(R.id.currencyConverter)
        val unitCard = view.findViewById<MaterialCardView>(R.id.unitCard)
        val calculatorCard = view.findViewById<MaterialCardView>(R.id.calculatorCard)

        currencyCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ConvertFragment())
                .commit()
        }

        unitCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, UnitFragment())
                .commit()
        }

        calculatorCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CalculatorFragment())
                .commit()
        }
    }
}