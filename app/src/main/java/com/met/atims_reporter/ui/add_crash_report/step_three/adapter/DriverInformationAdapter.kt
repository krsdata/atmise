package com.met.atims_reporter.ui.add_crash_report.step_three.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.AddCrashReportDriverItemBinding
import com.met.atims_reporter.model.CrashReportDriverInfo
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.widget.edittext.EditText
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData
import java.util.regex.Pattern

class DriverInformationAdapter(
    private val showMessageCallback: ShowMessageCallback,
    private val context: Context
) :
    RecyclerView.Adapter<DriverInformationAdapter.Holder>() {

    private val list: ArrayList<CrashReportDriverInfo> = arrayListOf(CrashReportDriverInfo())

    fun addOneMore() {
        list.add(
            CrashReportDriverInfo()
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
                it.dirver_name.isEmpty() ||
                it.driver_phone.isEmpty() ||
                it.driver_phone.length > 15 ||
                it.driver_phone.length < 10 ||
                !isValidMobile(it.driver_phone) ||
                it.driver_licence_number.isEmpty() ||
                it.vehicle_insurance_info.isEmpty() ||
                it.insurance_expriary_date.isEmpty()
            )
                valid = false

            it.passengerAdapter?.let { adapter ->
                if (!adapter.valid())
                    valid = false
            }
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

    fun getList(): ArrayList<CrashReportDriverInfo> {
        list.forEach {
            it.passengerAdapter?.let { adapter ->
                it.passenger_info = adapter.getList()
            }
        }
        return list
    }

    inner class Holder(private val binding: AddCrashReportDriverItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private lateinit var passengerInformationAdapter: PassengerInformationAdapter

        @SuppressLint("SetTextI18n")
        fun bind(driver: CrashReportDriverInfo) {
            binding.textViewThirdPartyInformationSet.text =
                "Third Party Information Set ${adapterPosition + 1}:"

            binding.editTextDriverName.apply {
                heading("Driver Name")
                inputMode(
                    EditTextInputMode.INPUT_TEXT
                )
                editTextWidthPercent(60)
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            driver.dirver_name = text
                        }
                    }
                )
            }
            binding.editTextPhoneNumber.apply {
                heading("Phone Number")
                inputMode(
                    EditTextInputMode.NUMBER
                )
                editTextWidthPercent(60)
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            driver.driver_phone = text
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
                            driver.driver_address = text
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
                            driver.driver_licence_number = text
                        }
                    }
                )
            }
            binding.editTextVehicleLicenseInformation.apply {
                heading("Driver Insurance Information")
                editTextWidthPercent(60)
                inputMode(
                    EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                    MultiLineConfig(1, 3)
                )
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            driver.vehicle_insurance_info = text
                        }
                    }
                )
            }
            binding.editTextInsuranceExpiryDate.apply {
                heading("Insurance Expiration Date")
                editTextWidthPercent(60)
                inputMode(EditTextInputMode.DATE)
                watchTextChange(
                    object : EditText.TextWatcher {
                        override fun textChanged(text: String) {
                            driver.insurance_expriary_date = text
                        }
                    }
                )
            }

            binding.spinnerInjuries.apply {
                heading("Injuries")
                driver.is_injury = "Yes"
                spinnerWidthPercent(60)
                addItems(
                    arrayListOf(
                        SpinnerData("Yes", "Yes"),
                        SpinnerData("No", "No")
                    ),
                    object : Spinner.OnItemSelectedListener {
                        override fun <T> selected(item: SpinnerData<T>) {
                            driver.is_injury = item.toShow
                        }
                    }
                )
            }

            binding.recyclerViewPassengerInfo.apply {
                layoutManager = LinearLayoutManager(context)
                passengerInformationAdapter = PassengerInformationAdapter(
                    showMessageCallback
                )
                driver.passengerAdapter = passengerInformationAdapter
                adapter = passengerInformationAdapter
            }

            binding.buttonAddMore.setOnClickListener {
                driver.passengerAdapter?.addOneMore()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            AddCrashReportDriverItemBinding.inflate(
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