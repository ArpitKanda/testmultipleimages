package com.example.bflmultipleimages

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ImageListActivity : AppCompatActivity() {

    var context: Context? = null
    var mutliImageAdapter: MultimageAdapter? = null
    var recyclerViewImage: RecyclerView? = null
    var btnCreatePdf: Button? = null
    var llbutton:LinearLayout?=null
    var btnDel:Button?=null
    var btnCreateImage:Button?=null
    var imageGet: String? = null
    var currentAnimator: Animator? = null
    var shortAnimationDuration = 0
    var bitmaplist: MutableList<Bitmap> = java.util.ArrayList()
    var list: MutableList<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iamge_list)
        context = this
        btnDel = findViewById(R.id.btnDel)
        btnCreateImage=findViewById(R.id.btnCreateImage)
        recyclerViewImage = findViewById(R.id.recyclerViewImage)
        btnCreatePdf = findViewById(R.id.btnCreatePdf)
        llbutton=findViewById(R.id.llbutton)
        shortAnimationDuration = resources.getInteger(
            android.R.integer.config_shortAnimTime
        )
//        if (list!=null){
            list?.clear()
//        }
        list=MyUtility.getFavoriteList(this)!!
        for (i in list!!.indices) {
            val bitmap = list!![i]
            bitmaplist.add(BitmapFactory.decodeFile(bitmap))
        }
        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mutliImageAdapter = MultimageAdapter(context as ImageListActivity, list)
        recyclerViewImage?.setLayoutManager(gridLayoutManager)
        recyclerViewImage?.setAdapter(mutliImageAdapter)
        if (list!!.size > 0) {
            changePosition()
        }
        btnCreateImage?.setOnClickListener {
            val result = Bitmap.createBitmap(
                bitmaplist[0]!!.width,
                bitmaplist[0]!!.height * bitmaplist.size,
                Bitmap.Config.ARGB_8888
            )
            val paint = Paint()
            val canvas = Canvas(result)
            for (i in bitmaplist.indices) {
                if (i == 0) {
                    canvas.drawBitmap(bitmaplist[i]!!, 0f, 0f, paint)
                } else {
                    canvas.drawBitmap(bitmaplist[i]!!, 0f, (bitmaplist[0]!!.height * i).toFloat(), paint)
                }
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
                reqH = width * bitmaplist.get(i)!!.getHeight() / bitmaplist.get(i)!!.getWidth()
                if (reqH < height) {
                } else {
                    reqH = height
                    reqW = height * bitmaplist.get(i)!!.getWidth() / bitmaplist.get(i)!!.getHeight()
                }
                val pageInfo: PageInfo =
                    PageInfo.Builder(bitmaplist.get(i)!!.getWidth(), bitmaplist.get(i)!!.getHeight(), 1)
                        .create()
                val page: PdfDocument.Page = document.startPage(pageInfo)
                val canvas: Canvas = page.getCanvas()
                canvas.drawBitmap((bitmaplist.get(i))!!, 0f, 0f, null)
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


    fun getOutputMediaFile(type: Int): File? {
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


    fun changePosition() {
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

}