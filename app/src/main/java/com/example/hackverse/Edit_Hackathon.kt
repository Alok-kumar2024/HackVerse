package com.example.hackverse

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hackverse.databinding.ActivityEditHackathonBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Edit_Hackathon : AppCompatActivity() {
    private lateinit var binding : ActivityEditHackathonBinding
    private lateinit var database : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditHackathonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonCancelEditHackathon.setOnClickListener {
            finish()
        }

        val HackathonID = intent.getStringExtra("HackathonID") ?: "Not Got"

        val CurrentUser = intent.getStringExtra("currentuserid") ?: "Not Got"
        database = FirebaseDatabase.getInstance().getReference("HACKATHON")

        val calendar = Calendar.getInstance()

        binding.ButtonSelectStartDate.setOnClickListener {

            DatePickerDialog(this,{_,year,month,dayOfMonth ->
                binding.TextViewShowSelectStartDate.text = "$year-${month+1}-$dayOfMonth"
            },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.ButtonSelectStartTime.setOnClickListener {

            TimePickerDialog(this,{_, hour, minute ->
                binding.TextViewSelectShowStartTime.text = "$hour:$minute"
            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE) , true ).show()
        }

        binding.ButtonSelectEndDate.setOnClickListener {

            DatePickerDialog(this ,{_, year, month, dayOfMonth ->
                binding.TextViewSelectShowEndDate.text = "$year-${month+1}-$dayOfMonth"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.ButtonSelectEndTime.setOnClickListener {

            Log.d("ClickedConfirm","YES")

            TimePickerDialog(this, {_, hour , minute ->
                binding.TextViewSelectShowEndTime.text = "$hour:$minute"
            },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }

        database.child(HackathonID).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                {

                    binding.EditTextShowHostName.setText(snapshot.child("hostedBy").value.toString())
                    binding.EditTextShowPrize.setText(snapshot.child("prize").value.toString())
                    binding.EditTextShowDescription.setText(snapshot.child("description").value.toString())
                    binding.EditTextShowBannerURL.setText(snapshot.child("bannerUrl").value.toString())
                    binding.EditTextShowEventName.setText(snapshot.child("eventName").value.toString())
                    binding.TextViewShowSelectStartDate.setText(snapshot.child("time").child("activeDate").value.toString())
                    binding.TextViewSelectShowStartTime.setText(snapshot.child("time").child("activeTime").value.toString())
                    binding.TextViewSelectShowEndDate.setText(snapshot.child("time").child("closedDate").value.toString())
                    binding.TextViewSelectShowEndTime.setText(snapshot.child("time").child("closeTime").value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        @SuppressLint("SimpleDateFormat")
        fun getCurrentDateTime(): String {
            val currentDate = Date() // Get the current date and time
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Define your date format
            return formatter.format(currentDate) // Format the date into a string
        }
        val CurrentTime = getCurrentDateTime()

//        binding.buttonconfirmEditHackathon.setOnClickListener {
//            var check = true
//
//            if (binding.EditTextShowPrize.text.isEmpty() || binding.EditTextShowHostName.text.isEmpty()
//                || binding.EditTextShowPrize.text.isEmpty() || binding.EditTextShowDescription.text.isEmpty()
//                || binding.EditTextShowEventName.text.isEmpty())
//            {
//                Toast.makeText(this,"Star Fields Cannot Be Empty..",Toast.LENGTH_SHORT).show()
//            }else {
//                val startTime =
//                    "${binding.TextViewShowSelectStartDate.text} ${binding.TextViewSelectShowStartTime.text}"
//
//                val dateFormat = SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault())
//                val startDate = dateFormat.parse(startTime)
//                val startTimestamp = startDate?.time ?: 0L
//
//                val endTime =
//                    "${binding.TextViewSelectShowEndDate.text} ${binding.TextViewSelectShowEndTime.text}"
//
//                val endDate = dateFormat.parse(endTime)
//                val endTimestamp = endDate?.time ?: 0L
//                try {
//
//
//                    if ( endTimestamp < System.currentTimeMillis()) {
//                        Toast.makeText(
//                            this,
//                            "Start/End Date-Time  can't be Less then Current Time...",
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        check = false
//
//                    }
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                if(check){
//
//                        val update_details = mapOf(
//                            "bannerUrl" to binding.EditTextShowBannerURL.text,
//                            "hostedBy" to binding.EditTextShowHostName.text,
//                            "prize" to binding.EditTextShowPrize.text,
//                            "description" to binding.EditTextShowDescription.text,
//                            "eventName" to binding.EditTextShowEventName.text,
//
//                            )
//
//                        val update_Time = mapOf(
//                            "activeDate" to binding.TextViewShowSelectStartDate.text,
//                            "activeTime" to binding.TextViewSelectShowStartTime.text,
//                            "closedDate" to binding.TextViewSelectShowEndDate.text,
//                            "closeTime" to binding.TextViewSelectShowEndTime.text
//                        )
//
//
//                        database.child(HackathonID).child("time").updateChildren(update_Time)
//
//                            database.child(HackathonID).updateChildren(update_details).addOnSuccessListener {
//                                Toast.makeText(
//                                    this,
//                                    "Successfully Updated Hackathon Details..",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                if(System.currentTimeMillis() in startTimestamp..endTimestamp)
//                                {
//                                    val update_Status = mapOf(
//                                        "status" to "Active"
//                                    )
//                                    database.child(HackathonID).updateChildren(update_Status)
//                                }
//                                finish()
//                            }
//
//
//                    }
//
//            }
//
//
//        }

        binding.buttonconfirmEditHackathon.setOnClickListener {
            if (binding.EditTextShowPrize.text.isEmpty() || binding.EditTextShowHostName.text.isEmpty()
                || binding.EditTextShowDescription.text.isEmpty() || binding.EditTextShowEventName.text.isEmpty()) {

                Toast.makeText(this, "Star Fields Cannot Be Empty..", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val startDateText = binding.TextViewShowSelectStartDate.text.toString().trim()
            val startTimeText = binding.TextViewSelectShowStartTime.text.toString().trim()
            val endDateText = binding.TextViewSelectShowEndDate.text.toString().trim()
            val endTimeText = binding.TextViewSelectShowEndTime.text.toString().trim()

            val dateFormat = SimpleDateFormat("yyyy-M-d HH:mm", Locale.getDefault())

            try {
                val startDate = dateFormat.parse("$startDateText $startTimeText")
                val endDate = dateFormat.parse("$endDateText $endTimeText")

                val startTimestamp = startDate?.time ?: 0L
                val endTimestamp = endDate?.time ?: 0L

                // **Fix: Validate both start and end times**
                if (endTimestamp < System.currentTimeMillis()) {
                    Toast.makeText(
                        this,
                        "Start/End Date-Time can't be before the current time...",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener // **Fix: Stop execution**
                }
                if (endTimestamp < startTimestamp) {
                    Toast.makeText(this, "End time cannot be before start time!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                // **Firebase Updates**
                val updateDetails = mapOf(
                    "bannerUrl" to binding.EditTextShowBannerURL.text.toString(),
                    "hostedBy" to binding.EditTextShowHostName.text.toString(),
                    "prize" to binding.EditTextShowPrize.text.toString(),
                    "description" to binding.EditTextShowDescription.text.toString(),
                    "eventName" to binding.EditTextShowEventName.text.toString()
                )

                val updateTime = mapOf(
                    "activeDate" to startDateText,
                    "activeTime" to startTimeText,
                    "closedDate" to endDateText,
                    "closeTime" to endTimeText
                )

                // **Fix: Chain updates correctly**
                val updateDetailsTask = database.child(HackathonID).updateChildren(updateDetails)
                val updateTimeTask = database.child(HackathonID).child("time").updateChildren(updateTime)

                updateDetailsTask.continueWithTask { task1 ->
                    if (task1.isSuccessful) updateTimeTask
                    else throw task1.exception ?: Exception("Couldn't update event details")
                }.addOnCompleteListener { task2 ->
                    if (task2.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Successfully Updated Hackathon Details",
                            Toast.LENGTH_SHORT
                        ).show()

                        // **Fix: Update status if current time is within the event period**
                        Log.d("Value of Times","The Current time ${System.currentTimeMillis()} \n" +
                                "The start Time $startTimestamp. \n" +
                                "The End Time $endTimestamp.")
                        if (System.currentTimeMillis() in startTimestamp..endTimestamp) {
                            Log.d("Going InsideIF","YES")
                            val updateStatus = mapOf("status" to "Active")
                            database.child(HackathonID).updateChildren(updateStatus).addOnCompleteListener { task3 ->
                                if (task3.isSuccessful) {
                                    Log.d("Debug", "Event status updated to Active")
                                } else {
                                    Log.e("Debug", "Failed to update event status: ${task3.exception}")
                                }
                            }
                        }

                        finish() // **Fix: Finish activity only after updates succeed**
                    } else {
                        val errorMessage = task2.exception?.message ?: "Unknown error occurred."
                        Toast.makeText(
                            this,
                            "Couldn't update details: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Invalid Date-Time Format!", Toast.LENGTH_SHORT).show()
            }
        }




    }
}