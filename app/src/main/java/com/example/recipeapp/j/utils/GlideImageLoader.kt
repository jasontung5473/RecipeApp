package com.example.recipeapp.j.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.recipeapp.R
import java.io.IOException

class GlideImageLoader(val context: Context) {

    fun loadImage(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            //********** need to import dependencies ***********
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(imageView)
        }
        catch(e: IOException) {
            e.printStackTrace()
        }
    }

}