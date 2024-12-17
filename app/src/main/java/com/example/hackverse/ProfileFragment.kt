package com.example.hackverse

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var database : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val userid = view.findViewById<TextView>(R.id.UserId)
        val useridShow = view.findViewById<TextView>(R.id.UserID_show)

        val username = view.findViewById<TextView>(R.id.Username)
        val usernameShow = view.findViewById<TextView>(R.id.Username_show)

        val fullname = view.findViewById<TextView>(R.id.fullname)
        val fullnameShow = view.findViewById<TextView>(R.id.fullname_show)

        val emailid = view.findViewById<TextView>(R.id.emailid)
        val emailidShow = view.findViewById<TextView>(R.id.email_show)


        val getuserid = arguments?.getString("UserId").toString()
        Log.d("GetUserID","UserId i got from main header is $getuserid")

        database = FirebaseDatabase.getInstance().getReference("USERS")

        if(getuserid.isEmpty())
        {
            useridShow.text = "Not Found"
            usernameShow.text = "Unable To Fetch"
            fullnameShow.text = "Unable To Fetch"
            emailidShow.text = "Unable To Fetch"

        }
        else{

            database.child(getuserid).get().addOnSuccessListener { snapshot ->

                if(snapshot.exists())
                {
                    useridShow.text = getuserid
                    usernameShow.text = snapshot.child("username").value.toString()
                    fullnameShow.text = snapshot.child("name").value.toString()
                    emailidShow.text = snapshot.child("email").value.toString()

                    Log.d("Fetched Data","useridshow after fetching $useridShow" +
                            "usernameshow after fetching $usernameShow" +
                            "full name after fetching $fullnameShow" +
                            "emailid after fetching $emailidShow")


                }else{


                    useridShow.text = "Does not Exists"
                    usernameShow.text = "Does not Exists"
                    fullnameShow.text = "Does not Exists"
                    emailidShow.text = "Does not Exists"

                    Log.e("Not exists","Error of not exsiting is ${snapshot.value}")

                }



            }
        }





        return view
    }



}