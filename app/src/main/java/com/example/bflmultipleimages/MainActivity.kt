package com.example.bflmultipleimages

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
//import com.example.bflmultipleimages.MyUtility.getFavoriteList
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import android.provider.MediaStore
//import com.example.bflmultipleimages.imagepdf.ImageListActivity
//import com.example.bflmultipleimages.imagepdf.MyUtility
//import com.example.bflmultipleimages.imagepdf.SharedPref
import com.google.gson.Gson
import java.text.SimpleDateFormat
class MainActivity : AppCompatActivity() {
    var btnTake: Button? = null
    var btnSelect: Button? = null
    private var btn_gray: Button? = null
    var ivShow: ImageView? = null
    var photoFile: File? = null
    var imagePath:String?=null;
    private var bitmap_gray: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        btnTake = findViewById<View>(R.id.btn_take) as Button
        btn_gray = findViewById(R.id.btn_gray)
        btnSelect = findViewById<View>(R.id.btn_select) as Button
        ivShow = findViewById<View>(R.id.iv_show) as ImageView
        btnTake!!.setOnClickListener {
            startActivityForResult(
                CropActivity.getJumpIntent(
                    this@MainActivity,
                    false,
                    photoFile
                ), 100
            )
        }
        btnSelect!!.setOnClickListener {
            startActivityForResult(
                CropActivity.getJumpIntent(
                    this@MainActivity,
                    true,
                    photoFile
                ), 100
            )
        }
        btn_gray?.setOnClickListener{
            if(MyUtility.getFavoriteList(this)!=null && MyUtility.getFavoriteList(this)!!.isNotEmpty()) {
                ivShow!!.setImageResource(R.drawable.ic_baseline_image_24)
                val intent = Intent(this, ImageListActivity::class.java)
                startActivity(intent)
            }else{
                showToast("No Images Found")
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 &&  photoFile!!.exists()) {
            val bitmap = BitmapFactory.decodeFile(photoFile?.path)
            bitmap_gray = bitmap
            ivShow?.setImageBitmap(bitmap_gray)
            Log.e("##Saved ",photoFile?.path!!)
            MyUtility.addFavoriteItem(this,  photoFile!!.path)
            Log.e("##Photo Path ", Gson().toJson(MyUtility.getFavoriteList(this)).toString().substring(
                2, Gson().toJson(
                    MyUtility.getFavoriteList(this)
                ).toString().length - 2))

        }
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }
    private fun checkPermission() {
        val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }
    }

    override fun onResume() {
        super.onResume()
        photoFile = Utils.getOutputMediaFile(1,"ScanImage")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        var byteArray: ByteArray? = null
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }
}