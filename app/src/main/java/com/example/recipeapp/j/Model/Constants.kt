package com.example.recipeapp.j.Model

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import com.example.recipeapp.R

object Constants {
    const val RECIPE_DETAILE: String = "Recipe_detail"
    const val READ_STORAGE_PERMISSION_CODE: Int = 999
    const val RECIPE_TYPE: String = "recipeType"
    const val RECIPE: String = "recipe"
    const val SELECT_IMAGE_REQUEST_CODE:Int = 666
    const val RECIPE_IMAGE:String = "recipeImage"


    fun showImageChooser(activity: Activity) {
        val Intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(Intent, SELECT_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?) : String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

    fun setupSpinnerAdapter(activity: Activity): ArrayAdapter<CharSequence> {
        val adapter = ArrayAdapter.createFromResource(
            activity,
            R.array.recipeType,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }
}