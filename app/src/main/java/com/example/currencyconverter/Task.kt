package com.example.currencyconverter

data class Task(
    val id: String,
    var title: String,
    var isCompleted: Boolean = false,
    var date: String,
    var time: String,
    var priority: String
)