package com.example.service

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.service.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    var myService : MyService? = null
    var mBound = false
    val TAG = "MainActivity"
    private val serviceConnection : ServiceConnection = object :ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val binder : MyService.LocalBinder = service as MyService.LocalBinder
            myService = binder.getService()
            mBound = true
            bringServiceToForeground()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }

    }

    private fun bringServiceToForeground() {
        myService?.let {
            if(!it.isForegroundService){
                val intent = Intent(this,MyService::class.java)
                intent.action = MyService.FOREGROUND_SERVICE
                ContextCompat.startForegroundService(this,intent)
                it.doForegroundThings()
            }else{
                Log.d(TAG,"Service is already in foreground")
            }
        }?:Log.d(TAG,"Service is null ")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //service

        binding.startServiceSmatly.setOnClickListener {
            val intent = Intent(this@MainActivity,MyService::class.java)
            intent.action = MyService.START_SERVICE
            startService(intent)
            bindWithService()
        }

        binding.startForegroundService.setOnClickListener {
            val intent = Intent(this@MainActivity,MyService::class.java)
            intent.action = MyService.FOREGROUND_SERVICE
            ContextCompat.startForegroundService(this@MainActivity, intent)

        }

        binding.stopService.setOnClickListener {
            val intent = Intent(this@MainActivity,MyService::class.java)
            intent.action = MyService.STOP_SERVICE
            startService(intent)
        }

    }

    private fun bindWithService() {
        val intent  = Intent(this,MyService::class.java)
        bindService(intent,serviceConnection, BIND_IMPORTANT)
    }
}