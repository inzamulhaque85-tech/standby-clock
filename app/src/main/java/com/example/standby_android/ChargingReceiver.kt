package com.example.standby_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
// MainActivity Java ক্লাসের সঠিক Import যুক্ত করা হয়েছে
import com.example.standby_android.MainActivity

class ChargingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_POWER_CONNECTED) {
            val standbyIntent = Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(standbyIntent)
        }
    }
}
