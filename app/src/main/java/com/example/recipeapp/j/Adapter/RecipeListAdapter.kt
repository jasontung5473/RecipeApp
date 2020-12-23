package com.example.recipeapp.j.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipeapp.R
import com.example.recipeapp.j.Model.Constants
import com.example.recipeapp.j.Model.Recipe
import com.example.recipeapp.j.ui.RecipeDetailActivity
import com.example.recipeapp.j.utils.GlideImageLoader
import kotlinx.android.synthetic.main.recipe_list_item.view.*

class RecipeListAdapter(private val data: MutableList<Recipe>)
    : RecyclerView.Adapter<RecipeListAdapter.AdapterViewHolder>() {

    class AdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        return AdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recipe_list_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val currentItem = data[position]
        holder.itemView.apply {
            recipe_name_textView.text = currentItem.recipeName
            if (currentItem.recipeImage!=""){
                GlideImageLoader(context).loadImage(currentItem.recipeImage, recipe_imageView)
            }else{
                recipe_imageView.setImageResource(R.drawable.ic_baseline_image_24)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, RecipeDetailActivity::class.java)
                intent.putExtra(Constants.RECIPE_DETAILE, currentItem)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}