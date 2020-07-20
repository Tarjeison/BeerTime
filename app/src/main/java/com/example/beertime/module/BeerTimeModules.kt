package com.example.beertime.module

import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.beertime.util.CHANNEL_ID
import com.example.beertime.R
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val beerTimeModules = module {
    single {
        createNotificationBuilder(androidContext())
    }
}

private fun createNotificationBuilder(context: Context): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_icon_beer)
        .setContentTitle(context.getText(R.string.notification_title))
        .setContentText(context.getString(R.string.notification_text))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
}