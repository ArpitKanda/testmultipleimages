package com.scan.testmultipleimages

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.app.ProgressDialog
import android.content.Context
import android.graphics.*
import java.lang.Thread
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import java.io.FileOutputStream
import java.io.IOException
import android.widget.Toast
import java.io.File
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper

import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent

import android.provider.MediaStore

import android.content.DialogInterface
import android.net.Uri


class ImageListActivity : AppCompatActivity() {
    var context: Context? = null
    var recyclerViewImage: RecyclerView? = null
    private var btnCreatePdf: Button? = null
    var btnAddMore:Button?=null
    var llbutton:LinearLayout?=null
    var btnDel:Button?=null
    var btnCreateImage:Button?=null
    var imageGet: String? = null
    var currentAnimator: Animator? = null
    var shortAnimationDuration = 0
    var positionList: Int? =null
    var bitmaplist: MutableList<Bitmap> = java.util.ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iamge_list)
        context = this
        btnDel = findViewById(R.id.btnDel)
        btnCreateImage=findViewById(R.id.btnCreateImage)
        recyclerViewImage = findViewById(R.id.recyclerViewImage)
        btnCreatePdf = findViewById(R.id.btnCreatePdf)
        btnAddMore=findViewById(R.id.btnAddMore)
        llbutton=findViewById(R.id.llbutton)
        shortAnimationDuration = resources.getInteger(
            android.R.integer.config_shortAnimTime
        )

        if(list!=null && list!!.isNotEmpty()){
            list?.clear()
        }


        btnDel!!.setOnClickListener {
            if(positionList!=null ){
            positionList?.let { it1 -> list?.removeAt(it1) }
            val joined = TextUtils.join(",", list!!)
            Log.e("##Remove Items ",joined)
                if(joined.equals("")){
                    MyUtility.putStringInPreferences(
                        context as ImageListActivity,
                        null,
                        "favorites"
                    )
                }else{
                    MyUtility.putStringInPreferences(
                        context as ImageListActivity,
                        joined,
                        "favorites"
                    )
                }

        }
        }

        //resigerty pdf
        //naver bill
        //uarmil aadhar card
        ////bank pass book first pages
        //2 photo
        //form
        //100*

        btnCreateImage?.setOnClickListener {
            val result = Bitmap.createBitmap(
                bitmaplist[0]!!.width,
                bitmaplist[0]!!.height * bitmaplist.size,
                Bitmap.Config.ARGB_8888
            )
            val paint = Paint()
            val canvas = Canvas(result)
            for (i in bitmaplist.indices) {
                    canvas.drawBitmap(bitmaplist[i]!!, 0f, (bitmaplist[0]!!.height * i+10).toFloat(), paint)
            }
            var os: OutputStream? = null
            try {
                os = FileOutputStream(ImageCompress.getOutputMediaFile(1))
                result.compress(Bitmap.CompressFormat.JPEG, 100, os)
                Toast.makeText(this@ImageListActivity, "Image Genrated", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("##MultiImages ", e.toString())
            }
        }
        btnCreatePdf?.setOnClickListener(View.OnClickListener {
            createPDF()
        })

        btnAddMore?.setOnClickListener {
            finish()
        }
    }

