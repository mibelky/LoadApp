package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        getSystemService(NotificationManager::class.java).cancel(intent.extras?.get("notification ID") as Int)
        source_text.text = intent.extras?.get("source").toString()
        status_text.text = intent.extras?.get("status").toString()

        ok_button.setOnClickListener {
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
    }

}
