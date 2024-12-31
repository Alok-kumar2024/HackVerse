package com.example.hackverse

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private var sendUserID : String = ""


    private lateinit var firebase : FirebaseAuth
    private lateinit var  drawerLayout: DrawerLayout
    private lateinit var navigation : NavigationView
    private lateinit var database : DatabaseReference

    private lateinit var HeaderUser : TextView
    private lateinit var HeaderEmail : TextView
    private lateinit var HeaderUserID : TextView
    private lateinit var HeaderImageView : ImageView

    private lateinit var loginUserId : String
    private lateinit var loginUserName : String
    private lateinit var loginEmailID : String
    private lateinit var  checkLogin : String

    val str1 : String = "UserID -> "
    val str2 : String = "UserName -> "
    val str3 : String = "UserEmail -> "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val getLoggedInDetails = getSharedPreferences("LoggedIN", MODE_PRIVATE)

        val isLoggedInUser = getLoggedInDetails.getBoolean("isLoggedIn",false)
        Log.d("LoggedInBoolean","The Logged In value is $isLoggedInUser")

         checkLogin = getLoggedInDetails.getString("ShareLoginToMain",null) ?: "Unknow"
        Log.d("CheckLogin","The value of CheckLogin is $checkLogin")

        if (!isLoggedInUser)
        {
            //user is not logged in
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{

            //user is Logged in


            setContentView(R.layout.activity_main)
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


        firebase = FirebaseAuth.getInstance()

        if(savedInstanceState == null)
        {
            replaceFragment(HomeFragment(),"Home","Home")
            navigation.setCheckedItem(R.id.home)
        }



//        val getshared = getSharedPreferences("MyUsersUserID", MODE_PRIVATE)
//       // val checkRegister = getshared.getString("CheckRegister",null).toString()
//        val UserID =getshared.getString("CodersUserID",null).toString()

        val UserID = intent.getStringExtra("RegisterUserID") ?: "Not_Got"


       // val getToActivityMainFromLogin = getSharedPreferences("SourceLogin", MODE_PRIVATE)


       // val checkLogin = getToActivityMainFromLogin.getString("CheckLogin",null).toString()

//        val checkLogin = intent.getStringExtra("ShareLoginToMain") ?: "Unknow"
//        Log.d("CheckLogin","The value of CheckLogin is $checkLogin")

        val checkRegister = intent.getStringExtra("ShareRegisterToMain") ?: "Unknow"
        Log.d("CheckRegister","The value of CheckRegister is $checkRegister")

          val getSharedLogin = getSharedPreferences("ShareLogin",MODE_PRIVATE)
//        val checkLogin = getSharedLogin.getString("CheckLogin",null).toString()
//
//        isLoggedInUser = getSharedLogin.getBoolean("isLoggedIn",false)
//        Log.d("LoggedInBoolean","The Logged In value is $isLoggedInUser")


        loginUserId =getSharedLogin.getString("LoginUserID",null) ?: "N/A"
        loginUserName =getSharedLogin.getString("LoginUserName",null) ?: "N/A"
        loginEmailID =getSharedLogin.getString("LoginEmailID",null) ?: "N/A"

        Log.d(
            "CheckingDataFromLogin",
            "Username: $loginUserName , UserEmail: $loginEmailID , UserID: $loginUserId - Data got from login ")


        val menuButtonOut: ImageButton = findViewById<ImageButton>(R.id.menu_button)

        val menuButonIn : ImageButton = headerfile.findViewById(R.id.menu_button_In)

         HeaderUser  = headerfile.findViewById(R.id.headerUser)
         HeaderEmail = headerfile.findViewById(R.id.headerEmail)
         HeaderUserID = headerfile.findViewById(R.id.headerUserID)
         HeaderImageView = headerfile.findViewById(R.id.ProfileImageOfMain)

        database = FirebaseDatabase.getInstance().getReference("USERS")

        if(UserID.isEmpty() || loginUserId.isEmpty() ) {

            HeaderUser.text = "N/A"
            HeaderEmail.text = "N/A"
            HeaderUserID.text = "Not found"

        }else {
//            if(UserID !="Not_Got"){
//            database.child(UserID).get().addOnSuccessListener { snapshot ->
//                if (snapshot.exists()) {
//                    val Username = snapshot.child("name").value.toString()
//                    val EmailId = snapshot.child("email").value.toString()
//
//                    Log.d(
//                        "CheckingDataFromRegister",
//                        "Username: $Username , UserEmail: $EmailId , UserID: $UserID - Data got from Register..."
//                    )
//
//                    sendUserID = UserID
//
//
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
//                    if (checkRegister == "SourceRegister") {
//
//                        HeaderUser.text = "$str2$Username"
//                        HeaderEmail.text = "$str3$EmailId"
//
//                        HeaderUserID.text = "$str1$UserID"
//
//                        Log.d(
//                            "DataFromRegister",
//                            "Username: $Username , UserEmail: $EmailId , UserID: $UserID - Data got from Register..." +
//                                    "Username: $HeaderUser , UserEmail: $HeaderEmail , UserID: $HeaderUserID after putting data from login...."
//                        )
//                    } else {
//                        HeaderUser.text = "Not found_Register"
//                        HeaderEmail.text = "Not Found_Register"
//
//                        HeaderUserID.text = "Not Found_Register"
//                    }


//                HeaderUser.text = "$str2$Username"
//                HeaderEmail.text = "$str3$EmailId"
//
//                HeaderUserID.text = "$str1$UserID"

//                    Log.d("FetchDataFromRealTime", "SnapShot ${snapshot.value}")
//                    Log.e("FetchDataFromRealTimeError", "Error ${snapshot.value}")
//                } else {

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
////                    }
//
//                    Log.e("FetchDatafromRealtime_register", "Error ${snapshot.value}")
//
//                }
//            }
//        }
//        else{

            //Important

//            database.child(loginUserId).addValueEventListener(object :
//                ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if(snapshot.exists())
//                    {
//                        val loginUsername2 = snapshot.child("name").value.toString()
//                        val loginEmailId2 = snapshot.child("email").value.toString()
//                        val urlFromRealDatabase = snapshot.child("url").value.toString()
//
//                        if (checkLogin =="SourceLogin") {
//                            HeaderUser.text = "$str2$loginUsername2"
//                            HeaderEmail.text = "$str3$loginEmailId2"
//                            HeaderUserID.text = "$str1$loginUserId"
//
//                            Glide.with(this@MainActivity)
//                                .load(urlFromRealDatabase)
//                                .placeholder(R.drawable.loading_for_image_vector)
//                                .error(R.drawable.default_image_of_profile)
//                                .into(HeaderImageView)
//
//                            Log.d(
//                                "DataFromLogin",
//                                "Username: $loginUserName , UserEmail: $loginEmailID , UserID: $loginUserId - Data got from login ..." +
//                                        "Username: $HeaderUser , UserEmail: $HeaderEmail , UserID: $HeaderUserID after putting data from login...."
//                            )
//
//                            sendUserID = loginUserId
//                        }else {
//
//                            HeaderUser.text = "N/A_Login"
//                            HeaderEmail.text = "N/A_Login"
//                            HeaderUserID.text = "N/A_Login"
//                            Log.e("FetchDataFromRealTimeError", "Error ${snapshot.value}")
//                        }
//
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(this@MainActivity,"Failed To Show users Details",Toast.LENGTH_SHORT).show()
//                }
//
//            })


                setupRealTimeListner(loginUserId)

//        }




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
                R.id.home -> replaceFragment(HomeFragment(),menuitem.title.toString(),"Home")
                R.id.favourite -> replaceFragment(FavouriteFragment(),menuitem.title.toString(),"Favourite")
                R.id.my_hackathons -> replaceFragment(My_HackathonsFragment(),menuitem.title.toString(),"My_Hackathons")
                R.id.my_profile -> replaceFragment(ProfileFragment(),menuitem.title.toString(),"My_Profie")
                R.id.friends -> replaceFragment(FriendsFragment(),menuitem.title.toString(),"Friends")
                R.id.logout -> Logout(LogOutFragment())



            }
            true
        }




    }

    private fun setupRealTimeListner(loginUserId: String) {
        database.child(loginUserId).addValueEventListener(object :
            ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val loginUsername2 = snapshot.child("username").value.toString()
                    val loginEmailId2 = snapshot.child("email").value.toString()
                    val urlFromRealDatabase = snapshot.child("url").value.toString()

                    if (checkLogin =="SourceLogin") {
                        HeaderUser.text = "$str2$loginUsername2"
                        HeaderEmail.text = "$str3$loginEmailId2"
                        HeaderUserID.text = "$str1$loginUserId"

                        val activity = this@MainActivity // or `context` if used in a Fragment
                        if (!activity.isDestroyed && !activity.isFinishing) {
                            Glide.with(activity)
                                .load(urlFromRealDatabase)
                                .placeholder(R.drawable.loading_for_image_vector)
                                .error(R.drawable.default_image_of_profile)
                                .into(HeaderImageView)
                        }

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

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Failed To Show users Details",Toast.LENGTH_SHORT).show()
            }

        })

    }


    @SuppressLint("SuspiciousIndentation")
    private fun replaceFragment(fragment : Fragment, title : String , tags : String) {

            val sendid = sendUserID

        val bundle = Bundle()
        bundle.putString("UserId",sendid)
        fragment.arguments = bundle

            supportFragmentManager.beginTransaction().replace(R.id.Fragment_Container_Main,fragment,tags)
                .commit()


            drawerLayout.closeDrawer(GravityCompat.START)

            setTitle(title)


        }

    private fun Logout(fragment1 : Fragment){

        val FRAME = findViewById<FrameLayout>(R.id.Logout_container_main)

        supportFragmentManager.beginTransaction().replace(R.id.Logout_container_main,fragment1)
            .addToBackStack(null).commit()

        drawerLayout.closeDrawer(GravityCompat.START)

        FRAME.visibility = View.VISIBLE


    }


}