package com.met.atims_reporter.ui.add_crash_report.step_three.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AddCrashReportPassengerItemBinding
import com.met.atims_reporter.model.CrashReportPassengerInfo
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.widget.edittext.EditText
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import java.util.regex.Pattern

class PassengerInformationAdapter(private val showMessageCallback: ShowMessageCallback) :
    RecyclerView.Adapter<PassengerInformationAdapter.Holder>() {

    private val list: ArrayList<CrashReportPassengerInfo> = ArrayList()

    fun addOneMore() {
        list.add(
            CrashReportPassengerInfo()
        )
        notifyDataSetChanged()
    }

    private fun removeOne(index: Int) {
        list.removeAt(index)
        notifyDataSetChanged()
    }

    fun valid(): Boolean {
        var valid = true
        list.forEach {
            if (
                it.name.isEmpty() ||
                it.passenger_address.isEmpty() ||
                it.passenger_licence_number.isEmpty() ||
                it.passenger_phone.isEmpty() ||
                it.passenger_phone.length > 15 ||
                it.passenger_phone.length < 10 ||
                !isValidMobile(it.passenger_phone)
            )
                valid = false
        }


        if (!valid) {
            showMessageCallback.showMessageInDialog("Please fill the required fields with valid data.")
        }

        return valid
    }

    val patterns =  "^\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?\\s*$"
    fun isValidMobile(target: String): Boolean {
        return when {
            !Pattern.compile(patterns).matcher(target).matches() -> false
            isSameDigits(target) -> false
            //target.trim().length < 9  -> false
            else -> true
        }
    }

    fun isSameDigits(target: String): Boolean {
        val n: Int = target.length
        for (i in 1 until n)
            if (target[i] != target[0]) return false
        return true
    }

    fun getList() = list

    inner class Holder(private val binding: AddCrashReportPassengerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(passenger: CrashReportPassengerInfo) {
            binding.editTextName.apply {
                heading("Name")
                editTextWidthPercent(60)
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            passenger.name = text
                        }
                    }
                )
            }
            binding.editTextPhoneNumber.apply {
                heading("Phone Number")
                editTextWidthPercent(60)
                inputMode(EditTextInputMode.NUMBER)
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            passenger.passenger_phone = text
                        }
                    }
                )
            }
            binding.editTextAddress.apply {
                heading("Address")
                editTextWidthPercent(60)
                inputMode(
                    EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                    MultiLineConfig(1, 3)
                )
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            passenger.passenger_address = text
                        }
                    }
                )
            }
            binding.editTextDriverLicenseInformation.apply {
                heading("Driver License Information")
                editTextWidthPercent(60)
                inputMode(
                    EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                    MultiLineConfig(1, 3)
                )
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            passenger.passenger_licence_number = text
                        }
                    }
                )
            }
            binding.spinnerInjuries.apply {
                passenger.is_injury = "yes"
                heading("Injuries")
                spinnerWidthPercent(60)
                addItems(
                    arrayListOf(
                        SpinnerData("Yes", "Yes"),
                        SpinnerData("No", "No")
                    ),
                    object : Spinner.OnItemSelectedListener {
                        override fun <T> selected(item: SpinnerData<T>) {
                            passenger.is_injury = item.toShow
                        }
                    }
                )
            }

            binding.appcompatImageViewMinus.setOnClickListener {
                removeOne(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            AddCrashReportPassengerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }
}