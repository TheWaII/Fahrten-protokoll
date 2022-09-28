package com.example.protokoll

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class MainActivity : AppCompatActivity() {

    val SHARED_PREFS = "sharedPrefs"

    private val myCalendar: Calendar = Calendar.getInstance()
    private var editText: EditText? = null

    private var proList = ArrayList<ProtokollItem>()

    private var proItems = ProtokollItem()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.dateValue)

        val x = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE)



        loadData()
        populateList()
        populateSpinner()
        datePicker()

    }

    fun add(view: View) {

        val date: EditText = findViewById(R.id.dateValue)
        val kmInit: EditText = findViewById(R.id.kmInitialValue)
        val kmEnd: EditText = findViewById(R.id.kmEndValue)
        val kfz: EditText = findViewById(R.id.kfzValue)
        val timeOfDay: Spinner = findViewById(R.id.timeOfDayValue)
        val route: EditText = findViewById(R.id.routeValue)
        val condition: EditText = findViewById(R.id.conditionValue)


        proItems.date = date.text.toString()
        proItems.kmInit = kmInit.text.toString().toInt()
        proItems.kmEnd = kmEnd.text.toString().toInt()
        proItems.kfz = kfz.text.toString()
        proItems.timeOfDay = timeOfDay.selectedItem.toString()
        proItems.route = route.text.toString()
        proItems.condition = condition.text.toString()

        proList.add(proItems)

        saveData()
        populateList()
    }

    private fun populateList() {
        val arrayAdapter: ArrayAdapter<*>

        val myListview = findViewById<ListView>(R.id.lstViewProtocol)

        var proStringList = ArrayList<String>()


        for (protokollItem in proList) {
            proStringList.add(
                protokollItem.date + " -- "
                        + protokollItem.kmInit + " km -- "
                        + protokollItem.kmEnd + " km -- "
                        + protokollItem.kfz + " -- "
                        + protokollItem.timeOfDay + " -- "
                        + protokollItem.route + " -- "
                        + protokollItem.condition
            )
        }


        arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, proStringList
        )

        myListview.adapter = arrayAdapter

    }

    private fun populateSpinner() {
        val spinner: Spinner = findViewById(R.id.timeOfDayValue)
        ArrayAdapter.createFromResource(
            this,
            R.array.timeOfDay,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun datePicker() {
        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, day)
                updateLabel()
            }
        editText?.setOnClickListener(View.OnClickListener {
            DatePickerDialog(
                this@MainActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        })
        updateLabel()
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.GERMAN)
        editText?.setText(dateFormat.format(myCalendar.time))
    }

    private fun saveData() {
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(proList)
        editor.putString("taskList", json)
        editor.apply()
    }

    private fun loadData() {

        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("taskList", gson.toJson(proList))

        val type = object : TypeToken<ArrayList<ProtokollItem>>() {}.type

        proList = gson.fromJson(json, type)
    }
}