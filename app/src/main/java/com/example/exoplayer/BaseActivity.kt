package com.example.exoplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val watch_bt=findViewById<Button>(R.id.watch_now_bt)

        watch_bt.setOnClickListener{
            val intent =Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}