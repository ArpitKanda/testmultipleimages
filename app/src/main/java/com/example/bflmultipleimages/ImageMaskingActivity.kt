package com.example.bflmultipleimages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.io.File
import kotlin.math.roundToInt
class ImageMaskingActivity : AppCompatActivity() {
    private var bitmap_gray: Bitmap? = null
    private var btn_save:Button?=null
    var ivShow: ImageView? = null
    private val PERMISSION_REQUEST_CODE = 117
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_macking)
        ivShow = findViewById(R.id.ivMask)
        btn_save=findViewById(R.id.btn_save);

        if (checkPermission1()) {
            //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        var bitmap: Bitmap? = null
       // if (intent.hasExtra("image")) {
            val byteArray = MainActivity.byteArray //intent.getByteArrayExtra("image")
            if (byteArray != null) {
                bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                ivShow?.setImageBitmap(bitmap)
                bitmap_gray=bitmap

            }
//        }
        btn_save?.setOnClickListener {
//            bitmap_gray?.let { generatePDF() }
            val path: String =
                MediaStore.Images.Media.insertImage(contentResolver, bitmap_gray, "Title", null)
            val selectedImage = Uri.parse(path)
            compressImage(this,selectedImage)

            Toast.makeText(this,"Image file generated successfully. Please check your download folder",Toast.LENGTH_SHORT).show()

        }
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }


    private fun checkPermission1(): Boolean {
        // checking of permissions.
        val permission1 =
            ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        val permission2 =
            ContextCompat.checkSelfPermission(applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }


    private fun generatePDF() {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val mypageInfo = bitmap_gray?.width?.let { bitmap_gray?.height?.let { it1 ->
            PdfDocument.PageInfo.Builder(it,
                it1, 1).create()
        } }
        val myPage = pdfDocument.startPage(mypageInfo)
        val canvas = myPage.canvas
        bitmap_gray=bitmap_gray?.let { compressBitmap(it,18) }
        bitmap_gray?.let { canvas.drawBitmap(it, 0F, 0F, paint) }
        pdfDocument.finishPage(myPage)
       //val file = File(Environment.getExternalStorageDirectory(), "BFL_DOC.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(getOutputMediaFile(1)))

            Toast.makeText(
                this,
                "PDF file generated successfully. Please check your file manager",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        pdfDocument.close()
    }

    private fun compressBitmap(bitmap:Bitmap, quality:Int):Bitmap{
        // Initialize a new ByteArrayStream
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        val byteArray = stream.toByteArray()
        // Finally, return the compressed bitmap
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

//    fun getOutputMediaFile(type: Int): File? {
//        val mediaStorageDir = File(
//            Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOWNLOADS
//            ), "Compress PDF"
//        )
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.e("BrillentWellness", "failed to create directory")
//                return null
//            }
//        }
//        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val mediaFile: File = if (type == 1) {
//            File(
//                mediaStorageDir.path + File.separator +
//                        "PDF_" + timeStamp + ".pdf"
//            )
//        } else if (type == 2) {
//            File(
//                (mediaStorageDir.path + File.separator +
//                        "VID_" + timeStamp + ".mp4")
//            )
//        } else {
//            return null
//        }
//        return mediaFile
//    }



    /// Convert img

    fun compressImage(context: Context, imageUri: Uri): String? {
        val filePath = getRealPathFromURI(context, imageUri)
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(filePath, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth

//      max Height and width values of the compressed image is taken as 816x612
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            when {
                imgRatio < maxRatio -> {
                    imgRatio = maxHeight / actualHeight
                    actualWidth = (imgRatio * actualWidth).toInt()
                    actualHeight = maxHeight.toInt()
                }
                imgRatio > maxRatio -> {
                    imgRatio = maxWidth / actualWidth
                    actualHeight = (imgRatio * actualHeight).toInt()
                    actualWidth = maxWidth.toInt()
                }
                else -> {
                    actualHeight = maxHeight.toInt()
                    actualWidth = maxWidth.toInt()
                }
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options)
            //            ivCompress.setImageBitmap(bmp);
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
            Log.e("##Press 1 ", exception.toString())
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
            Log.e("##Press 2 ", exception.toString())
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(
            bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG)
        )

//      check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            exif = ExifInterface(filePath!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0
            )
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            when (orientation) {
                6 -> {
                    matrix.postRotate(90f)
                    Log.d("EXIF", "Exif: $orientation")
                }
                3 -> {
                    matrix.postRotate(180f)
                    Log.d("EXIF", "Exif: $orientation")
                }
                8 -> {
                    matrix.postRotate(270f)
                    Log.d("EXIF", "Exif: $orientation")
                }
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("##Press 3 ", e.toString())
        }
        var out: FileOutputStream? = null
        val filename = getFilename()
        filename?.let { Log.e("##Compress Path ", it) }
        try {
            out = FileOutputStream(filename)

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Log.e("##Press 4 ", e.toString())
        }
        return filename
    }

    fun getFilename(): String? {
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), "BFLMaskingToolIMAGE"
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    private fun getRealPathFromURI(context: Context, contentURI: Uri): String? {
        val contentUri = contentURI
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            return contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(index)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), "Compress Image"
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("BrillentWellness", "failed to create directory")
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val mediaFile: File = when (type) {
            1 -> {
                File(
                    mediaStorageDir.path + File.separator +
                            "IMG_" + timeStamp + ".jpg"
                )
            }
            2 -> {
                File(
                    (mediaStorageDir.path + File.separator +
                            "VID_" + timeStamp + ".mp4")
                )
            }
            else -> {
                return null
            }
        }
        return mediaFile
    }


}