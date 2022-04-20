package io.github.null2264.dicodingstories.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.api.AuthService
import io.github.null2264.dicodingstories.data.preference.PreferencesHelper
import io.github.null2264.dicodingstories.databinding.FragmentLoginBinding
import io.github.null2264.dicodingstories.lib.Common
import io.github.null2264.dicodingstories.lib.Common.hideKeyboard
import io.github.null2264.dicodingstories.lib.Common.quickDialog
import io.github.null2264.dicodingstories.widget.dialog.ZiAlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val binding: FragmentLoginBinding by viewBinding(CreateMethod.INFLATE)

    @Inject
    lateinit var auth: AuthService

    @Inject
    lateinit var prefs: PreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.apply {
            btnLogin.setOnButtonClickListener {
                doLogin()
            }

            pwdPass.editText.apply {
                imeOptions = EditorInfo.IME_ACTION_GO
                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_GO -> {
                            doLogin()
                            true
                        }
                        else -> false
                    }
                }
            }

            btnRegisterSuffix.setOnClickListener {
                findNavController().navigate(R.id.registerFragment)
            }
        }

        return binding.root
    }

    private fun doLogin() {
        binding.apply {
            var isValid = true
            val inputViews = listOf(emEmail, pwdPass)
            inputViews.forEach { isValid = isValid.and(it.validateInput()) }

            if (isValid) {
                if (view != null)
                    hideKeyboard(requireContext(), view!!.rootView.windowToken)

                inputViews.forEach { it.editText.isEnabled = false }
                btnLogin.onStartLoading()
                lifecycleScope.launch {
                    val jsonObject = JSONObject()
                        .put("email", emEmail.text.toString())
                        .put("password", pwdPass.text.toString())
                    val resp = auth.login(jsonObject.toString())

                    resp.apply {
                        if (isSuccessful && body() != null) {
                            val body = body()!!
                            Log.d("ziRequest", body.loginResult.token)
                            prefs.setToken(body.loginResult.token)
                            findNavController().navigate(R.id.action_login_to_dashboard)
                        } else if (errorBody() != null) {
                            val errJSON = Common.parseError(errorBody()!!.string())
                            withContext(Dispatchers.Main) {
                                val errMsg: String? = when (errJSON.message) {
                                    "Invalid password" -> {
                                        pwdPass.error = getString(R.string.wrong_password)
                                        null
                                    }
                                    "User not found" -> {
                                        emEmail.error = getString(R.string.user_not_found)
                                        null
                                    }
                                    else -> errJSON.message
                                }
                                if (errMsg != null)
                                    quickDialog(requireContext(), ZiAlertDialog.ERROR, getString(R.string.fail), errMsg)
                                inputViews.forEach { it.editText.isEnabled = true }
                                btnLogin.onStopLoading()
                            }
                        }
                    }
                }
            }
        }
    }
}