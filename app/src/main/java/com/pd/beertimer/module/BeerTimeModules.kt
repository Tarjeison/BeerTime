package com.pd.beertimer.module

import android.content.Context
import androidx.room.Room
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.pd.beertimer.feature.drinks.DrinkRepository
import com.pd.beertimer.feature.drinks.MyDrinksViewModel
import com.pd.beertimer.feature.info.InfoViewModel
import com.pd.beertimer.feature.profile.ProfileViewModel
import com.pd.beertimer.feature.startdrinking.StartDrinkingViewModel
import com.pd.beertimer.room.AppDatabase
import com.pd.beertimer.room.DrinkDao
import com.pd.beertimer.util.AlarmUtils
import com.pd.beertimer.util.ProfileStorage
import com.pd.beertimer.util.SHARED_PREF_BEER_TIME
import com.pd.beertimer.util.StorageHelper
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val beerTimeModules = module {
    factory {
        InfoViewModel(androidContext())
    }

    factory {
        ProfileViewModel(profileStorage = get())
    }

    factory {
        androidApplication().getSharedPreferences(
            SHARED_PREF_BEER_TIME,
            Context.MODE_PRIVATE
        )
    }

    factory {
        StorageHelper(
            sharedPreferences = get()
        )
    }

    single {
        AlarmUtils(androidContext())
    }

    viewModel {
        StartDrinkingViewModel(
            drinkRepository = get(),
            applicationContext = androidContext(),
            profileStorage = get(),
            firebaseAnalytics = get()
        )
    }

    factory {
        ProfileStorage(appContext = androidContext())
    }

    single {
        getFirebaseAnalytics()
    }

    viewModel { MyDrinksViewModel(drinkRepository = get()) }

    factory {
        DrinkRepository(drinkDb = get())
    }

    single {
        getDrinkDao(get())
    }

    single {
        getRoomDatabase(androidApplication())
    }
}

private fun getFirebaseAnalytics(): FirebaseAnalytics {
    return Firebase.analytics
}

fun getDrinkDao(database: AppDatabase): DrinkDao {
    return database.drinkDao()
}

fun getRoomDatabase(applicationContext: Context): AppDatabase {
    return Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java, "drink"
    ).build()
}

