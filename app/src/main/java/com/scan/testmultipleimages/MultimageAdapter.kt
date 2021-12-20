package com.scan.testmultipleimages


import android.content.Context
import android.graphics.BitmapFactory
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

        holder.ivList.setOnClickListener {
            Utils.showExpandableImage(context, data, holder.ivList, list, position)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    inner class MultiImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivList: ImageView = itemView.findViewById(R.id.ivList)
        var imageView: ImageView = itemView.findViewById(R.id.expanded_image)
        var cardContainer: CardView = itemView.findViewById(R.id.cardContainer)

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
