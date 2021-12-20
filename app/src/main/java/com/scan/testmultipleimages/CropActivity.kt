package com.scan.testmultipleimages

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

import me.pqpo.smartcropperlib.view.CropImageView
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CropActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    var ivCrop: CropImageView? = null
    var btnCancel: Button? = null
    var btnOk: Button? = null
    var mFromAlbum = false
    var mCroppedFile: File? = null
    var tempFile: File? = null
    var imgRoted:ImageView?=null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        ivCrop = findViewById<View>(R.id.iv_crop) as CropImageView?
        btnCancel = findViewById<View>(R.id.btn_cancel) as Button?
        btnOk = findViewById<View>(R.id.btn_ok) as Button?
        imgRoted=findViewById<View>(R.id.imgRoted) as ImageView?

        imgRoted?.setOnClickListener {
            //ivCrop?.rotationX=90F
            ivCrop?.rotation=90F
        }
        btnCancel?.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btnOk?.setOnClickListener {
            if (ivCrop!!.canRightCrop()) {
                val crop: Bitmap = ivCrop!!.crop()
                mCroppedFile?.let { saveImage(crop, it) }
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this@CropActivity, "cannot crop correctly", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        mFromAlbum = intent.getBooleanExtra(EXTRA_FROM_ALBUM, true)
        mCroppedFile = intent.getSerializableExtra(EXTRA_CROPPED_FILE) as File
        if (mCroppedFile == null) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
        tempFile= Utils.getOutputMediaFile(1, "CropImages")
//        tempFile = File(getExternalFilesDir("img"), "temp.jpg")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EasyPermissions.requestPermissions(
                this@CropActivity,
                "申请权限",
                0,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        } else {
            selectPhoto()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        selectPhoto()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }


    private fun selectPhoto() {
        if (mFromAlbum) {
            val selectIntent = Intent(Intent.ACTION_PICK)
            selectIntent.type = "image/*"
            if (selectIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(selectIntent, REQUEST_CODE_SELECT_ALBUM)
            }
        } else {
            val startCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "com.scan.testmultipleimages.fileProvider", tempFile!!)
            } else {
                Uri.fromFile(tempFile)
            }
            startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            if (startCameraIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }
        var selectedBitmap: Bitmap? = null
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && tempFile!!.exists()) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(tempFile?.path, options)
            options.inJustDecodeBounds = false
            options.inSampleSize = calculateSampleSize(options)
            selectedBitmap = BitmapFactory.decodeFile(tempFile?.path, options)
            Log.e("##CropPath ", tempFile?.path.toString())
        } else if (requestCode == REQUEST_CODE_SELECT_ALBUM && data != null && data.data != null) {
            val cr: ContentResolver = contentResolver
            val bmpUri: Uri = data.data!!
            try {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(cr.openInputStream(bmpUri), Rect(), options)
                options.inJustDecodeBounds = false
                options.inSampleSize = calculateSampleSize(options)
                selectedBitmap =
                    BitmapFactory.decodeStream(cr.openInputStream(bmpUri), Rect(), options)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        if (selectedBitmap != null) {
            ivCrop?.setImageToCrop(selectedBitmap)
        }
    }
    private fun saveImage(bitmap: Bitmap, saveFile: File) {
        try {
            val fos = FileOutputStream(saveFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("##Save Error ",e.toString());
        }
    }

    private fun calculateSampleSize(options: BitmapFactory.Options): Int {
        val outHeight: Int = options.outHeight
        val outWidth: Int = options.outWidth
        var sampleSize = 1
        val destHeight = 1000
        val destWidth = 1000
        if (outHeight > destHeight || outWidth > destHeight) {
            sampleSize = if (outHeight > outWidth) {
                outHeight / destHeight
            } else {
                outWidth / destWidth
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1
        }
        return sampleSize
    }

    companion object {
        private const val EXTRA_FROM_ALBUM = "extra_from_album"
        private const val EXTRA_CROPPED_FILE = "extra_cropped_file"
        private const val REQUEST_CODE_TAKE_PHOTO = 100
        private const val REQUEST_CODE_SELECT_ALBUM = 200
        @JvmStatic
        fun getJumpIntent(context: Context?, fromAlbum: Boolean, croppedFile: File?): Intent {
            val intent = Intent(context, CropActivity::class.java)
            intent.putExtra(EXTRA_FROM_ALBUM, fromAlbum)
            intent.putExtra(EXTRA_CROPPED_FILE, croppedFile)
            return intent
        }
    }
}