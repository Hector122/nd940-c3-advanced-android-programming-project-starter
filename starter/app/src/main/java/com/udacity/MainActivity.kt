package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    
    private var downloadID: Long = 0
    private lateinit var downloadUrl: String
    
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        
        radio_group.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radio_glide -> downloadUrl = URL_GLIDE
                R.id.radio_project -> downloadUrl = URL_PROJECT
                R.id.radio_retrofit -> downloadUrl = URL_RETROFIT
            }
        }
        
        custom_button.setOnClickListener {
            if (radio_group.checkedRadioButtonId == -1) {
                Toast.makeText(this, getString(R.string.select_file), Toast.LENGTH_SHORT).show()
            } else {
                download()
            }
        }
    }
    
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }
    
    private fun download() {
            val request = DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
            
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }
    
    companion object {
        private const val URL = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
        private const val URL_PROJECT = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
    }
}
