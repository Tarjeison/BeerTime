package com.example.beertime.feature.profile

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.beertime.models.Gender
import com.example.beertime.models.UserProfile
import java.io.FileNotFoundException
import java.nio.charset.Charset

class ProfileViewModel (application: Application): AndroidViewModel(application){

    companion object {
        const val MODEL_TAG = "ProfileViewModel"
        const val FILE_AGE = "age"
        const val FILE_WEIGHT = "weight"
        const val FILE_GENDER = "gender"
    }

    private val appContext = getApplication<Application>().applicationContext

    fun getUserProfile(): UserProfile? {
        return try {
            val age = String(appContext.openFileInput(FILE_AGE).readBytes(), Charset.defaultCharset()).toInt()
            val weight = String(appContext.openFileInput(FILE_WEIGHT).readBytes(), Charset.defaultCharset()).toInt()
            val gender = Gender.stringToGender(String(appContext.openFileInput(FILE_GENDER).readBytes(),Charset.defaultCharset()))
            UserProfile(age, gender, weight)
        } catch (error: FileNotFoundException) {
            Log.d(MODEL_TAG, error.message ?: "UserProfileLoadError")
            null
        }
    }

    fun saveUserProfile(context: Context?, userProfile: UserProfile) {
        if (context == null) {
            return
        }
        context.openFileOutput(FILE_AGE, Context.MODE_PRIVATE).use {
            it.write(userProfile.age.toString().toByteArray(Charset.defaultCharset()))
        }

        context.openFileOutput(FILE_GENDER, Context.MODE_PRIVATE).use {
            it.write(userProfile.gender.toString().toByteArray(Charset.defaultCharset()))
        }

        context.openFileOutput(FILE_WEIGHT, Context.MODE_PRIVATE).use {
            it.write(userProfile.weight.toString().toByteArray(Charset.defaultCharset()))
        }
    }
}