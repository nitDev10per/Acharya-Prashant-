package com.example.acharyaprashant

import ImageLoader
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageGridAdapter(private val context: Context, private var images: List<MediaCoverage>,
                       private var bitMapList: Map<String,Bitmap>,
                       var callback: (Int, Boolean) -> Unit) :
    RecyclerView.Adapter<ImageGridAdapter.ViewHolder>() {

    private val imageLoader = ImageLoader(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = images[position]
        val imageUrl = "${image.thumbnail.domain}/${image.thumbnail.basePath}/0/${image.thumbnail.key}"
        val bitmap = bitMapList.get(imageUrl)

        bitmap?.let {
            imageLoader.loadImage(imageUrl, holder.imageView,bitmap , callback, position)
        } ?: run {
            imageLoader.loadImage(imageUrl, holder.imageView, null, callback, position)
        }

    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setImages(images: List<MediaCoverage>) {
        this.images = images
        notifyDataSetChanged()
    }
    fun setImagesBitmap(bitMapList: Map<String,Bitmap>){
        this.bitMapList = bitMapList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }


}
