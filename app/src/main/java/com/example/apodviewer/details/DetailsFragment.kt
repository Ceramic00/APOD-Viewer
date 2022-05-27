package com.example.apodviewer.details

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.app.ShareCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.apodviewer.R
import com.example.apodviewer.databinding.FragmentDetailsBinding


class DetailsFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(activity).application

        val item = DetailsFragmentArgs.fromBundle(requireArguments()).item
        val viewModelFactory = DetailsViewModelFactory(item, application)
        val detailsViewModel = ViewModelProvider(this, viewModelFactory) [DetailsViewModel::class.java]

        val binding: FragmentDetailsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_details, container, false)

        binding.viewModel = detailsViewModel
        binding.lifecycleOwner = this
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.share_menu, menu)
        if (null == getShareIntent().resolveActivity(requireActivity().packageManager)){
            menu.findItem(R.id.share)?.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.share -> shareUrl()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareUrl() {
        startActivity(getShareIntent())
    }

    private fun getShareIntent(): Intent {
        val args = DetailsFragmentArgs.fromBundle(requireArguments())
        return ShareCompat.IntentBuilder(requireActivity())
            .setText(args.item.fullSizeURL)
            .setType("text/plain")
            .intent
    }
}