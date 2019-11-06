package eu.mobilebear.flickr.presentation.gallery.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.mobilebear.flickr.R
import eu.mobilebear.flickr.domain.model.PhotoModel
import eu.mobilebear.flickr.presentation.gallery.view.PhotoModelOnClickListener
import kotlinx.android.synthetic.main.item_photo.view.*
import javax.inject.Inject

class PhotoModelAdapter @Inject constructor() : ListAdapter<PhotoModel, PhotoViewHolder>(DIFFER) {

    private lateinit var photoModelOnClickListener: PhotoModelOnClickListener

    fun setListener(photoModelOnClickListener: PhotoModelOnClickListener) {
        this.photoModelOnClickListener = photoModelOnClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view, photoModelOnClickListener)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFFER = object : DiffUtil.ItemCallback<PhotoModel>() {
            override fun areItemsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: PhotoModel, newItem: PhotoModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class PhotoViewHolder(val view: View, private val photoModelOnClickListener: PhotoModelOnClickListener) : RecyclerView.ViewHolder(view) {

    fun bind(photo: PhotoModel) {
        view.title.text = photo.title
        view.author.text = photo.author
        view.tags.text = photo.tags
        view.publishedDate.text = photo.publishedDate
        view.takenDate.text = photo.takenDate
        view.websiteLink.text = photo.link

        Glide.with(view.context).load(photo.mediaUrl).into(view.image)
        view.image.setOnClickListener { photoModelOnClickListener.onPhotoClicked(photo.mediaUrl) }
    }
}
