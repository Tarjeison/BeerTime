package com.pd.beertimer

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pd.beertimer.util.CHANNEL_ID
import com.pd.beertimer.util.INTENT_EXTRA_NOTIFICATION_MESSAGE
import com.pd.beertimer.util.NOTIFICATION_ID

class NotificationBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        context?.let {

            val intent = Intent(it, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val pendingIntent: PendingIntent = PendingIntent.getActivity(it, 0, intent, 0)

            val builder = NotificationCompat.Builder(it, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_beer)
                .setContentTitle(it.getText(R.string.notification_title))
                .setContentText(
                    p1?.extras?.getString(INTENT_EXTRA_NOTIFICATION_MESSAGE)
                        ?: it.getString(R.string.notification_text)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(it)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}
