package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MyService:Service() {

    companion object {
        val START_SERVICE = "start"
        val STOP_SERVICE = "stop"
        val FOREGROUND_SERVICE = "foreground"

        const val TAG = "MyService"

    }

    var isForegroundService = false

    val CHANNEL_ID:String = " channelId"

    inner class LocalBinder:Binder(){
        fun getService():MyService = this@MyService
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val intentAction = intent?.action

        when(intentAction){
            START_SERVICE ->{
                showToast("Service Started")
            }
            STOP_SERVICE ->{
                stopThisService()
            }

            FOREGROUND_SERVICE ->{
                doForegroundThings()
            }
        }


        return super.onStartCommand(intent, flags, startId)
    }

     fun doForegroundThings() {
        showToast("Going Foreground")

        createNotificationChannel()

        val notificationIntent = Intent(this,MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0)
        isForegroundService = true

        val builder = NotificationCompat.Builder(this,CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("My Notification")
            .setContentText("This is a notification service")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val notification = builder.build()
        with(NotificationManagerCompat.from(this)){
            // Notification id cannot be zero(0)
            notify(4,notification)
        }



    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val name = " My Custom Channel"
            val descriptionText =  "My Channel's Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description=descriptionText
            }

            val notificationMAnager : NotificationManager = getSystemService(NOTIFICATION_SERVICE)as NotificationManager
            notificationMAnager.createNotificationChannel(channel)
        }
    }

    private fun stopThisService() {
        showToast("Service Stopped")

        try {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }

    private val binder = LocalBinder()

    override fun onBind(p0: Intent?): IBinder? {
        return binder

   }

}