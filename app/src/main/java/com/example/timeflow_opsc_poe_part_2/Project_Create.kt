package com.example.timeflow_opsc_poe_part_2

import android.os.Bundle
import android.text.Selection.setSelection
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Project_Create : AppCompatActivity() {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var projectReference : DatabaseReference
    var priorities = arrayOf("High", "Low")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_project_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val currentUser = CurrentUser.userID
        rootNode = FirebaseDatabase.getInstance()

        val spinnerID = findViewById<Spinner>(R.id.mySpinner)
        val arrayAdapt = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spinnerID.adapter = arrayAdapt
        val priority = false

        spinnerID?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (priorities[p2] == "false"){
                    priority == false
                }
                else{
                    priority == true
                }
                Toast.makeText(this@Project_Create, "item selected: ${priorities[p2]}" ,Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(this@Project_Create, "item selected: Nothing" ,Toast.LENGTH_SHORT).show()
            }
        }

        projectReference = rootNode.getReference("projects/$currentUser")
    }

    fun writeProject(name: String, priority: Boolean) {
        var myRef = projectReference.push()
        var key = myRef.key
        val project = Project( name, priority)
        if (key != null) {
            projectReference.child(key).setValue(project)
        }
    }
}