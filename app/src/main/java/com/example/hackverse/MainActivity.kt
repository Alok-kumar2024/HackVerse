package com.example.hackverse

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private var sendUserID : String = ""

    private lateinit var  drawerLayout: DrawerLayout
    private lateinit var navigation : NavigationView
    private lateinit var database : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //finding navigation view...
        navigation = findViewById(R.id.navigation_view)

        //fnding first header view in navigation view of acivity main, first navigation view by 0
        val headerfile = navigation.getHeaderView(0)



        drawerLayout = findViewById(R.id.drawer_layout)

//        fun replaceFragment(fragment : Fragment, title : String) {
//
//            supportFragmentManager.beginTransaction().replace(R.id.Fragment_Container_Main,fragment)
//                .commit()
//            drawerLayout.closeDrawer(GravityCompat.START)
//
//            setTitle(title)
//
//
//        }

        if(savedInstanceState == null)
        {
            replaceFragment(HomeFragment(),"Home")
            navigation.setCheckedItem(R.id.home)
        }


        val str1 : String = "UserID -> "
        val str2 : String = "UserName -> "
        val str3 : String = "UserEmail -> "


//        val getshared = getSharedPreferences("MyUsersUserID", MODE_PRIVATE)
//       // val checkRegister = getshared.getString("CheckRegister",null).toString()
//        val UserID =getshared.getString("CodersUserID",null).toString()

        val UserID = intent.getStringExtra("RegisterUserID") ?: "Not_Got"


       // val getToActivityMainFromLogin = getSharedPreferences("SourceLogin", MODE_PRIVATE)


       // val checkLogin = getToActivityMainFromLogin.getString("CheckLogin",null).toString()

        val checkLogin = intent.getStringExtra("ShareLoginToMain") ?: "Unknow"
        Log.d("CheckLogin","The value of CheckLogin is $checkLogin")

        val checkRegister = intent.getStringExtra("ShareRegisterToMain") ?: "Unknow"
        Log.d("CheckRegister","The value of CheckRegister is $checkRegister")

        val getSharedLogin = getSharedPreferences("ShareLogin",MODE_PRIVATE)
