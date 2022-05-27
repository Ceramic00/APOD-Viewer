package com.example.apodviewer.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apodviewer.R
import com.example.apodviewer.database.PodDatabase
import com.example.apodviewer.databinding.FragmentGalleryBinding


class GalleryFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentGalleryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_gallery, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = PodDatabase.getInstance(application).podDatabaseDao
        val viewModelFactory = GalleryViewModelFactory(dataSource, application)

        val viewModel =
            ViewModelProvider(this, viewModelFactory)[GalleryViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.galleryList.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            viewModel.displayDetails(it)
        })

        binding.galleryList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    if (totalItemCount > 0 &&
                        lastVisible >= (totalItemCount - 3) &&
                        viewModel.status.value != PodApiStatus.LOADING) {
                            viewModel.getNextPodItems()
                    }
                }
            }
        })

        viewModel.navigateToDetails.observe(viewLifecycleOwner) {
            it?.let {
                if (it.isVideo){
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.fullSizeURL)))
                    viewModel.displayDetailsComplete()
                } else {
                    this.findNavController().navigate(
                        GalleryFragmentDirections.actionFragmentGalleryToDetailsFragment(it)
                    )
                    viewModel.displayDetailsComplete()
                }
            }
        }

        viewModel.showNetworkError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(
                    application.applicationContext,
                    R.string.no_internet,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.showNetworkError.value = null
            }
        }

        return binding.root
    }
}