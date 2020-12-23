package com.example.recipeapp.j.Model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class Recipe(
    val recipeName: String = "",
    val recipeType: String = "",
    val recipeIngredients: String = "",
    val recipeStep: String = "",
    val recipeImage: String = "",
    var recipeId: String = ""
): Parcelable