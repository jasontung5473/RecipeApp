package com.example.recipeapp.j.Firestore

import android.app.Activity
import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import com.example.recipeapp.j.Model.Constants
import com.example.recipeapp.j.Model.Recipe
import com.example.recipeapp.j.ui.CreateRecipe
import com.example.recipeapp.j.ui.MainActivity
import com.example.recipeapp.j.ui.RecipeDetailActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun addNewRecipe(activity: CreateRecipe, recipeInfo: MutableMap<String, Any>){
        mFirestore.collection(Constants.RECIPE)
            .document()
            .set(recipeInfo, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Add Recipe", "onSuccess: firebase added")
                activity.addSuccess()
            }.addOnFailureListener { e->
                activity.addFailure()
                Log.e(activity.javaClass.simpleName, "Error in add data", e)
            }
    }

    fun getRecipe(activity: MainActivity, type: String){
        val recipeList = mutableListOf<Recipe>()

        mFirestore.collection(Constants.RECIPE)
            .whereEqualTo(Constants.RECIPE_TYPE, type)
            .get()
            .addOnCompleteListener{ task->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val mutableMap: MutableMap<String, Any> = document.data
                        val id = document.id

                        val recipe = Recipe(
                            mutableMap["recipeName"].toString(),
                            mutableMap["recipeType"].toString(),
                            mutableMap["recipeIngredients"].toString(),
                            mutableMap["recipeStep"].toString(),
                            mutableMap["recipeImage"].toString(),
                            id
                        )
                        recipeList.add(recipe)
                    }
                    activity.setupRecipeList(recipeList)
                    Log.d("Get Recipe", "onSuccess: firebase get")
                }
            }.addOnFailureListener{ e->
                activity.hideProgress()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error in get data",
                    e
                )
            }

    }

    fun uploadImageToStorage(activity: Activity, imageUri: Uri?){
        val storageRef: StorageReference=FirebaseStorage.getInstance().reference.child(
            Constants.RECIPE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageUri
            )
        )

        storageRef.putFile(imageUri!!).addOnSuccessListener { t->
            Log.e(
                "Firebase image URL",
                t.metadata!!.reference!!.downloadUrl.toString()
            )
            t.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri->
                when(activity){
                    is CreateRecipe -> {
                        Log.d("Get image url", "onSuccess: firebase get")
                        activity.uploadImageSuccess(uri.toString())
                    }
                }
            }
        }.addOnFailureListener{ e->
            when(activity){
                is CreateRecipe -> {
                    activity.hideProgress()
                }
            }
            Log.e(
                activity.javaClass.simpleName, e.message, e
            )
        }
    }

    fun updateRecipeDetail(activity: CreateRecipe, recipeMap: MutableMap<String, Any>, id: String){
        mFirestore.collection(Constants.RECIPE)
            .document(id)
            .update(recipeMap)
            .addOnSuccessListener {
                Log.d("Update recipe", "onSuccess: firebase update")
                activity.addSuccess()
            }.addOnFailureListener{ e->
                Log.e(
                    activity.javaClass.simpleName, e.message, e
                )
                activity.addFailure()
            }
    }

    fun deleteRecipe(activity: RecipeDetailActivity, recipe: Recipe){
        mFirestore.collection(Constants.RECIPE)
            .document(recipe.recipeId)
            .delete()
            .addOnSuccessListener {
                Log.d("delete recipe", "onSuccess: firebase delete")
                if(recipe.recipeImage!="")
                    deleteImageAtStorage(activity, recipe.recipeImage)
                else
                    activity.deleteSuccess()
            }
            .addOnFailureListener { e ->
                activity.deleteFailure()
                Log.w(TAG, "Error deleting document", e)
            }

    }

    private fun deleteImageAtStorage(activity: RecipeDetailActivity, mImageUrl: String){
        val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(mImageUrl)
        photoRef.delete().addOnSuccessListener {
            Log.d("delete image", "onSuccess: firebase delete")
            activity.deleteSuccess()
        }.addOnFailureListener { e->
            Log.w(TAG, "Error deleting document", e)
        }
    }
}