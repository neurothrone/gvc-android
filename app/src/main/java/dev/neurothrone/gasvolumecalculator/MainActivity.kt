package dev.neurothrone.gasvolumecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import dev.neurothrone.gasvolumecalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()

    // TODO: npsTable: Map, nps: dn
    private val npsTable = mapOf(
        "3⁄4" to 20,
        "1" to 25,
        "1 1⁄4" to 32,
        "1 1⁄2" to 40,
        "2" to 50,
        "2 1⁄2" to 65,
        "3" to 80,
        "3 1⁄2" to 90,
        "4" to 100,
        "4 1⁄2" to 115,
        "5" to 125,
        "6" to 150,
        "8" to 200,
        "10" to 250,
        "12" to 300,
        "14" to 350,
        "16" to 400
    )
    private val dnArray = npsTable.values.toIntArray()
    private var selectedDN: Int = 0

    private val isButtonEnabled: Boolean
        get() = isLengthInputValid && isPressureInputValid

    private val isLengthInputValid: Boolean
        get() {
            val lengthInput = binding.lengthEditText.text.toString()

            if (lengthInput.isEmpty()) {
                return false
            }

            // Covers edge cases such as a dot
            lengthInput.toDoubleOrNull() ?: return false

            return true
        }

    private val isPressureSelectionCustom: Boolean
        get()  = !binding.pressureSpinner.selectedItem.toString().isDigitsOnly()

    private val isPressureInputValid: Boolean
        get() {
            if (isPressureSelectionCustom) {
                val pressureTextInput = binding.pressureEditText.text.toString()
                pressureTextInput.toDoubleOrNull() ?: return false
            }

            return true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initGUI()
    }

    private fun initGUI() {
        binding.npsSpinner.onItemSelectedListener = this
        binding.pressureSpinner.onItemSelectedListener = this
        populateSpinnerItems(binding.npsSpinner, R.array.radius_array)
        populateSpinnerItems(binding.pressureSpinner, R.array.pressure_array)
        registerListeners()
        binding.resultOutputText.text = viewModel.currentGasVolume.toString()
    }

    private fun calculate() {
        val radius = viewModel.getPipeInnerRadius(dnArray[selectedDN])
        val length = binding.lengthEditText.text.toString().toDouble()
        val pressure = if (!isPressureSelectionCustom) {
            val pressureItem = binding.pressureSpinner.selectedItem.toString()
            pressureItem.toDouble()
        } else {
            val pressureTextInput = binding.pressureEditText.text.toString()
            pressureTextInput.toDouble()
        }
        viewModel.calculateGasVolume(radius, length, pressure)
        binding.resultOutputText.text = String.format("%.3f", viewModel.currentGasVolume)
    }

    private fun validateInput() {
        binding.calculateButton.isEnabled = isButtonEnabled
    }

    private fun populateSpinnerItems(spinner: Spinner, textArrayResId: Int) {
        ArrayAdapter.createFromResource(
            this,
            textArrayResId,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    private fun registerListeners() {
        binding.calculateButton.setOnClickListener {
            calculate()
        }

        binding.lengthEditText.addTextChangedListener {
            validateInput()
        }

        binding.pressureEditText.addTextChangedListener {
            validateInput()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val spinnerView = parent ?: return

        when (spinnerView.id) {
            R.id.nps_spinner -> {
                selectedDN = position
            }
            R.id.pressure_spinner -> {
                val item = spinnerView.getItemAtPosition(position).toString()
                onPressureSpinnerItemSelected(item)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun onPressureSpinnerItemSelected(item: String) {
        binding.pressureEditText.visibility = if (item.isDigitsOnly())
            View.GONE
        else
            View.VISIBLE

        validateInput()
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
