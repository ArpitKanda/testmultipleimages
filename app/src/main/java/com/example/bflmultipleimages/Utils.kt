package com.example.bflmultipleimages

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.util.Pair
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import java.io.File
import java.io.Serializable
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun getOutputMediaFile(type: Int,path:String): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), path
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("BrillentWellness", "failed to create directory")
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File
        if (type == 1) {
            mediaFile = File(
                mediaStorageDir.path + File.separator +
                        "IMG_" + timeStamp + ".jpg"
            )
        } else if (type == 2) {
            mediaFile = File(
                (mediaStorageDir.path + File.separator +
                        "VID_" + timeStamp + ".mp4")
            )
        } else {
            return null
        }
        return mediaFile
    }


    fun showExpandableImage(
        context: Context?,
        imageUri: String?,
        view: View,
        `object`: List<String?>?,
        position: Int
    ) {
        var imageUri = imageUri
        try {
            val intent = Intent(context, ViewImageActivity::class.java)
            intent.putExtra("image", imageUri)
            val args = Bundle()
            args.putSerializable("ARRAYLIST", `object` as Serializable?)
            intent.putExtra("BUNDLE", args)
            intent.putExtra("position", position)
            if (TextUtils.isEmpty(imageUri)) {
                imageUri = getURLForResource(R.drawable.null_profile) // default image in drawable
            }
            val bodyPair = Pair.create(view, imageUri)
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation((context as Activity?)!!)
            ActivityCompat.startActivity(context!!, intent, options.toBundle())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getURLForResource(resourceId: Int): String? {
        return Uri.parse("android.resource://com.example.bflmultipleimages/$resourceId").toString()
    }


}