package com.example.beertime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.beertime.util.CHANNEL_ID
import com.example.beertime.util.NOTIFICATION_ID

class NotificationBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        context?.let {
            val builder = NotificationCompat.Builder(it, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_icon_beer)
                .setContentTitle(it.getText(R.string.notification_title))
                .setContentText(it.getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())

            with(NotificationManagerCompat.from(it)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}