//        val checkLogin = getSharedLogin.getString("CheckLogin",null).toString()
        val loginUserId =getSharedLogin.getString("LoginUserID",null) ?: "N/A"
        val loginUserName =getSharedLogin.getString("LoginUserName",null) ?: "N/A"
        val loginEmailID =getSharedLogin.getString("LoginEmailID",null) ?: "N/A"

        Log.d(
            "CheckingDataFromLogin",
            "Username: $loginUserName , UserEmail: $loginEmailID , UserID: $loginUserId - Data got from login ")


        val menuButtonOut: ImageButton = findViewById<ImageButton>(R.id.menu_button)

        val menuButonIn : ImageButton = headerfile.findViewById(R.id.menu_button_In)

        val HeaderUser : TextView = headerfile.findViewById(R.id.headerUser)
        val HeaderEmail : TextView = headerfile.findViewById(R.id.headerEmail)
        val HeaderUserID : TextView = headerfile.findViewById(R.id.headerUserID)

        database = FirebaseDatabase.getInstance().getReference("USERS")

        if(UserID.isEmpty() || loginUserId.isEmpty() ) {

            HeaderUser.text = "N/A"
            HeaderEmail.text = "N/A"
            HeaderUserID.text = "Not found"

        }else {
            if(UserID !="Not_Got"){
            database.child(UserID).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val Username = snapshot.child("name").value.toString()
                    val EmailId = snapshot.child("email").value.toString()

                    Log.d(
                        "CheckingDataFromRegister",
                        "Username: $Username , UserEmail: $EmailId , UserID: $UserID - Data got from Register..."
                    )

                    sendUserID = UserID


//                    if (checkLogin =="SourceLogin") {
//                        HeaderUser.text = "$str2$loginUserName"
//                        HeaderEmail.text = "$str3$loginEmailID"
//                        HeaderUserID.text = "$str1$loginUserId"
//
//                        Log.d(
//                            "DataFromLogin",
//                            "Username: $loginUserName , UserEmail: $loginEmailID , UserID: $loginUserId - Data got from login ..." +
//                                    "Username: $HeaderUser , UserEmail: $HeaderEmail , UserID: $HeaderUserID after putting data from login...."
//                        )
//                    }
                    if (checkRegister == "SourceRegister") {

                        HeaderUser.text = "$str2$Username"
                        HeaderEmail.text = "$str3$EmailId"

                        HeaderUserID.text = "$str1$UserID"

                        Log.d(
                            "DataFromRegister",
                            "Username: $Username , UserEmail: $EmailId , UserID: $UserID - Data got from Register..." +
                                    "Username: $HeaderUser , UserEmail: $HeaderEmail , UserID: $HeaderUserID after putting data from login...."
                        )
                    } else {
                        HeaderUser.text = "Not found_Register"
                        HeaderEmail.text = "Not Found_Register"

                        HeaderUserID.text = "Not Found_Register"
                    }


//                HeaderUser.text = "$str2$Username"
//                HeaderEmail.text = "$str3$EmailId"
//
//                HeaderUserID.text = "$str1$UserID"

                    Log.d("FetchDataFromRealTime", "SnapShot ${snapshot.value}")
                    Log.e("FetchDataFromRealTimeError", "Error ${snapshot.value}")
                } else {

//                    if (checkLogin =="SourceLogin") {
//                        HeaderUser.text = "$str2$loginUserName"
//                        HeaderEmail.text = "$str3$loginEmailID"
//                        HeaderUserID.text = "$str1$loginUserId"
//
//                        Log.d(
//                            "DataFromLogin",
//                            "Username: $loginUserName , UserEmail: $loginEmailID , UserID: $loginUserId - Data got from login ..." +
//                                    "Username: $HeaderUser , UserEmail: $HeaderEmail , UserID: $HeaderUserID after putting data from login...."
//                        )
//                    }else {
//
//                        HeaderUser.text = "N/A_Login"
//                        HeaderEmail.text = "N/A_Login"
//                        HeaderUserID.text = "N/A_Login"
//                        Log.e("FetchDataFromRealTimeError", "Error ${snapshot.value}")
//                    }

                    Log.e("FetchDatafromRealtime_register", "Error ${snapshot.value}")

                }
            }
        }else{
            database.child(loginUserId).get().addOnSuccessListener { snapshot ->
                if(snapshot.exists())
                {
                    val loginUsername2 = snapshot.child("name").value.toString()
                    val loginEmailId2 = snapshot.child("email").value.toString()

                    if (checkLogin =="SourceLogin") {
                        HeaderUser.text = "$str2$loginUsername2"
                        HeaderEmail.text = "$str3$loginEmailId2"
                        HeaderUserID.text = "$str1$loginUserId"

                        Log.d(
                            "DataFromLogin",
                            "Username: $loginUserName , UserEmail: $loginEmailID , UserID: $loginUserId - Data got from login ..." +
                                    "Username: $HeaderUser , UserEmail: $HeaderEmail , UserID: $HeaderUserID after putting data from login...."
                        )

                        sendUserID = loginUserId
                    }else {

                        HeaderUser.text = "N/A_Login"
                        HeaderEmail.text = "N/A_Login"
                        HeaderUserID.text = "N/A_Login"
                        Log.e("FetchDataFromRealTimeError", "Error ${snapshot.value}")
                    }

                }
            }
        }




        }


        val shareToProfile =getSharedPreferences("SendToProfile", MODE_PRIVATE)
        val editorProfile = shareToProfile.edit()
        editorProfile.putString("SendUserIDToProfile",HeaderUserID.text.toString()).apply()



        menuButtonOut.setOnClickListener {

//            Toast.makeText(this," checklogin is$checkLogin",Toast.LENGTH_SHORT).show()

            if(drawerLayout.isDrawerOpen(GravityCompat.START))
            {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            else
            {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        menuButonIn.setOnClickListener {

//            Toast.makeText(this," checkRegister is$checkRegister",Toast.LENGTH_SHORT).show()

            if(drawerLayout.isDrawerOpen((GravityCompat.START)))
            {
                drawerLayout.closeDrawer((GravityCompat.START))


            }
            else
            {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }


        navigation.setNavigationItemSelectedListener { menuitem ->

            menuitem.isChecked = true

            when(menuitem.itemId)
            {
                R.id.home -> replaceFragment(HomeFragment(),menuitem.title.toString())
                R.id.favourite -> replaceFragment(FavouriteFragment(),menuitem.title.toString())

                R.id.my_profile -> replaceFragment(ProfileFragment(),menuitem.title.toString())
                R.id.friends -> replaceFragment(FriendsFragment(),menuitem.title.toString())


            }
            true
        }



    }

    private fun replaceFragment(fragment : Fragment, title : String) {

            val sendid = sendUserID

        val bundle = Bundle()
        bundle.putString("UserId",sendid)
        fragment.arguments = bundle

            supportFragmentManager.beginTransaction().replace(R.id.Fragment_Container_Main,fragment)
                .commit()


            drawerLayout.closeDrawer(GravityCompat.START)

            setTitle(title)


        }


}