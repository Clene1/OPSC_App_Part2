package com.example.timeflow_opsc_poe_part_2

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.integrity.internal.s
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ScheduleFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private  lateinit var rootNode : FirebaseDatabase
    private  lateinit var timeEntriesReference : DatabaseReference
    val currentUser = CurrentUser.userID
    private  lateinit var projectReference : DatabaseReference
    private val calender = Calendar.getInstance()
    private val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.UK)
    var currentDate = formatter.format(Date())
    val listData : MutableList<ParentData> = ArrayList()
    var parentData = ArrayList<String>()
    var childDataData = ArrayList<ChildData>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnAddEntry = view.findViewById<Button>(R.id.btnAddEntry)
        btnAddEntry.setOnClickListener {
            val intent = Intent(context, Select_Option::class.java)
            startActivity(intent)
        }
        // prior values
        rootNode = FirebaseDatabase.getInstance()
        val context = context as MainActivity
        var selectedDate = view.findViewById<TextView>(R.id.txtSelectedDate)
        selectedDate.text = currentDate

        var btnchangeDate = view.findViewById<ImageButton>(R.id.btnSelectDate)
        btnchangeDate.setOnClickListener {

            // read projects
            readData(object : FirebaseCallback {
                override fun onCallback(prjList: ArrayList<String>) {
                    Log.w("thisthedate", prjList.toString())
                }
            })

            DatePickerDialog(
                context,
                object : DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                        calender.set(year, month, dayOfMonth)
                        displayFormatDate(calender.timeInMillis)

                        // now retrieve data
                        retrieveTimesheets()
                        Display()
                        updateDisplay()
                    }
                },
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

    }


    fun retrieveTimesheets(){
        Log.w("thisthedate", currentDate)
        childDataData.clear()
        for (project in UserProjects.projectsList) {
            timeEntriesReference = rootNode.getReference("timeEntries/$currentUser/$currentDate/$project")
            timeEntriesReference.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot){
                    for(snapshot1 in snapshot.children){
                        val dc2 = snapshot1.getValue(TimesheetEntry::class.java)
                        
                        childDataData.add(ChildData("${dc2!!.startTime} - ${dc2!!.endTime}", null))
                    }
                    Log.w("thisthedate", childDataData.toString())
                }
                override fun onCancelled(error: DatabaseError){
                }
            })
        }
    }


    fun Display(){
        //val childDataData1: MutableList<ChildData> = mutableListOf(ChildData("Anathapur", null),ChildData("Chittoor", null))
        //val childDataData2: MutableList<ChildData> = mutableListOf(ChildData("Rajanna Sircilla", null), ChildData("Karimnagar", null))
        //val childDataData3: MutableList<ChildData> = mutableListOf(ChildData("Chennai", null), ChildData("Erode", null))

        //val parentObj1 = ParentData(parentTitle = parentData[0], subList = childDataData1)
        //val parentObj2 = ParentData(parentTitle = parentData[1], subList = childDataData2)
        //val parentObj3 = ParentData(parentTitle = parentData[2])
        //val parentObj4 = ParentData(parentTitle = parentData[1], subList = childDataData3)

        //listData.add(parentObj1)
        //listData.add(parentObj2)
        //listData.add(parentObj3)
        //listData.add(parentObj4)
        updateDisplay()
    }

    fun updateDisplay(){
        val context = context as MainActivity
        val RecyclerView = view?.findViewById<RecyclerView>(R.id.Recycler)
        RecyclerView?.layoutManager = LinearLayoutManager(context)
        RecyclerView?.adapter = RecycleAdapter(context,listData)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calender.set(year, month, dayOfMonth)
        displayFormatDate(calender.timeInMillis)
    }

    private fun displayFormatDate(timestamp: Long) {
        var selectedDate = view?.findViewById<TextView>(R.id.txtSelectedDate)
        if (selectedDate != null) {
            selectedDate.text = formatter.format(timestamp)
            currentDate = formatter.format(timestamp)
        }
    }

    fun byteArrayToBitmap(data: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

    fun readData(firebaseCallback:FirebaseCallback){
        projectReference = rootNode.getReference("projects/${CurrentUser.userID}")
        projectReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    if (dc2!!.highPriority) {
                        parentData.add(dc2!!.name)
                    }
                }
                for(snapshot1 in snapshot.children){
                    val dc2 = snapshot1.getValue(Project::class.java)
                    if (!dc2!!.highPriority) {
                        parentData.add(dc2!!.name)
                    }
                }
                firebaseCallback.onCallback(parentData)
            }
            override fun onCancelled(error: DatabaseError){
            }
        })
    }

    interface FirebaseCallback{
        fun onCallback(prjList:ArrayList<String>)
    }
    interface FirebaseCallbackLoop{
        fun onCallback(prjList:ArrayList<String>)
    }

}