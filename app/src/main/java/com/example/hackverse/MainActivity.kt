package com.example.hackverse

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var  drawerLayout: DrawerLayout
    private lateinit var navigation : NavigationView

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


        val menuButtonOut: ImageButton = findViewById<ImageButton>(R.id.menu_button)

        val menuButonIn : ImageButton = headerfile.findViewById(R.id.menu_button_In)

        menuButtonOut.setOnClickListener {

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

            if(drawerLayout.isDrawerOpen((GravityCompat.START)))
            {
                drawerLayout.closeDrawer((GravityCompat.START))

            }
            else
            {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }




    }
}