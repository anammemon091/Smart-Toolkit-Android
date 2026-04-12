package com.example.currencyconverter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import com.bumptech.glide.Glide

class CurrencyAdapter(
    context: Context,
    private val items: List<CurrencyItem>
) : ArrayAdapter<CurrencyItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_currency, parent, false
        )

        val currency = items[position]
        val flagImage = view.findViewById<ImageView>(R.id.flagImage)
        val codeText = view.findViewById<TextView>(R.id.codeText)

        codeText.text = currency.code
        Glide.with(context).load(currency.flagUrl).into(flagImage)

        return view
    }
}