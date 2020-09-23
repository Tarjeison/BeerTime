package com.example.beertime.module

import com.example.beertime.feature.info.InfoViewModel
import com.example.beertime.feature.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val beerTimeModules = module {
    factory {
        InfoViewModel(androidContext())
    }

    factory {
        ProfileViewModel()
    }
}
