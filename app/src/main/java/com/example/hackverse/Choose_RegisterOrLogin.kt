package com.example.hackverse

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Choose_RegisterOrLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_register_or_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val register = findViewById<Button>(R.id.Register)
        val login = findViewById<Button>(R.id.Login)

        register.setOnClickListener {

            val register_intent = Intent(this,RegisterActivity::class.java)
            startActivity(register_intent)
            finish()
        }

        login.setOnClickListener {

            val login_intent = Intent(this,LoginActivity::class.java)
            startActivity(login_intent)
            finish()

        }

    }
}