package com.example.hackverse

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.hackverse.databinding.ActivityCreateHackathonBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class CreateHackathon : AppCompatActivity() {
    private lateinit var binding : ActivityCreateHackathonBinding
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateHackathonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val CurrentUerID = intent.getStringExtra("CurrentUserID").toString()
        Log.d("CurrentUserIDCreateHackathon","UserId i fom My hackathon in Create Hackathon is $CurrentUerID")

        database = FirebaseDatabase.getInstance().getReference("HACKATHON")
        val DatabaseUsers = FirebaseDatabase.getInstance().getReference("USERS")

        binding.buttonCancelHackathon.setOnClickListener {
            finish()
        }

        val calendar = Calendar.getInstance()

        binding.ButtonSelectStartDate.setOnClickListener {

            DatePickerDialog(this,{_,year,month,dayOfMonth ->
                binding.TextViewSelectStartDate.text = "$year-${month+1}-$dayOfMonth"
            },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.ButtonSelectStartTime.setOnClickListener {

            TimePickerDialog(this,{_, hour, minute ->
                binding.TextViewSelectStartTime.text = "$hour:$minute"
            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE) , true ).show()
        }

        binding.ButtonSelectEndDate.setOnClickListener {

            DatePickerDialog(this ,{_, year, month, dayOfMonth ->
                binding.TextViewSelectEndDate.text = "$year-${month+1}-$dayOfMonth"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.ButtonSelectEndTime.setOnClickListener {

            TimePickerDialog(this, {_, hour , minute ->
                binding.TextViewSelectEndTime.text = "$hour:$minute"
            },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }


        binding.buttonCreateHackathon.setOnClickListener {

            val EventName = binding.EditTextEventName.text.toString()
            val bannerURL = binding.EditTextBannerURL.text.toString()
            val HostName = binding.EditTextHostName.text.toString()
            val prize = binding.EditTextPrize.text.toString()
            val description = binding.EditTextDescription.text.toString()

            val startDate = binding.TextViewSelectStartDate.text.toString()
            val startTime = binding.TextViewSelectStartTime.text.toString()
            val endDate = binding.TextViewSelectEndDate.text.toString()
            val endTime = binding.TextViewSelectEndTime.text.toString()

            Log.d("Time","Start -> Date - $startDate , Time - $startTime" +
                    "End -> Date - $endDate , Time - $endTime")


            fun generateHackathonKey(storeName : String , callback : (String) -> Unit){

//                val storeName =

                fun createKey() : String{
                    val suffix = (1000..9999).random()
                    val symbol = listOf('@', '%', '!', '&')
                    val randomsymbol = symbol.random()
                    return "$storeName${randomsymbol}$suffix"
                }
                val Check = createKey()

                database.orderByKey().equalTo(Check).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists())
                        {
                            generateHackathonKey(storeName,callback)
                        }else{
                            callback(Check)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }

//            fun getCurrentDateTime(): String {
//                // Get the current date and time
//                val currentDateTime = LocalDateTime.now()
//
//                // Format the date and time in a readable format (e.g., yyyy-MM-dd HH:mm:ss)
//                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//                return currentDateTime.format(formatter)
//            }

            @SuppressLint("SimpleDateFormat")
            fun getCurrentDateTime(): String {
                val currentDate = Date() // Get the current date and time
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Define your date format
                return formatter.format(currentDate) // Format the date into a string
            }

            generateHackathonKey(EventName){UniqueKEY ->
                val EventID = UniqueKEY
                Log.d("EVENTID","The Hackathon Id is $EventID")

                val CreatedTime = getCurrentDateTime()
                Log.d("CREATEDTIME","The Created Time of Event is $CreatedTime")

            if (EventName.isEmpty() || HostName.isEmpty() || prize.isEmpty() || startDate.isEmpty() || startTime.isEmpty()
                || endDate.isEmpty() || endTime.isEmpty())
            {
                Toast.makeText(this,"Star Fields are compulsory to Fill.",Toast.LENGTH_SHORT).show()
            }else{
                if (EventID.isEmpty())
                {
                    Toast.makeText(this,"ERROR : Couldn't Create the key for hackathon.",Toast.LENGTH_SHORT).show()
                }else{
                    val EventHackathon = hackathons_info(
                        bannerUrl = bannerURL ,
                        EventName = EventName ,
                        HostedBy = HostName ,
                        Prize = prize ,
                        CreatedBy = CurrentUerID,
                        status = "Active" ,
                        description = description ,
                        Time = time(
                            ActiveDate = startDate ,
                            ActiveTime = startTime ,
                            CreatedDateTime = CreatedTime ,
                            ClosedDate = endDate ,
                            CloseTime = endTime
                            )

                        )
                    database.child(EventID).setValue(EventHackathon).addOnSuccessListener {
                        Toast.makeText(this,"SuccessFully Created Event",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this,"ERROR : Couldn't Created Event",Toast.LENGTH_SHORT).show()
                    }
                    val change = mapOf(
                        "EventStatus" to "Created"
                    )
                    DatabaseUsers.child(CurrentUerID).child("hackathons").child(EventID).updateChildren(change)
                        .addOnSuccessListener {
                            Log.d("EventStatus","SuccessFully Changed Status of Current user for eventId ")
                        }.addOnFailureListener {
                            Log.d("EventStatus","Error: Couldn't Chang Status of Current user for eventId ")
                        }

                    finish()



                }
            }




            }



        }

    }
}