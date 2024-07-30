package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity
import timber.log.Timber

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        Timber.plant(Timber.DebugTree())

        val binding = DataBindingUtil.setContentView<ActivityAuthenticationBinding>(
            this, R.layout.activity_authentication
        )

        observeAuthenticationState()
        binding.loginButton.setOnClickListener {
            login()
        }
    }

    private var loginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Timber.i(
                    "Successfully signed in user " + "${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                val response = IdpResponse.fromResultIntent(result.data)
                Timber.e("Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }

    private fun login() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val layout = AuthMethodPickerLayout.Builder(R.layout.login_layout)
            .setEmailButtonId(R.id.email_button).setGoogleButtonId(R.id.google_button).build()

        val intent =
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setAuthMethodPickerLayout(layout)
                .setTheme(R.style.AppTheme)
                // TODO: should we keep this? Removing this line throws errors in logcat, but app works
                .setIsSmartLockEnabled(false)
                .build()
        loginLauncher.launch(intent)
    }

    /**
     * Observes the authentication state and changes the UI accordingly.
     * If there is a logged in user: (1) show a logout button and (2) display their name.
     * If there is no logged in user: show a login button
     */
    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(this) { authenticationState ->
            if (authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                startActivity(Intent(this, RemindersActivity::class.java))
                finish()
            }
        }
    }
}