package com.example.recipeapp.j.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeapp.R
import com.example.recipeapp.j.Adapter.RecipeListAdapter
import com.example.recipeapp.j.Firestore.FirestoreClass
import com.example.recipeapp.j.Model.Constants
import com.example.recipeapp.j.Model.Recipe
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private var recipeType: String = ""
    private lateinit var adapter: RecipeListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSpinner()

        fab_create_recipe.setOnClickListener {
            startActivity(Intent(this, CreateRecipe::class.java))
        }
    }

    private fun setSpinner() {
        recipe_type_spinner.adapter = Constants.setupSpinnerAdapter(this)
        recipeType = recipe_type_spinner.selectedItem.toString()
        recipe_type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                return
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showProgress(resources.getString(R.string.loading_textView))
                recipeType = recipe_type_spinner.selectedItem.toString()
                FirestoreClass().getRecipe(this@MainActivity,recipeType)
            }
        }
    }

    fun setupRecipeList(recipe: MutableList<Recipe>){
        if(recipe.size>0){
            adapter = RecipeListAdapter(recipe)
            recipe_RecyclerView.adapter = adapter
            recipe_RecyclerView.layoutManager = LinearLayoutManager(this)
            recipe_RecyclerView.hasFixedSize()
            recipe_RecyclerView.visibility = View.VISIBLE
            no_recipe_textView.visibility = View.GONE
        }else{
            recipe_RecyclerView.visibility = View.GONE
            no_recipe_textView.visibility = View.VISIBLE
        }
        hideProgress()
    }
}