package io.github.null2264.dicodingstories.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.data.api.AuthService
import io.github.null2264.dicodingstories.databinding.FragmentRegisterBinding
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
class RegisterFragment : Fragment() {
    private val binding: FragmentRegisterBinding by viewBinding(CreateMethod.INFLATE)

    @Inject
    lateinit var auth: AuthService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding.apply {
            btnRegister.setOnButtonClickListener {
                doRegister()
            }

            etName.editText.apply {
                imeOptions = EditorInfo.IME_ACTION_NEXT
                maxLines = 1
                isSingleLine = true
            }

            pwdPass.editText.apply {
                imeOptions = EditorInfo.IME_ACTION_GO
                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_GO -> {
                            doRegister()
                            true
                        }
                        else -> false
                    }
                }
            }

            btnLoginSuffix.setOnClickListener {
                goToLogin()
            }
        }

        return binding.root
    }

    private fun doRegister() {
        binding.apply {
            var isValid = true
            val inputViews = listOf(etName, emEmail, pwdPass)
            inputViews.forEach { isValid = isValid.and(it.validateInput()) }

            if (isValid) {
                if (view != null)
                    hideKeyboard(requireContext(), view!!.rootView.windowToken)

                inputViews.forEach { it.editText.isEnabled = false }
                btnRegister.onStartLoading()
                lifecycleScope.launch {
                    val jsonObject = JSONObject()
                        .put("name", etName.text.toString())
                        .put("email", emEmail.text.toString())
                        .put("password", pwdPass.text.toString())
                    val resp = auth.register(jsonObject.toString())

                    resp.apply {
                        if (isSuccessful && body() != null) {
                            withContext(Dispatchers.Main) {
                                quickDialog(
                                    requireContext(),
                                    ZiAlertDialog.SUCCESS,
                                    getString(R.string.success),
                                    getString(R.string.account_created)
                                ) {
                                    goToLogin()
                                }
                            }
                        } else if (errorBody() != null) {
                            val errJSON = Common.parseError(errorBody()!!.string())
                            withContext(Dispatchers.Main) {
                                val errMsg: String = when (errJSON.message) {
                                    "Email is already taken" -> getString(R.string.taken_email)
                                    else -> errJSON.message
                                }
                                quickDialog(requireContext(), ZiAlertDialog.ERROR, getString(R.string.fail), errMsg) {
                                    inputViews.forEach { it.editText.isEnabled = true }
                                    btnRegister.onStopLoading()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun goToLogin() {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()
        findNavController().navigate(R.id.loginFragment, null, navOptions)
    }
}