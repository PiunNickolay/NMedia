package ru.netology.learningandtrying.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.learningandtrying.R
import android.Manifest
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.learningandtrying.activity.NewPostFragment.Companion.textArg
import ru.netology.learningandtrying.auth.AppAuth
import ru.netology.learningandtrying.databinding.ActivityAppBinding
import ru.netology.learningandtrying.viewmodel.AuthViewModel
import javax.inject.Inject


@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationsPermission()
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu (
                    menu: Menu,
                    menuInflater: MenuInflater
                ) {
                    menuInflater.inflate(R.menu.auth_menu, menu)
                        viewModel.state.observe(this@AppActivity){ authorized ->
                        menu.setGroupVisible(R.id.unauthorized, !authorized)
                        menu.setGroupVisible(R.id.authorized, authorized)
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                    when (menuItem.itemId) {
                        R.id.signin -> {
                            findNavController(R.id.nav_controller).navigate(R.id.signInFragment)
                            true
                        }
                        R.id.signup -> {
                            true
                        }
                        R.id.logout -> {
                            appAuth.removeAuth()
                            true
                        }
                        else -> false
                    }
            }
        )

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if(it.action != Intent.ACTION_SEND){
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if(text.isNullOrBlank()){
                Snackbar.make(
                    binding.root,
                    R.string.error_empty_content,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok){
                    finish()
                    }
                    .show()
            }
            findNavController(R.id.nav_controller).navigate(
                R.id.action_postFragment_to_newPostFragment,
                Bundle().apply { textArg = text }
            )

            findNavController(R.id.nav_controller).navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply {
                    textArg = text
                }
            )
        }

    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }
}