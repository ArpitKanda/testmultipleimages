package com.example.bflmultipleimages

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.ImageView
import java.util.ArrayList

class ViewImageActivity : AppCompatActivity() {
    private val context: Context? = null
    private var ivDoc: TouchImageView? = null
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
            `object`!!.removeAt(position)
            val joined = TextUtils.join(",", `object`)
            if(joined.equals("")){
                MyUtility.putStringInPreferences(this@ViewImageActivity, null, "favorites")
            }else{
                MyUtility.putStringInPreferences(this@ViewImageActivity, joined, "favorites")
            }


        finish()
        }
    }
}