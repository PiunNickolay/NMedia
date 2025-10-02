package ru.netology.learningandtrying.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ru.netology.learningandtrying.R
import ru.netology.learningandtrying.databinding.FragmentImageBinding

class ImageFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImageBinding.inflate(inflater, container, false)

        val url = arguments?.getString("url") ?: return binding.root

        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_is_not_image_24)
            .error(R.drawable.ic_error_24)
            .into(binding.fullscreenImage)

        return binding.root
    }
}