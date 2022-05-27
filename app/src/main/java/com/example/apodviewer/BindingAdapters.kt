package com.example.apodviewer

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.apodviewer.database.PodItem
import com.example.apodviewer.gallery.PhotoGridAdapter
import com.example.apodviewer.gallery.PodApiStatus

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<PodItem>?) {
    val adapter = recyclerView.adapter as PhotoGridAdapter
    adapter.submitList(data)
}

@BindingAdapter("thumbnail")
fun bindThumbnail(imgView: ImageView, imgUrl: String) {
    val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
    Glide.with(imgView.context)
        .load(imgUri)
        .apply(
            RequestOptions()
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image))
        .into(imgView)
}

@BindingAdapter("imgSrc")
fun bindImage(imgView: ImageView, imgUrl: String) {
    val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
    Glide.with(imgView.context)
        .load(imgUri)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.ic_image_wait)
                .error(R.drawable.ic_broken_image))
        .into(imgView)
}

@BindingAdapter("podApiStatus")
fun bindStatus(statusImageView: ImageView, status: PodApiStatus?) {
    when (status) {
        PodApiStatus.LOADING -> {
            statusImageView.setImageResource(R.drawable.loading_animation)
            statusImageView.visibility = View.VISIBLE
        }
        PodApiStatus.DONE, PodApiStatus.ERROR -> {
            statusImageView.visibility = View.GONE
        }
        PodApiStatus.FATAL -> {
            statusImageView.setImageResource(R.drawable.ic_connection_error)
            statusImageView.visibility = View.VISIBLE
        }
        else -> {

        }
    }
}