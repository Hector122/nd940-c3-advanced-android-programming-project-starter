package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {
    
    private var downloadID: Long = 0
    private lateinit var downloadUrl: String
    private lateinit var downloadFile: DownloadFile
    
    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        
        // Init
        downloadFile = DownloadFile("", "")
        
        //Register the BroadcastReceiver when the downloadManager complete
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        
        // radio group listener
        radio_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_glide -> {
                    downloadUrl = URL_GLIDE
                    downloadFile.name = radio_glide.text.toString()
                }
                R.id.radio_project -> {
                    downloadUrl = URL_PROJECT
                    downloadFile.name = radio_project.text.toString()
                }
                R.id.radio_retrofit -> {
                    downloadUrl = URL_RETROFIT
                    downloadFile.name = radio_retrofit.text.toString()
                }
            }
        }
        
        // button listeners
        custom_button.setOnClickListener {
            if (radio_group.checkedRadioButtonId == -1) {
                custom_button.buttonState = ButtonState.Completed
                Toast.makeText(this, getString(R.string.select_file), Toast.LENGTH_SHORT).show()
            } else {
                notificationManager.cancelNotifications()
                download()
            }
        }
        
        //create a notification channel
        createChannel(CHANNEL_ID, CHANNEL_NAME)
    }
    
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            
            //get instance for download manager
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            
            //get the cursor with the id to verify the request status
            // review from: https://camposha.info/android-examples/android-downloadmanager/#gsc.tab=0
            val query = DownloadManager.Query().setFilterById(id!!)
            val cursor = downloadManager.query(query)
            cursor.moveToFirst()
            
            when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                DownloadManager.STATUS_SUCCESSFUL -> downloadFile.status = getString(R.string.success)
                DownloadManager.STATUS_FAILED -> downloadFile.status = getString(R.string.fail)
            }
            
            // Set button to completed state to stop the animation
            custom_button.buttonState = ButtonState.Completed
            
            // send notification
            notificationManager.sendNotification(applicationContext, downloadFile)
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
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "loadApp"
        
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
        private const val URL_PROJECT =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        
        private const val NOTIFICATION_ID = 0
    }
    
    private fun createChannel(id: String, name: String) {
        //Channels are available from api level 26.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create a notification channel
            val notificationChannel =
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = getString(R.string.notification_description)
            
            // int notification manager
            notificationManager = ContextCompat.getSystemService(applicationContext,
                    NotificationManager::class.java) as NotificationManager
            // set channel and send notification
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
    
    //Set a extension func
    fun NotificationManager.sendNotification(context: Context, downloadFile: DownloadFile) {
        // Init pending intent with the activity to be launch
        val intent = Intent(applicationContext, DetailActivity::class.java)
        intent.putExtra(DetailActivity.DOWNLOAD_FILE, downloadFile)
        intent.putExtra(DetailActivity.NOTIFICATION_NAME, NOTIFICATION_ID)
        
        pendingIntent = PendingIntent.getActivity(applicationContext,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        
        // Add action button
        action = NotificationCompat.Action(null,
                getString(R.string.notification_button_text),
                pendingIntent)
        
        //Instance of NotificationCompat.Builder
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_description))
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(action)
        
        notify(NOTIFICATION_ID, builder.build())
    }
    
    //cancel all notification.
    private fun NotificationManager.cancelNotifications() {
        cancelAll()
    }
}

@Parcelize
data class DownloadFile(var name: String, var status: String) : Parcelable
