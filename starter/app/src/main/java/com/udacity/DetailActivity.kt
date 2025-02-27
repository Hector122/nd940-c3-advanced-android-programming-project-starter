package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*


class DetailActivity : AppCompatActivity() {
    
    companion object {
        const val NOTIFICATION_NAME = "CheckStatusButton"
        const val DOWNLOAD_FILE = "downloadFile"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        
        setSupportActionBar(toolbar)
        
        val downloadFile = intent.getParcelableExtra<DownloadFile>(DOWNLOAD_FILE)
        
        cancelNotificationAction(intent.getIntExtra(NOTIFICATION_NAME, 2))
        
        text_name.text = downloadFile?.name
        text_name.setTextColor(getColor(R.color.colorPrimaryDark))
        
        text_status.text = downloadFile?.status
        text_status.setTextColor(getColor(downloadFile!!.status))
        
        button.setOnClickListener {
            //clear the activity stack.
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
    
    private fun getColor(status: String) = ContextCompat.getColor(this,
            if (status == getString(R.string.success)) R.color.colorPrimaryDark else R.color.colorRed)
    
    private fun cancelNotificationAction(id: Int) {
        // int notification manager
        val notificationManager = ContextCompat.getSystemService(applicationContext,
                NotificationManager::class.java) as NotificationManager
        
        notificationManager.cancel(id)
    }
    
    
}
