package io.github.null2264.githubuser.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.lifecycleScope
import io.github.null2264.githubuser.BuildConfig
import io.github.null2264.githubuser.databinding.ActivityAuthBinding
import io.github.null2264.githubuser.lib.Token
import io.github.null2264.githubuser.lib.api.OAuthConfig
import io.github.null2264.githubuser.lib.api.apolloClient
import io.github.null2264.githubuser.lib.setToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthActivity : AppCompatActivity() {
    // Placeholder for OAuth
    // TODO: Make the actual OAuth if i have enough time
    private lateinit var binding: ActivityAuthBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var webView: WebView

    companion object {
        private const val GITHUB_OAUTH_URL = "https://github.com/login/oauth/"
        private const val CLIENT_ID = BuildConfig.clientId
        private const val CLIENT_SECRET = BuildConfig.clientSecret
        private const val REDIRECT_URI = "${BuildConfig.redirectScheme}://callback"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
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

        sharedPref = getSharedPreferences("GITHUB_TOKEN", MODE_PRIVATE)

        binding.apply {
            btnAuthUse.setOnClickListener {
                if (binding.etAuthToken.text.isEmpty()) {
                    binding.etAuthToken.error = "Token can't be empty!"
                } else {
                    binding.btnAuthUse.isEnabled = false
                    val token = binding.etAuthToken.text.toString()
                    val client = apolloClient(token)

                    lifecycleScope.launch {
                        val isSuccess =
                            setToken(
                                apolloClient = client,
                                sharedPref = sharedPref,
                                editText = binding.etAuthToken
                            )
                        withContext(Dispatchers.Main) {
                            binding.btnAuthUse.isEnabled = true
                            if (isSuccess)
                                finish()
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

                setToken(sharedPref, response.body()!!.key)
                finish()
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                Log.e(TAG, "onFail: ${t.message}")
            }
        })
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}