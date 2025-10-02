package ru.netology.learningandtrying.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.learningandtrying.R
import ru.netology.learningandtrying.databinding.FragmentNewPostBinding
import ru.netology.learningandtrying.util.AndroidUtils
import ru.netology.learningandtrying.util.StringArg
import ru.netology.learningandtrying.viewmodel.PostViewModel

private const val MAX_SIZE = 2048

@AndroidEntryPoint
class NewPostFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)
        binding.content.requestFocus()
        arguments?.textArg?.let(binding.content::setText)

        binding.topAppBar.inflateMenu(R.menu.new_post_menu)
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save -> {
                    viewModel.changeContent(binding.content.text.toString())
                    viewModel.save()
                    AndroidUtils.hideKeyboard(requireView())
                    true
                }

                else -> false
            }
        }

        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(
                        requireContext(),
                        R.string.image_picker_error,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@registerForActivityResult
                }

                val uri = it.data?.data ?: return@registerForActivityResult
                viewModel.savePhoto(uri, uri.toFile())
            }
        viewModel.postsCreated.observe(viewLifecycleOwner) {
            AndroidUtils.hideKeyboard(requireView())
            viewModel.load()
            findNavController().navigateUp()
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                binding.photoContainer.visibility = View.GONE
                return@observe
            }

            binding.photoContainer.visibility = View.VISIBLE
            binding.photo.setImageURI(it.uri)
        }

        binding.removePhoto.setOnClickListener {
            viewModel.removePhoto()
            binding.photo.setImageDrawable(null)
        }

        binding.takePhoto.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .maxResultSize(MAX_SIZE, MAX_SIZE)
                .cameraOnly()
                .createIntent {
                    imagePickerLauncher.launch(it)
                }
        }

        binding.openGallery.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .maxResultSize(MAX_SIZE, MAX_SIZE)
                .galleryOnly()
                .createIntent {
                    imagePickerLauncher.launch(it)
                }
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg by StringArg
    }
}