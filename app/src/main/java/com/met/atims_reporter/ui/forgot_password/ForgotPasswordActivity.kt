package com.met.atims_reporter.ui.forgot_password

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.lifecycle.Observer
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.ActivityForgotPasswordBinding
import com.met.atims_reporter.enums.SuperActivityStatusBarColor
import com.met.atims_reporter.model.ForgotPasswordOTPRequest
import com.met.atims_reporter.model.ResetPasswordRequest
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.regex.Matcher
import java.util.regex.Pattern

class ForgotPasswordActivity : AtimsSuperActivity(SuperActivityStatusBarColor.WHITE), KodeinAware {

    enum class State {
        OTP_REQUESTED
    }

    override val kodein: Kodein by kodein()

    private lateinit var state: State
    private val provider: ViewModelProvider by instance<ViewModelProvider>()
    private lateinit var viewModel: ViewModel
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_forgot_password)
        binding.context = this

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        ).get(
            ViewModel::class.java
        )
        bindToViewModel()

        changeUI()
    }

    fun clickedButton() {
        if (!this::state.isInitialized) {
            if (validateEmail()) {
                showProgress()
                viewModel.requestOTPForResetPassword(
                    ForgotPasswordOTPRequest(
                        email = binding.EdLoginEmailAddress.text.toString().trim()
                    )
                )
                binding.EdLoginEmailAddress.isEnabled = false
            }
            return
        }

        when (state) {
            State.OTP_REQUESTED -> {
                if (validateEmail() && validateOTP()) {
                    if (binding.editTextNewPassword.text.toString().isEmpty()) {
                        showMessageInDialog("Please provide new Password")
                        return
                    }
                    if (!validatePassword(binding.editTextNewPassword.text.toString().trim())) {
                        showMessageInDialog(getString(R.string.is_valid_passwd))
                        return
                    }
                    if (binding.editTextConfirmNewPassword.text.toString().isEmpty()) {
                        showMessageInDialog("Please provide confirm Password")
                        return
                    }
                    if (binding.editTextConfirmNewPassword.text.toString() != binding.editTextNewPassword.text.toString()) {
                        showMessageInDialog("New and confirm password should be same.")
                        return
                    }
                    showProgress()
                    viewModel.resetPassword(
                        ResetPasswordRequest(
                            email = binding.EdLoginEmailAddress.text.toString().trim(),
                            password = binding.editTextConfirmNewPassword.text.toString(),
                            reset_password_otp = binding.editTextOTP.text.toString()
                        )
                    )
                }
            }
        }
    }

    fun validatePassword(password: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#\$&*%^&~ ]).{8,}\$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private fun validateEmail(): Boolean {
        if (
            binding.EdLoginEmailAddress.text.toString().isEmpty()
        ) {
            showMessageInDialog("Please provide email")
            return false
        }
        if (
            !Patterns.EMAIL_ADDRESS.matcher(
                binding.EdLoginEmailAddress.text.toString()
            ).matches()
        ) {
            showMessageInDialog("Please provide valid email address")
            return false
        }

        return true
    }

    private fun validateOTP(): Boolean {
        if (
            binding.editTextOTP.text.toString().isEmpty()
        ) {
            showMessageInDialog("Please provide OTP")
            return false
        }

        return true
    }

    @SuppressLint("SetTextI18n")
    private fun changeUI() {
        if (!this::state.isInitialized) {
            binding.apply {
               // AutoTextOTP.visibility = View.GONE
                editTextOTP.visibility = View.GONE
               // appcompatImageViewOTP.visibility = View.GONE
                //AutoTextNewPassword.visibility = View.GONE
                editTextNewPassword.visibility = View.GONE
               // appcompatImageViewNewPassword.visibility = View.GONE
              //  AutoTextReenterNewPassword.visibility = View.GONE
                editTextConfirmNewPassword.visibility = View.GONE
               // appcompatImageViewConfirmPassword.visibility = View.GONE
            }
            return
        }

        when (state) {
            State.OTP_REQUESTED -> {
                binding.apply {
                  //  AutoTextOTP.visibility = View.VISIBLE
                    editTextOTP.visibility = View.VISIBLE
                  //  appcompatImageViewOTP.visibility = View.VISIBLE
                  //  AutoTextNewPassword.visibility = View.VISIBLE
                    editTextNewPassword.visibility = View.VISIBLE
                  //  appcompatImageViewNewPassword.visibility = View.VISIBLE
                  //  AutoTextReenterNewPassword.visibility = View.VISIBLE
                    editTextConfirmNewPassword.visibility = View.VISIBLE
                  //  appcompatImageViewConfirmPassword.visibility = View.VISIBLE
                    btnForgotPassword.text = "Reset Password"
                }
            }
        }
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataOTPRequestForResetPassword.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        it.readContent()
                        state = State.OTP_REQUESTED
                        changeUI()
                    }
                }
            }
        )
        viewModel.mediatorLiveDataOTPRequestForResetPasswordError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
        viewModel.mediatorLiveDataResetPassword.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        hideProgress()
                        it.readContent()
                        showMessageWithOneButton(
                            message = "Your password has been changed. you can now login with new password.",
                            buttonText = "Okay",
                            cancellable = false,
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
        viewModel.mediatorLiveDataResetPasswordError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        handleGenericResult(it.getContent()!!)
                    }
                }
            }
        )
    }
}
