package com.example.beertime

import android.app.Application
import com.example.beertime.module.beerTimeModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class BeerTimeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@BeerTimeApplication)
            modules(listOf(beerTimeModules))
        }
    }
}