//    private fun selectImage() {
//        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Add Photo!")
//        builder.setItems(options) { dialog, item ->
//            if (options[item] == "Take Photo") {
//                startActivityForResult(
//                    CropActivity.getJumpIntent(
//                        this@MainActivity,
//                        false,
//                        photoFile
//                    ), 100
//                )
//            } else if (options[item] == "Choose from Gallery") {
//                val intent = Intent(
//                    Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                )
//                startActivityForResult(intent, 2)
//            } else if (options[item] == "Cancel") {
//                dialog.dismiss()
//            }
//        }
//        builder.show()
//    }


    companion object{
        var list: MutableList<String>? = null
        var mutliImageAdapter: MultimageAdapter? = null
        fun deleteImages(position: Int, context: ImageListActivity) {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Remove Picture?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Yes"){dialogInterface , which->
                list?.removeAt(position)
                val joined = TextUtils.join(",", list!!)
                if(joined.equals("")){
                    MyUtility.putStringInPreferences(context, null, "favorites")
                    context.finish()
                }else{
                    MyUtility.putStringInPreferences(context, joined, "favorites")
                }
                mutliImageAdapter?.notifyDataSetChanged()
            }
            builder.setNegativeButton("No"){dialogInterface, which ->

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        if(MyUtility.getFavoriteList(this) !=null && MyUtility.getFavoriteList(this)!!.isNotEmpty()) {
            list = MyUtility.getFavoriteList(this)!!
            bitmaplist.isEmpty()
//            bitmaplist.clear()
                for (i in list!!.indices) {
                    val bitmap = list!![i]
                    bitmaplist.add(BitmapFactory.decodeFile(bitmap))

            }
            val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            mutliImageAdapter = MultimageAdapter(context as ImageListActivity, list)
            recyclerViewImage?.layoutManager = gridLayoutManager
            recyclerViewImage?.adapter = mutliImageAdapter
            mutliImageAdapter!!.notifyDataSetChanged()
            if (list!!.size > 0) {
                changePosition()
            }
        }else{
            recyclerViewImage=null
            mutliImageAdapter!!.notifyDataSetChanged()
            finish()
        }
    }

    private fun createPDF(){
        val dialog = ProgressDialog.show(this, "", "Generating PDF...")
        dialog.show()
        Thread {
            val document: PdfDocument = PdfDocument()
            val height: Int = 1010
            val width: Int = 714
            var reqH: Int
            var reqW: Int
            reqW = width
            for (i in bitmaplist.indices) {
                reqH = width * bitmaplist[i]!!.height / bitmaplist[i]!!.width
                if (reqH < height) {
                } else {
                    reqH = height
                    reqW = height * bitmaplist[i]!!.width / bitmaplist[i]!!.height
                }
                val pageInfo: PageInfo =
                    PageInfo.Builder(bitmaplist[i]!!.width, bitmaplist[i]!!.height, 1)
                        .create()
                val page: PdfDocument.Page = document.startPage(pageInfo)
                val canvas: Canvas = page.canvas
                canvas.drawBitmap((bitmaplist[i])!!, 0f, 0f, null)
                document.finishPage(page)
            }
            val fos: FileOutputStream
            try {
                fos = FileOutputStream(getOutputMediaFile(1))
                document.writeTo(fos)
                document.close()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            runOnUiThread(Runnable {
                dialog.dismiss()
                Toast.makeText(this@ImageListActivity, "PDF Genrated", Toast.LENGTH_SHORT).show()
            })
        }.start()
    }


    private fun getOutputMediaFile(type: Int): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ), "Mutli PDF"
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
                        "IMG_" + timeStamp + ".PDF"
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


    private fun changePosition() {
        val _ithCallback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                Collections.swap(list, viewHolder.adapterPosition, target.adapterPosition)
                mutliImageAdapter!!.notifyItemMoved(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //TODO
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeFlag(
                    ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
                )
            }
        }
        val ith = ItemTouchHelper(_ithCallback)
        ith.attachToRecyclerView(recyclerViewImage)
    }


    fun zoomImageFromThumb(thumbView: View, bitmap: Bitmap?,pos:Int) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        positionList=pos
        recyclerViewImage!!.visibility = View.GONE
        llbutton!!.visibility = View.GONE
        btnDel?.visibility = View.VISIBLE
        if (currentAnimator != null) {
            currentAnimator?.cancel()
            recyclerViewImage!!.visibility = View.VISIBLE
            llbutton!!.visibility = View.VISIBLE
            btnDel?.visibility = View.GONE
        }

        // Load the high-resolution "zoomed-in" image.
        val imageView = findViewById<View>(
            R.id.expanded_image
        ) as ImageView
        imageView.setImageBitmap(bitmap)

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        val startBounds = Rect()
        val finalBounds = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds)
        findViewById<View>(R.id.container)
            .getGlobalVisibleRect(finalBounds, globalOffset)
        startBounds.offset(-globalOffset.x, -globalOffset.y)
        finalBounds.offset(-globalOffset.x, -globalOffset.y)

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        val startScale: Float
        if (finalBounds.width().toFloat() / finalBounds.height()
            > startBounds.width().toFloat() / startBounds.height()
        ) {
            // Extend start bounds horizontally
            startScale = startBounds.height().toFloat() / finalBounds.height()
            val startWidth = startScale * finalBounds.width()
            val deltaWidth = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width().toFloat() / finalBounds.width()
            val startHeight = startScale * finalBounds.height()
            val deltaHeight = (startHeight - startBounds.height()) / 2
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.alpha = 0f
        imageView.visibility = View.VISIBLE

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        imageView.pivotX = 0f
        imageView.pivotY = 0f

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        val set = AnimatorSet()
        set
            .play(
                ObjectAnimator.ofFloat(
                    imageView, View.X,
                    startBounds.left.toFloat(), finalBounds.left.toFloat()
                )
            )
            .with(
                ObjectAnimator.ofFloat(
                    imageView, View.Y,
                    startBounds.top.toFloat(), finalBounds.top.toFloat()
                )
            )
            .with(
                ObjectAnimator.ofFloat(
                    imageView, View.SCALE_X,
                    startScale, 1f
                )
            )
            .with(
                ObjectAnimator.ofFloat(
                    imageView,
                    View.SCALE_Y, startScale, 1f
                )
            )
        set.duration = shortAnimationDuration.toLong()
        set.interpolator = DecelerateInterpolator()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                currentAnimator = null
            }

            override fun onAnimationCancel(animation: Animator) {
                currentAnimator = null
            }
        })
        set.start()
        currentAnimator = set

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        imageView.setOnClickListener {
            recyclerViewImage!!.visibility = View.VISIBLE
            llbutton!!.visibility = View.VISIBLE
            btnDel?.setVisibility(View.GONE)
            if (currentAnimator != null) {
                currentAnimator?.cancel()
                recyclerViewImage!!.visibility = View.VISIBLE
                llbutton!!.visibility = View.VISIBLE
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            val set = AnimatorSet()
            set.play(
                ObjectAnimator
                    .ofFloat(
                        imageView,
                        View.X,
                        startBounds.left.toFloat()
                    )
            )
                .with(
                    ObjectAnimator
                        .ofFloat(
                            imageView,
                            View.Y, startBounds.top.toFloat()
                        )
                )
                .with(
                    ObjectAnimator
                        .ofFloat(
                            imageView,
                            View.SCALE_X, startScale
                        )
                )
                .with(
                    ObjectAnimator
                        .ofFloat(
                            imageView,
                            View.SCALE_Y, startScale
                        )
                )
            set.duration = shortAnimationDuration.toLong()
            set.interpolator = DecelerateInterpolator()
            set.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    thumbView.alpha = 1f
                    imageView.visibility = View.GONE
                    currentAnimator = null
                }

                override fun onAnimationCancel(animation: Animator) {
                    thumbView.alpha = 1f
                    imageView.visibility = View.GONE
                    currentAnimator = null
                }
            })
            set.start()
            currentAnimator = set
        }
    }

}