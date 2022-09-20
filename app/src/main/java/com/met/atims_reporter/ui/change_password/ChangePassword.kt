package com.met.atims_reporter.ui.change_password

import android.os.Bundle
import androidx.lifecycle.Observer
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.ActivityChangePasswordBinding
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChangePassword : AtimsSuperActivity(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val provider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_change_password)
        binding.context = this

        viewModel = androidx.lifecycle.ViewModelProvider(this, provider).get(ViewModel::class.java)
        bindToViewModel()

        enableBackButton()
        setPageTitle("CHANGE PASSWORD")

        binding.editTextNewPassword.apply {
            heading("New Password")
            inputMode(
                EditTextInputMode.PASSWORD
            )
        }

        binding.editTextConfirmPassword.apply {
            heading("Confirm Password")
            inputMode(
                EditTextInputMode.PASSWORD
            )
        }
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataChangePassword.observe(
            this,
            Observer { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        it.readContent()
                        showMessageWithOneButton(
                            message = "Your password has been changed.",
                            callback = object : DialogUtil.CallBack {

                                override fun buttonClicked() {
                                    super.buttonClicked()

                                    finish()
                                }
                            }
                        )
                    }
                }
            }
        )
        viewModel.mediatorLiveDataChangePasswordError.observe(
            this,
            Observer { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
    }

    fun validatePassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#\$&*%^&~ ]).{8,}\$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    fun changePassword() {
        if (
            binding.editTextNewPassword.getText().isEmpty()
        ) {
            showMessageInDialog("Please provide new password")
            return
        }
        if (!validatePassword(binding.editTextNewPassword.getText().trim())) {
            showMessageInDialog(getString(R.string.is_valid_passwd))
            return
        }
        if (
            binding.editTextConfirmPassword.getText().isEmpty()
        ) {
            showMessageInDialog("Please provide confirm password")
            return
        }
        if (
            !binding.editTextNewPassword.getText().equals(
                binding.editTextConfirmPassword.getText(),
                ignoreCase = false
            )
        ) {
            showMessageInDialog("New password and confirm password are not same.")
            return
        }

        showProgress()

        viewModel.changePassword(
            binding.editTextNewPassword.getText()
        )
    }
}