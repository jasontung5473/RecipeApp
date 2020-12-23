package com.example.recipeapp.j.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.recipeapp.R
import com.example.recipeapp.j.Firestore.FirestoreClass
import com.example.recipeapp.j.Model.Constants
import com.example.recipeapp.j.Model.Recipe
import com.example.recipeapp.j.utils.GlideImageLoader
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.activity_recipe_detail.fab_create_recipe
import kotlinx.android.synthetic.main.activity_recipe_detail.imageView_ic_back
import kotlinx.android.synthetic.main.activity_recipe_detail.recipeIngredient_editTextTextMultiLine
import kotlinx.android.synthetic.main.activity_recipe_detail.recipeStep_editTextTextMultiLine
import kotlinx.android.synthetic.main.activity_recipe_detail.recipe_type_textView


class RecipeDetailActivity : BaseActivity(), View.OnClickListener {
    private var recipeDetail:Recipe = Recipe()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        recipeIngredient_editTextTextMultiLine.isEnabled = false
        recipeStep_editTextTextMultiLine.isEnabled = false

        imageView_ic_back.setOnClickListener(this)
        imageView_ic_delete.setOnClickListener(this)
        fab_create_recipe.setOnClickListener(this)

        if (intent.hasExtra(Constants.RECIPE_DETAILE)){
            recipeDetail = intent.getParcelableExtra(Constants.RECIPE_DETAILE)!!
            setupUI(recipeDetail)
        }
    }

    private fun setupUI(recipeDetail: Recipe){
        if (recipeDetail.recipeImage!=""){
            GlideImageLoader(this).loadImage(recipeDetail.recipeImage, recipe_detail_imageView)
        }else{
            recipe_detail_imageView.setImageResource(R.drawable.ic_baseline_image_24)
        }
        recipe_name_textView.text = recipeDetail.recipeName
        recipe_type_textView.text = recipeDetail.recipeType
        recipeIngredient_editTextTextMultiLine.setText(recipeDetail.recipeIngredients)
        recipeStep_editTextTextMultiLine.setText(recipeDetail.recipeStep)
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.imageView_ic_back -> {
                    onBackPressed()
                }
                R.id.imageView_ic_delete -> {
                    setConfirmDialog(this, resources.getString(R.string.confirm_delete_recipe))
                }
                R.id.fab_create_recipe -> {
                    val intent = Intent(this, CreateRecipe::class.java)
                    intent.putExtra(Constants.RECIPE_DETAILE, recipeDetail)
                    startActivity(intent)
                }
            }
        }
    }

    fun deleteRecipe(){
        showProgress(resources.getString(R.string.loading_textView))
        FirestoreClass().deleteRecipe(this, recipeDetail)
    }

    fun deleteSuccess(){
        hideProgress()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()

        Toast.makeText(this, resources.getString(R.string.success), Toast.LENGTH_SHORT).show()
    }

    fun deleteFailure(){
        hideProgress()
        Toast.makeText(this, resources.getString(R.string.failure_text), Toast.LENGTH_SHORT).show()
    }
}