package ru.netology.learningandtrying.activity

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.learningandtrying.R
import ru.netology.learningandtrying.databinding.FragmentSignInBinding
import ru.netology.learningandtrying.viewmodel.SignInViewModel

@AndroidEntryPoint
class SignInFragment: Fragment() {

    private val viewModel: SignInViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        binding.signInButton.setOnClickListener {
            viewModel.signIn(
                binding.loginEdit.text.toString(),
                binding.passwordEdit.text.toString()
            )
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progress.isVisible = loading
            binding.signInButton.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
        }

        viewModel.succes.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        return binding.root
    }
}