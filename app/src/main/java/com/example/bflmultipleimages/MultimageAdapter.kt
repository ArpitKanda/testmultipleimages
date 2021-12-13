package com.example.bflmultipleimages

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import java.io.File

class MultimageAdapter(var context: Context, var list: MutableList<String>?) :
    RecyclerView.Adapter<MultimageAdapter.MultiImageHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultiImageHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapter_image_list, parent, false)
        return MultiImageHolder(view)
    }

    override fun onBindViewHolder(holder: MultiImageHolder, position: Int) {
        val data = list?.get(position)
        val bitmap = BitmapFactory.decodeFile(data)
        holder.ivList.setImageBitmap(bitmap)
        Log.e("##All Items ",TextUtils.join(",", list!!))
        val imageListActivity = context as ImageListActivity
        imageListActivity.btnDel!!.setOnClickListener {
            list?.removeAt(position)
            val joined = TextUtils.join(",", list!!)
            Log.e("##Remove Items ",joined)
            MyUtility.putStringInPreferences((context as Activity), joined,"favorites")
            //                SharedPref.save((Activity) context,"pqrs",joined);
            //                String path=Environment.getExternalStoragePublicDirectory(
            //                        Environment.DIRECTORY_DOWNLOADS)+ "/Compress Image/";
            //                File file=new File(data);
            //                String filename= file.getName();
            //                String finalPath=path+filename;
            //
            //
            //
            //                SharedPref.clearData((Activity) context,"pqrs");
            ////                delete(context, new File(data));
            //                Log.e("##File Path ",path+"\n"+filename+"\n"+finalPath);
            //
            //
            //                if (delete(context, new File(finalPath))){
            //                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            //                }else{
            //                    Toast.makeText(context, "Not Deleted", Toast.LENGTH_SHORT).show();
            //                }
        }
        holder.ivList.setOnClickListener {
//            Utils.showExpandableImage(context, data, holder.ivList, list, position)
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/ScanImage/"
            val file = File(data)
            val filename = file.name
            val finalPath = path + filename
            val fdelete = File(finalPath)
            Log.e("#file :", finalPath)
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    Log.e("#file Deleted :", finalPath)
                } else {
                    Log.e("#file not Deleted :", finalPath)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    inner class MultiImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivList: ImageView
        var imageView: ImageView
        var cardContainer: CardView

        init {
            ivList = itemView.findViewById(R.id.ivList)
            imageView = itemView.findViewById(R.id.expanded_image)
            cardContainer = itemView.findViewById(R.id.cardContainer)
        }
    }

    companion object {
        fun delete(context: Context, file: File): Boolean {
            val where = MediaStore.MediaColumns.DATA + "=?"
            val selectionArgs = arrayOf(
                file.absolutePath
            )
            val contentResolver = context.contentResolver
            val filesUri = MediaStore.Files.getContentUri("external")
            contentResolver.delete(filesUri, where, selectionArgs)
            if (file.exists()) {
                contentResolver.delete(filesUri, where, selectionArgs)
            }
            return !file.exists()
        }
    }
}
