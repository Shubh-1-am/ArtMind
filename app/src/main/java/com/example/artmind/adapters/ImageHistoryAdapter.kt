package com.example.artmind.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.artmind.R
import com.example.artmind.interfaces.ImageDownloadListener
import com.example.artmind.modal.ImageEntity

class ImageHistoryAdapter(private val images: ArrayList<ImageEntity>, private val context: Context) : RecyclerView.Adapter<ImageHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageHistoryViewHolder(view,context)
    }

    override fun onBindViewHolder(holder: ImageHistoryViewHolder, position: Int) {
        holder.bind(images[position])

    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateData(newData: List<ImageEntity>) {
        images.clear()
        images.addAll(newData)
        notifyDataSetChanged()
    }


}
 class ImageHistoryViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
    private val imageView: ImageView = itemView.findViewById(R.id.image_item_image_view)
    private val downloadButton: ImageButton = itemView.findViewById(R.id.image_download_button)
    private val imageDownloadListener: ImageDownloadListener = context as ImageDownloadListener
    fun bind(imageEntity: ImageEntity) {
        Glide.with(itemView)
            .load(imageEntity.imagePath)
            .into(imageView)
        downloadButton.setOnClickListener {
            imageDownloadListener.onImageDownload(imageEntity.imagePath)
        }
    }
}
