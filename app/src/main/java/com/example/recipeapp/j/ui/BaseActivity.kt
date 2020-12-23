package com.example.recipeapp.j.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recipeapp.R
import kotlinx.android.synthetic.main.progress_dialog.*

open class BaseActivity : AppCompatActivity() {
    private lateinit var progressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgress(text: String) {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.progress_textView.text = text
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }

    fun hideProgress() {
        progressDialog.dismiss()
    }

    fun setConfirmDialog(activity: Activity, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes") { _ , _ ->
                when(activity){
                    is CreateRecipe -> {
                        finish()
                    }
                    is RecipeDetailActivity -> {
                        activity.deleteRecipe()
                    }
                }
            }
            .setNegativeButton("CANCEL") { dialog, _ ->

                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

}