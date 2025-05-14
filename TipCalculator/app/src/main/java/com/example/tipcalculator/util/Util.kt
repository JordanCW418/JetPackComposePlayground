package com.example.tipcalculator.util

import android.util.Log


fun calculateTotalTip(totalBill: Double, tipPercentage: Int): Double {
    Log.d("JordanLog", "${totalBill} $tipPercentage")
    return if(totalBill > 1 &&
        totalBill.toString().isNotEmpty()) {
        (totalBill * tipPercentage) / 100
    } else 0.0
}

fun calculateTotalPerPerson(
    totalBill: Double,
    splitBy: Int,
    tipPercentage: Int): Double {
    val bill = calculateTotalTip(totalBill, tipPercentage) + totalBill

    return bill / splitBy
}