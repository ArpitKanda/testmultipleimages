package com.scan.testmultipleimages

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import java.util.*

class ViewImageActivity : AppCompatActivity() {
    private val context: Context? = null
    private var ivDoc: ImageView? = null
    private var imagepath: String? = null
    var btnDelete: Button? = null
    var position = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        findviews()
        initview()
    }

    private fun findviews() {
        ivDoc = findViewById(R.id.ivDoc)
        btnDelete = findViewById(R.id.btnDelte)
    }


    private fun initview() {
        imagepath = intent.extras!!.getString("image")
        val intent = intent
        val args = intent.getBundleExtra("BUNDLE")
        val `object` = args!!.getSerializable("ARRAYLIST") as ArrayList<Any?>?
        position = intent.extras!!.getInt("position")
        ivDoc?.setImageBitmap(BitmapFactory.decodeFile(imagepath))
        val bitmap = BitmapFactory.decodeFile(imagepath)
        btnDelete!!.setOnClickListener {
            alertBox(this,`object`)
        }
    }



    fun alertBox(activity: Activity?,list:ArrayList<Any?>?){
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Remove Picture?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes"){dialogInterface , which->
            list?.removeAt(position)
            val joined = TextUtils.join(",", list!!)
            if(joined.equals("")){
                MyUtility.putStringInPreferences(this@ViewImageActivity, null, "favorites")
            }else{
                MyUtility.putStringInPreferences(this@ViewImageActivity, joined, "favorites")
            }
            finish()
        }
        builder.setNegativeButton("No"){dialogInterface, which ->

        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}