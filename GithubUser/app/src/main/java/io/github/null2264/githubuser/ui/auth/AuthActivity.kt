package io.github.null2264.githubuser.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.null2264.githubuser.databinding.ActivityAuthBinding
import io.github.null2264.githubuser.lib.apolloClient
import io.github.null2264.githubuser.lib.setToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthActivity : AppCompatActivity() {
    // Placeholder for OAuth
    // TODO: Make the actual OAuth if i have enough time
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAuthUse.setOnClickListener {
            if (binding.etAuthToken.text.isEmpty()) {
                binding.etAuthToken.error = "Token can't be empty!"
            } else {
                binding.btnAuthUse.isEnabled = false
                val token = binding.etAuthToken.text.toString()
                val client = apolloClient(token)

                lifecycleScope.launch {
                    val isSuccess =
                        setToken(client, getSharedPreferences("GITHUB_TOKEN", MODE_PRIVATE), binding.etAuthToken)
                    withContext(Dispatchers.Main) {
                        binding.btnAuthUse.isEnabled = true
                        if (isSuccess)
                            finish()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        /* Prevent user from going to MainActivity. (They need to properly input the token first)
         * I don't know if there's a better way to do this so please let me know if there's a better way
         */
        finishAffinity()
    }
}