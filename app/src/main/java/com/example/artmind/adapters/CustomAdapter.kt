package com.example.artmind.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artmind.R
import com.example.artmind.interfaces.ImageDownloadListener
import com.example.artmind.interfaces.ImageLoadListener

class ImageViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.image_item_image_view)
    private val downloadButton: ImageButton = itemView.findViewById(R.id.image_download_button)
    private var imageUrl: String? = null
    private val imageDownloadListener: ImageDownloadListener = context as ImageDownloadListener
    private val imageLoadListener: ImageLoadListener = context as ImageLoadListener

    fun bind(url: String) {
            imageUrl = url
            Glide.with(itemView)
                .load(imageUrl)
                .into(imageView)

            imageLoadListener.onImageLoad(imageUrl)

        downloadButton.setOnClickListener {
            imageDownloadListener.onImageDownload(imageUrl)
        }
    }
}

class CustomAdapter(private val imageUrls: ArrayList<String>,private val context: Context) : RecyclerView.Adapter<ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view,context)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    fun updateData(newData: ArrayList<String>) {
        imageUrls.clear()
        imageUrls.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}