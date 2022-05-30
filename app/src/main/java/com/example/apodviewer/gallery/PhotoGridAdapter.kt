package com.example.apodviewer.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.apodviewer.database.PodItem
import com.example.apodviewer.databinding.PreviewElementBinding

class PhotoGridAdapter(private val onClickListener: OnClickListener ) :
    ListAdapter<PodItem, PhotoGridAdapter.PodItemViewHolder>(DiffCallback) {

    private val _lastBound = MutableLiveData<Boolean>()
    val lastBound: LiveData<Boolean>
            get() = _lastBound

    class PodItemViewHolder(private var binding: PreviewElementBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(podItem: PodItem) {
            if (podItem.isVideo) {
                binding.previewVideoIndicator.visibility = View.VISIBLE
            } else {
                binding.previewVideoIndicator.visibility = View.INVISIBLE
            }
            binding.element = podItem
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PodItem>() {
        override fun areItemsTheSame(oldItem: PodItem, newItem: PodItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: PodItem, newItem: PodItem): Boolean {
            return oldItem.date == newItem.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): PodItemViewHolder {
        return PodItemViewHolder(PreviewElementBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PodItemViewHolder, position: Int) {
        val podItem = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(podItem)
        }
        holder.bind(podItem)

        if (position == (itemCount - 1))
            _lastBound.value = true
    }

    class OnClickListener(val clickListener: (podItem:PodItem) -> Unit) {
        fun onClick(podItem:PodItem) = clickListener(podItem)
    }
}

