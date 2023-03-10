package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
    }

    private var downloadID: Long = 0
    private lateinit var notificationManager: NotificationManager
    private lateinit var loadingButton: LoadingButton
    private var downloaded = ""

    private val radioUriMap = mapOf<Int, Uri>(
        R.id.glide_radio to Uri.parse(GLIDE_URL),
        R.id.loadapp_radio to Uri.parse(LOADAPP_URL),
        R.id.retrofit_radio to Uri.parse(RETROFIT_URL)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager = getSystemService<NotificationManager>(NotificationManager::class.java)

        createChannel(
            getString(R.string.loadapp_notification_channel_id),
            getString(R.string.loadapp_notification_channel_name)
        )

        loadingButton = findViewById(R.id.custom_button)

        loadingButton.setOnClickListener {
            loadingButton.buttonState = ButtonState.Clicked

            val radioSelected = sourceSelection.checkedRadioButtonId

            if (radioSelected != -1) {
                loadingButton.buttonState = ButtonState.Loading
                download(radioUriMap[radioSelected]!!)
                downloaded = findViewById<RadioButton>(radioSelected).text.toString()
            } else {

                val toast = Toast.makeText(this, R.string.toast_text, Toast.LENGTH_SHORT).apply {
                    addCallback(object : Toast.Callback() {
                        override fun onToastHidden() {
                            super.onToastHidden()
                            loadingButton.buttonState = ButtonState.Completed
                        }
                    })
                }

                toast.show()
            }
        }
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val id = it.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadID) {

                    notificationManager = ContextCompat.getSystemService(
                        applicationContext,
                        NotificationManager::class.java
                    ) as NotificationManager

                    notificationManager.cancelAll()

                    notificationManager.sendNotification(
                        downloaded,
                        "Success",
                        applicationContext
                    )

                    loadingButton.buttonState = ButtonState.Completed
                }

            }
        }
    }


    private fun download(fromUri: Uri) {
        val request =
            DownloadManager.Request(fromUri)
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun createChannel(channelId: String, channelName: String) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_LOW
        )

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = getString(R.string.notification_description)

        notificationManager.createNotificationChannel(notificationChannel)
    }
}
