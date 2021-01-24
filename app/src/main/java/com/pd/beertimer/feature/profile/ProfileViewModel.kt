package com.pd.beertimer.feature.profile

import androidx.lifecycle.ViewModel
import com.pd.beertimer.models.UserProfile
import com.pd.beertimer.util.ProfileStorage

class ProfileViewModel(private val profileStorage: ProfileStorage): ViewModel() {

    companion object {
        const val MODEL_TAG = "ProfileViewModel"
        const val FILE_AGE = "age"
        const val FILE_WEIGHT = "weight"
        const val FILE_GENDER = "gender"
    }

    fun getUserProfile(): UserProfile? {
        return profileStorage.getUserProfile()
    }

    fun saveUserProfile(userProfile: UserProfile) {
        profileStorage.saveUserProfile(userProfile)
    }
}