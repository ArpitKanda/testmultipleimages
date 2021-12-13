package com.example.bflmultipleimages

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import com.example.bflmultipleimages.ImageCompress.compressImage
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.graphics.Bitmap
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.util.Log
import java.lang.Exception
import android.widget.Toast
import kotlin.Throws
import java.io.IOException
import android.graphics.BitmapFactory
import android.content.res.AssetFileDescriptor
import androidx.core.app.ActivityCompat
import java.io.File
import android.os.Environment
import java.text.SimpleDateFormat
import java.util.*

class MultiImageActivity : AppCompatActivity() {
    var btnAdd: Button? = null
    var btnPdf: Button? = null
    var ivMulti: ImageView? = null
    var context: Context? = null
    var bitmapList: MutableList<Bitmap?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multi_image)
        context = this
        findviews()
        initviews()
    }

    private fun initviews() {
        bitmapList = ArrayList()
        btnAdd!!.setOnClickListener { checkPermission() }
        btnPdf!!.setOnClickListener {
            if ((bitmapList as ArrayList<Bitmap?>).size > 0) {
                startActivity(Intent(context, ImageListActivity::class.java))
            }
        }
    }

    private fun findviews() {
        btnAdd = findViewById(R.id.btnAdd)
        btnPdf = findViewById(R.id.btnpdf)
        ivMulti = findViewById(R.id.ivMulti)
    }

    var galleryUri: Uri? = null
    private fun openGallaryIntent() {
        val options = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Camera") {
                if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    } else {
                        TODO("VERSION.SDK_INT < M")
                    }
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(arrayOf(Manifest.permission.CAMERA), 300)
                    }
                } else {
                    val fileName = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + ".jpg"
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    galleryUri = FileProvider.getUriForFile(
                        Objects.requireNonNull(
                            applicationContext
                        ),
                        BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(1)!!
                    )
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, galleryUri)
                    startActivityForResult(intent, 300)
                }
            } else if (options[item] == "Gallery") {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 400)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 300) {
                //Currently Camera Code is Not Working
            } else if (requestCode == 400) {
                val uri = data!!.data
                Log.e("#URI_1 ", uri.toString())
                val selectedImage = data.data
                compressImage(ivMulti!!, context!!, selectedImage!!)
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(selectedImage, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val picturePath = cursor.getString(columnIndex)
                val bitmap = DataBridge.FINAL_IMAGE
                //                ivMulti.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                bitmapList!!.add(bitmap)
                cursor.close()
            }
        } catch (e: Exception) {
            Log.e("##MultiImage ", e.toString())
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun getBitmap(selectedimg: Uri): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 3
        var fileDescriptor: AssetFileDescriptor? = null
        fileDescriptor = contentResolver.openAssetFileDescriptor(selectedimg, "r")
        return BitmapFactory.decodeFileDescriptor(
            fileDescriptor!!.fileDescriptor, null, options
        )
    }

    fun addMultiImages(bitmap: Bitmap) {
        val bitmapArrayList = ArrayList<Bitmap>()
        bitmapArrayList.add(bitmap)
    }

    private fun checkPermission() {
        val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (!hasPermissions(this, *PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        } else {
            openGallaryIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun getOutputMediaFile(type: Int): File? {
            val mediaStorageDir = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ), "Mutli Images"
            )
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e("BrillentWellness", "failed to create directory")
                    return null
                }
            }
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val mediaFile: File
            mediaFile = if (type == 1) {
                File(
                    mediaStorageDir.path + File.separator +
                            "IMG_" + timeStamp + ".jpg"
                )
            } else if (type == 2) {
                File(
                    mediaStorageDir.path + File.separator +
                            "VID_" + timeStamp + ".mp4"
                )
            } else {
                return null
            }
            return mediaFile
        }

        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
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

        fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
            val cursor = context.contentResolver.query(contentURI, null, null, null, null)
            return if (cursor == null) {
                contentURI.path
            } else {
                cursor.moveToFirst()
                val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                cursor.getString(index)
            }
        }
    }
}