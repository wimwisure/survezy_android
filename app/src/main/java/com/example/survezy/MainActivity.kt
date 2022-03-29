package com.example.survezy

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.wimwisure.survezy.Survezy

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val survezy = Survezy(this)

        findViewById<Button>(R.id.button).setOnClickListener {
            survezy.show(eventId = "c2F1cmF2QHdpbXdpc3VyZS5jb20gc3VydmV6eQ==")
        }
    }
}
