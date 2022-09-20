package com.met.atims_reporter.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.met.atims_reporter.application.ApplicationClass
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.ActivityProfileBinding
import com.met.atims_reporter.ui.change_password.ChangePassword
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.util.activity.ShowProgressCallback
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.File
import java.util.regex.Pattern

class ProfileFragment : Fragment(), KodeinAware {

    override lateinit var kodein: Kodein

    private val provider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private var image: String = ""
    private lateinit var binding: ActivityProfileBinding
    private lateinit var requiredContext: Context
    lateinit var showMessageCallback: ShowMessageCallback
    lateinit var showProgressCallback: ShowProgressCallback
    private var editing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = ActivityProfileBinding.inflate(
            inflater,
            container,
            false
        )
        binding.context = this

        binding.firstName.apply {
            heading("First Name")
            editable(false)
        }
        binding.lastName.apply {
            heading("Last Name")
            editable(false)
        }
        binding.email.apply {
            heading("Email")
            editable(false)
        }
        binding.mobileNumber.apply {
            heading("Mobile Number")
            inputMode(
                EditTextInputMode.NUMBER
            )
            editable(false)
        }
        binding.address.apply {
            heading("Street")
            inputMode(
                EditTextInputMode.INPUT_TEXT,
                MultiLineConfig(1, 3)
            )
            editable(false)
        }
        binding.city.apply {
            heading("City")
            editable(false)
        }
        binding.pinCode.apply {
            heading("Zip Code")
            inputMode(
                EditTextInputMode.NUMBER
            )
            editable(false)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (activity as AtimsSuperActivity).showProfileActionBar()
        if (editing)
            (activity as Dashboard).editProfile()
        (activity as AtimsSuperActivity).setPageTitle("PROFILE")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        this.requiredContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kodein = (requiredContext.applicationContext as ApplicationClass).kodein

        viewModel = androidx.lifecycle.ViewModelProvider(
            this,
            provider
        )
            .get(ViewModel::class.java)
        bindToViewModel()

        try {
            setImageToUI()

            preFillData()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun preFillData() {
        viewModel.giveRepository().getUserData()!!.apply {
            binding.firstName.setText(first_name)
            binding.lastName.setText(last_name)
            binding.email.setText(email)
            binding.mobileNumber.setText(contact_mobile)
            binding.address.setText(address)
            binding.city.setText(city)
            binding.pinCode.setText(zip)
            if (profile_image != "")
                binding.ivProfileImage.setImage(
                    profile_image,
                    isCircularImage = true,
                    needBorderWithCircularImage = true
                )

        }
    }

    fun gotNewImage(image: String) {
        this.image = image
        setImageToUI()
    }

    private fun setImageToUI() {
        if (image == "") {
            binding.ivProfileImage.putPlaceHolder(
                viewModel.giveRepository().getUserData()!!.first_name.toString(),
                isCircularImage = true
            )
        } else {
            binding.ivProfileImage.setImage(
                File(image),
                isCircularImage = true,
                needBorderWithCircularImage = true
            )
        }
    }

    fun initiatePictureCapture() {
        activity?.let {
            (it as Dashboard).initiateCaptureForProfilePicture()
        }
    }

    fun changeEditableMode(editable: Boolean) {
        editing = editable
        binding.firstName.apply {
            editable(editable)
        }
        binding.lastName.apply {
            editable(editable)
        }
        binding.email.apply {
            editable(editable)
        }
        binding.mobileNumber.apply {
            editable(editable)
        }
        binding.address.apply {
            editable(editable)
        }
        binding.city.apply {
            editable(editable)
        }
        binding.pinCode.apply {
            editable(editable)
        }
        if (editable)
            binding.ivCamera.visibility = View.VISIBLE
        else
            binding.ivCamera.visibility = View.GONE
    }

    fun saveData() {
        if (binding.firstName.getText().isEmpty()) {
            showMessageCallback.showMessageInDialog("Please provide first name")
            return
        }
        if (binding.firstName.getText().toString().trim().contains("[0-9!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            showMessageCallback.showMessageInDialog("Please provide valid first name")
            return
        }
        if (binding.lastName.getText().isEmpty()) {
            showMessageCallback.showMessageInDialog("Please provide last name")
            return
        }
        if (binding.lastName.getText().toString().trim().contains("[0-9!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            showMessageCallback.showMessageInDialog("Please provide valid last name")
            return
        }
        if (binding.email.getText().isEmpty()) {
            showMessageCallback.showMessageInDialog("Please provide email")
            return
        }
        if (!isValidEmail(binding.email.getText().toString())) {
            showMessageCallback.showMessageInDialog("Please provide valid email")
            return
        }
        if (binding.mobileNumber.getText().isEmpty()) {
            showMessageCallback.showMessageInDialog("Please provide mobile number")
            return
        }
        if (binding.mobileNumber.getText().length != 10) {
            showMessageCallback.showMessageInDialog("Please provide 10 digit mobile number")
            return
        }
        if (!isValidMobile(binding.mobileNumber.getText().toString())) {
            showMessageCallback.showMessageInDialog("Please provide valid mobile number")
            return
        }
        if (binding.address.getText().isEmpty()) {
            showMessageCallback.showMessageInDialog("Please provide street")
            return
        }
        if (binding.city.getText().isEmpty()) {
            showMessageCallback.showMessageInDialog("Please provide city")
            return
        }
        if (binding.city.getText().trim().contains("[0-9!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())) {
            showMessageCallback.showMessageInDialog("Invalid city!")
            return
        }
        if (binding.pinCode.getText().isEmpty()) {
            showMessageCallback.showMessageInDialog("Please zip code")
            return
        }
        if (!isValidZipCode(binding.pinCode.getText())) {
            showMessageCallback.showMessageInDialog("Please provide valid zip code")
            return
        }

        showProgressCallback.showProgress()
        viewModel.updateProfile(
            binding.firstName.getText(),
            binding.lastName.getText(),
            binding.mobileNumber.getText(),
            binding.address.getText(),
            binding.city.getText(),
            binding.pinCode.getText(),
            if (image != "") image else null
        )
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataUpdateProfile.observe(
            this,
            Observer<Event<String>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        editing = false
                        showProgressCallback.hideProgress()
                        activity?.let { activity ->
                            (activity as Dashboard).profileSaved()
                            (activity as Dashboard).refreshHomeUserName()
                        }
                        changeEditableMode(false)
                        showMessageCallback.showMessageInDialog("Profile updated")
                    }
                }
            }
        )

        viewModel.mediatorLiveDataUpdateProfileError.observe(
            this,
            Observer<Event<Result>> { t ->
                t?.let {
                    if (it.shouldReadContent()) {
                        showProgressCallback.hideProgress()
                        activity?.let { activity ->
                            (activity as Dashboard).handleGenericResult(it.getContent()!!)
                        }
                    }
                }
            }
        )
    }

    fun changePassword() {
        startActivity(
            Intent(
                requiredContext,
                ChangePassword::class.java
            )
        )
    }

    val patterns =  "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    fun isValidMobile(target: String): Boolean {
        return when {
            !Pattern.compile(patterns).matcher(target).matches() -> false
            isSameDigits(target) -> false
            target.trim().length < 9  -> false
            else -> true
        }
    }

    fun isSameDigits(target: String): Boolean {
        val n: Int = target.length
        for (i in 1 until n)
            if (target[i] != target[0]) return false
        return true
    }

    fun isValidZipCode(target: String): Boolean {
        return when {
            target.trim().contains("[A-Za-z!\\\"#\$%&'()*+,-./:;\\\\\\\\<=>?@\\\\[\\\\]^_`{|}~]".toRegex()) -> false
            isSameDigits(target) -> false
            target.trim().length < 5  -> false
            else -> true
        }
    }

    val EMAIL_ADDRESS_PATTERN = Pattern.compile(".+@.+\\.[a-z]+")
    fun isValidEmail(target: String): Boolean {
        //return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        return !TextUtils.isEmpty(target) && EMAIL_ADDRESS_PATTERN.matcher(target).matches()
    }
}