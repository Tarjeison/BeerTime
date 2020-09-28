package com.pd.beertimer.module

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.pd.beertimer.feature.info.InfoViewModel
import com.pd.beertimer.feature.profile.ProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val beerTimeModules = module {
    factory {
        InfoViewModel(androidContext())
    }

    factory {
        ProfileViewModel(androidApplication())
    }

    single {
        getFirebaseAnalytics()
    }
}

private fun getFirebaseAnalytics(): FirebaseAnalytics {
    return Firebase.analytics
}
