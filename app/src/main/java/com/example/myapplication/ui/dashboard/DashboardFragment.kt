package com.example.myapplication.ui.dashboard

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentDashboardBinding
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.materialspinner.MaterialSpinner
import java.text.SimpleDateFormat
import android.app.TimePickerDialog
import java.util.*
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import java.math.RoundingMode
import java.text.DecimalFormat


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    //Varibles
    private lateinit var editTextDate: EditText
    private lateinit var startTime: EditText
    private lateinit var endTime: EditText
    private lateinit var payHour: EditText
    private val calendar = Calendar.getInstance()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        editTextDate = binding.editTextDate
        editTextDate.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                showDatePicker()
            }
            true
        }
        editTextDate.isFocusable = false

        //Setting the Start Time for the job!
        startTime = binding.startTime
        startTime.setOnClickListener {
            showTimePicker(true)
        }
        startTime.isFocusable = false

        endTime = binding.endTime
        endTime.setOnClickListener {
            showTimePicker(false)
        }
        startTime.isFocusable = false

        payHour = binding.payHour
        payHour.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // User pressed "Enter" key or clicked "Done" button on the soft keyboard
                roundDecimalValue()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        payHour.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                payHour.text.clear()
            } else {
                roundDecimalValue()
            }
        }

        //Setting the Frequency of the Schedule
        val spinner = binding.multiSpinner as MaterialSpinner
        spinner.setItems("Weekly", "Bi-Weekly")
        spinner.setOnItemSelectedListener { view, position, id, item ->
            Snackbar.make(
                view,
                "Clicked $item",
                Snackbar.LENGTH_LONG
            ).show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //For the Ammount Rounding
    private fun roundDecimalValue() {
        val inputValue = payHour.text.toString().toDoubleOrNull()
        if (inputValue != null) {
            val roundedValue = roundToTwoDecimalPlaces(inputValue)
            payHour.setText("$" + roundedValue.toString() + " per hour")
        }
    }
    private fun roundToTwoDecimalPlaces(value: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP
        return df.format(value).toDouble()
    }

    private fun showTimePicker(isStart: Boolean) {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                // Handle the selected time here
                val selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                if (isStart) {startTime.setText(selectedTime)} else {
                    endTime.setText(selectedTime)
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Set this to true for 24-hour format
        )

        timePickerDialog.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = formatDate(selectedYear, selectedMonth, selectedDayOfMonth)
                editTextDate.setText(selectedDate)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}