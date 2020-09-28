package com.pd.beertimer

import android.app.Application
import com.pd.beertimer.module.beerTimeModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BeerTimerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BeerTimerApplication)
            modules(listOf(beerTimeModules))
        }
    }
}