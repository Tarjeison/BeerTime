package com.pd.beertimer.util

import android.content.Context
import android.util.Log
import com.pd.beertimer.feature.profile.ProfileViewModel
import com.pd.beertimer.models.Gender
import com.pd.beertimer.models.UserProfile
import java.io.FileNotFoundException
import java.nio.charset.Charset

class ProfileStorage(private val appContext: Context) {

    fun getUserProfile(): UserProfile? {
        return try {
            val weight = String(appContext.openFileInput(ProfileViewModel.FILE_WEIGHT).readBytes(), Charset.defaultCharset()).toInt()
            val gender = Gender.stringToGender(String(appContext.openFileInput(ProfileViewModel.FILE_GENDER).readBytes(),
                Charset.defaultCharset()))
            UserProfile(gender, weight)
        } catch (error: FileNotFoundException) {
            Log.d(ProfileViewModel.MODEL_TAG, error.message ?: "UserProfileLoadError")
            null
        }
    }

    fun saveUserProfile(userProfile: UserProfile) {
        appContext.openFileOutput(ProfileViewModel.FILE_GENDER, Context.MODE_PRIVATE).use {
            it.write(userProfile.gender.toString().toByteArray(Charset.defaultCharset()))
        }

        appContext.openFileOutput(ProfileViewModel.FILE_WEIGHT, Context.MODE_PRIVATE).use {
            it.write(userProfile.weight.toString().toByteArray(Charset.defaultCharset()))
        }
    }
}
