package com.pd.beertimer.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> Fragment.observe(liveData: LiveData<T>, function: (value: T) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer(function))
}
