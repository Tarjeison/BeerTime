package com.pd.beertimer.util

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.pd.beertimer.R

object ToastHelper {
    fun createToast(
        layoutInflater: LayoutInflater,
        nullableContext: Context?,
        @StringRes resId: Int,
        @DrawableRes imageId: Int
    ) {
        nullableContext?.let { context ->
            val layout: View = layoutInflater.inflate(R.layout.view_toast, null)
            val text: TextView = layout.findViewById(R.id.tvMessage)
            val image: ImageView = layout.findViewById(R.id.ivToast)
            image.setImageResource(imageId)
            text.text = context.getString(resId)
            with(Toast(context)) {
                setGravity(Gravity.CENTER_VERTICAL or Gravity.TOP, 0, 300)
                duration = Toast.LENGTH_SHORT
                view = layout
                show()
            }
        }
    }
}
