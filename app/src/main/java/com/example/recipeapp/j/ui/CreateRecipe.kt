package com.example.recipeapp.j.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recipeapp.R
import com.example.recipeapp.j.Firestore.FirestoreClass
import com.example.recipeapp.j.Model.Constants
import com.example.recipeapp.j.Model.Recipe
import com.example.recipeapp.j.utils.GlideImageLoader
import kotlinx.android.synthetic.main.activity_create_recipe.*
import java.io.IOException


class CreateRecipe : BaseActivity(), View.OnClickListener {
    private var selectedImageUri: Uri?=null
    private var uploadedImageUri: String = ""
    private var message: String = ""
    private var recipeDetail:Recipe = Recipe()
    private var check: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        create_recipe_type_spinner.adapter = Constants.setupSpinnerAdapter(this)
        checkStatus()

        done_button.setOnClickListener(this)
        imageView_ic_back.setOnClickListener(this)
        recipe_image_card.setOnClickListener(this)
    }

    private fun checkStatus() {
        if (intent.hasExtra(Constants.RECIPE_DETAILE)){
            check = false
            fill_out_textView_title.text = resources.getString(R.string.let_edit_text)
            create_textView_title.text = resources.getString(R.string.edit_recipe_title)

            recipeDetail = intent.getParcelableExtra(Constants.RECIPE_DETAILE)!!
            recipeName_editText.setText(recipeDetail.recipeName)
            recipeIngredient_editTextTextMultiLine.setText(recipeDetail.recipeIngredients)
            recipeStep_editTextTextMultiLine.setText(recipeDetail.recipeStep)
            if (recipeDetail.recipeImage!=""){
                GlideImageLoader(this).loadImage(recipeDetail.recipeImage, recipe_photo_imageView)
            }
            when (recipeDetail.recipeType) {
                "Main Course" -> {
                    create_recipe_type_spinner.setSelection(0)
                }
                "Snack" -> {
                    create_recipe_type_spinner.setSelection(1)
                }
                "Dessert & Beverage" -> {
                    create_recipe_type_spinner.setSelection(2)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        if(v!=null){
            when(v.id){
                R.id.done_button -> {
                    if (validateUserInput()){
                        showProgress(resources.getString(R.string.loading_textView))
                        if (selectedImageUri != null) {
                            FirestoreClass().uploadImageToStorage(this, selectedImageUri)
                        } else {
                            uploadRecipe()
                        }
                    }else{
                        Toast.makeText(this, "Please enter recipe ${message}.",
                            Toast.LENGTH_LONG).show()
                    }
                }
                R.id.imageView_ic_back -> {
                    setConfirmDialog(this, resources.getString(R.string.exit_without_save))
                }
                R.id.recipe_image_card -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
            }
        }
    }

    private fun validateUserInput():Boolean {
        return when{
            TextUtils.isEmpty(recipeName_editText.text.toString().trim { it <= ' ' })-> {
                message = "name"
                false
            }
            TextUtils.isEmpty(recipeIngredient_editTextTextMultiLine.text.toString())-> {
                message = "ingredient"
                false
            }
            TextUtils.isEmpty(recipeStep_editTextTextMultiLine.text.toString())-> {
                message = "step"
                false
            }else-> {
                true
            }
        }
    }

    override fun onBackPressed() {
        setConfirmDialog(this, resources.getString(R.string.exit_without_save))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if ( requestCode == Constants.SELECT_IMAGE_REQUEST_CODE) {
                if(data != null) {
                    try {
                        selectedImageUri = data.data!!
                        GlideImageLoader(this).loadImage(selectedImageUri!!, recipe_photo_imageView)
                    }catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show ( )
                    }
                }
            }
        }
    }

    private fun uploadRecipe(){
        val mutableMap = mutableMapOf<String, Any>()
        mutableMap["recipeName"] = recipeName_editText.text.toString().trim { it <= ' ' }
        mutableMap["recipeType"] = create_recipe_type_spinner.selectedItem.toString()
        mutableMap["recipeIngredients"] = recipeIngredient_editTextTextMultiLine.text.toString()
        mutableMap["recipeStep"] = recipeStep_editTextTextMultiLine.text.toString()
        if(uploadedImageUri != ""){
            mutableMap["recipeImage"] = uploadedImageUri
        }else{
            mutableMap["recipeImage"] = recipeDetail.recipeImage
        }

        if(check){
            FirestoreClass().addNewRecipe(this, mutableMap)
        }else{
            FirestoreClass().updateRecipeDetail(this, mutableMap, recipeDetail.recipeId)
        }
    }

    fun addSuccess(){
        hideProgress()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()

        Toast.makeText(
            this,
            resources.getString(R.string.success),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun addFailure(){
        hideProgress()
        Toast.makeText(
            this,
            resources.getString(R.string.failure_text),
            Toast.LENGTH_SHORT
        ).show()
    }

    fun uploadImageSuccess(image: String){
        uploadedImageUri = image
        uploadRecipe()
    }
}