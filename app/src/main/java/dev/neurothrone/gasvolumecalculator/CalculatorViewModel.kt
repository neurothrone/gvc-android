package dev.neurothrone.gasvolumecalculator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.math.PI
import kotlin.math.pow

private const val GAS_PRESSURE = 1013.0
private const val CURRENT_GAS_VOLUME_KEY = "CURRENT_GAS_VOLUME_KEY"

// TODO: make updating for JBro eazier
// TODO: Create web api to consume by android, ios & web service

class CalculatorViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    var currentGasVolume: Double
        get() = savedStateHandle.get(CURRENT_GAS_VOLUME_KEY) ?: 0.0
        private set(value) = savedStateHandle.set(CURRENT_GAS_VOLUME_KEY, value)

    // dn values is expected to be in mm, first convert to meters then divide by 2 to get radius
    fun getPipeInnerRadius(dn: Int): Double = (dn / 1000.0) / 2.0

    fun calculateGasVolume(radius: Double, length: Double, pressure: Double) {
        currentGasVolume = (PI * radius.pow(2)) * length * ((pressure + GAS_PRESSURE) / GAS_PRESSURE)
    }
}