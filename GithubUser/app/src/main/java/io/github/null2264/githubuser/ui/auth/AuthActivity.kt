package io.github.null2264.githubuser.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.githubuser.BuildConfig
import io.github.null2264.githubuser.R
import io.github.null2264.githubuser.data.preference.SettingPreferences
import io.github.null2264.githubuser.databinding.ActivityAuthBinding
import io.github.null2264.githubuser.lib.Token
import io.github.null2264.githubuser.lib.api.OAuthConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : AppCompatActivity(R.layout.activity_auth) {
    private val binding: ActivityAuthBinding by viewBinding(CreateMethod.INFLATE)

    @Inject
    lateinit var prefs: SettingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val authorizeUrl: String? = when {
            CLIENT_ID == "" -> null
            CLIENT_SECRET == "" -> null
            else -> buildString {
                append(GITHUB_OAUTH_URL)
                append("authorize?client_id=").append(CLIENT_ID)
                append("&scope=user%20public_repo%20repo")
                append("&response_type=code")
                append("&redirect_uri=").append(REDIRECT_URI)
            }
        }

        binding.apply {
            btnAuthUse.setOnClickListener {
                if (etAuthToken.text.isEmpty()) {
                    etAuthToken.error = "Token can't be empty!"
                } else {
                    btnAuthUse.isEnabled = false
                    val token = Token("bearer", etAuthToken.text.toString())

                    lifecycleScope.launch {
                        val isValid = token.validateToken()
                        withContext(Dispatchers.Main) {
                            btnAuthUse.isEnabled = true
                            if (isValid) {
                                prefs.setToken(token)
                                finish()
                            } else {
                                withContext(Dispatchers.Main) {
                                    etAuthToken.error = "Invalid token!"
                                }
                            }
                        }
                    }
                }
            }

            btnAuthOauth.apply {
                isEnabled = authorizeUrl != null
                if (isEnabled)
                    setOnClickListener {
                        val customTabs = CustomTabsIntent.Builder().build()
                        customTabs.launchUrl(this@AuthActivity, Uri.parse(authorizeUrl))
                    }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val code = intent?.data?.getQueryParameter("code")
        if (code != null)
            tokenFromCode(code)
    }

    private fun tokenFromCode(authCode: String) {
        val client = OAuthConfig.retrofitClient().getToken(CLIENT_ID, CLIENT_SECRET, authCode)
        client.enqueue(object : Callback<Token> {
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if (!response.isSuccessful || response.body() == null)
                    return

                lifecycleScope.launch {
                    prefs.setToken(response.body()!!)
                    finish()
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                Log.e(TAG, "onFail: ${t.message}")
            }
        })
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    companion object {
        private const val GITHUB_OAUTH_URL = "https://github.com/login/oauth/"
        private const val CLIENT_ID = BuildConfig.clientId
        private const val CLIENT_SECRET = BuildConfig.clientSecret
        private const val REDIRECT_URI = "${BuildConfig.redirectScheme}://callback"
    }
}