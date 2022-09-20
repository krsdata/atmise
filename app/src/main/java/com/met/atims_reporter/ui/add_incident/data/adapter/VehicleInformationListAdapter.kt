package com.met.atims_reporter.ui.add_incident.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.VehicleInformationListItemBinding
import com.met.atims_reporter.enums.OperationMode
import com.met.atims_reporter.model.ColorResponce
import com.met.atims_reporter.model.IncidentDetails
import com.met.atims_reporter.model.VehicleInformation
import com.met.atims_reporter.model.VehicleMotoristModel
import com.met.atims_reporter.widget.edittext.EditText
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData

class VehicleInformationListAdapter(
    private val colorList: ArrayList<ColorResponce>,
    private val motoristTypeList: ArrayList<VehicleMotoristModel>,
    private val mode: OperationMode,
    private val incidentDetails: IncidentDetails? = null
) :
    RecyclerView.Adapter<VehicleInformationListAdapter.Holder>() {
    private val vehicleInformationList = arrayListOf(
        VehicleInformation()
    )

    fun setCount(count: Int) {
        vehicleInformationList.clear()
        for (i in 1..count) {
            vehicleInformationList.add(VehicleInformation())
        }
        notifyDataSetChanged()
    }

    fun getSelected(): ArrayList<VehicleInformation> {
        return vehicleInformationList
    }

    fun isValid(): Boolean {
        var valid = true
        vehicleInformationList.forEach {
            if (it.color.isEmpty() || it.plate_no.isEmpty() || it.vehicle_type.isEmpty())
                valid = false
        }

        return valid
    }

    fun setSelected(items: ArrayList<VehicleInformation>) {
        if (items.size == 0)
            return

        vehicleInformationList.clear()
        vehicleInformationList.addAll(items)

        notifyDataSetChanged()
    }

    inner class Holder(private val binding: VehicleInformationListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(vehicleInformation: VehicleInformation) {
            binding.editTextPlateNo.apply {
                inputMode(
                    EditTextInputMode.INPUT_TEXT_CAPS
                )
                mandatory(true)
                heading("License Plate")
                editTextWidthPercent(60)
                watchTextChange(object : EditText.TextWatcher {
                    override fun textChanged(text: String) {
                        vehicleInformation.plate_no = text
                    }
                })
            }

            if (colorList.size > 0) {
                vehicleInformation.colorResponce = colorList[0]
            }
            val colorResponseList: ArrayList<SpinnerData<ColorResponce>> = ArrayList()
            for (i in 0 until colorList.size) {
                colorResponseList.add(
                    SpinnerData(
                        colorList[i].color_name, colorList[i]
                    )
                )
            }
            binding.spinnerColor.heading("Vehicle Color")
            binding.spinnerColor.mandatory(true)
            binding.spinnerColor.spinnerWidthPercent(60)
            binding.spinnerColor.addItems(
                colorResponseList,
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        vehicleInformation.colorResponce = item.data as ColorResponce
                        vehicleInformation.color = vehicleInformation.colorResponce.color_name
                    }
                }
            )
            try {
                colorResponseList.forEach { itemInner ->
                    if (itemInner.data.color_id == vehicleInformation.color) {
                        binding.spinnerColor.select(
                            data = itemInner.toShow
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            if (motoristTypeList.size > 0)
                vehicleInformation.motoristModel = motoristTypeList[0]
            val list: ArrayList<SpinnerData<VehicleMotoristModel>> = ArrayList()
            for (i in 0 until motoristTypeList.size) {
                list.add(
                    SpinnerData(
                        motoristTypeList[i].type_name, motoristTypeList[i]
                    )
                )
            }
            binding.spinnerVehicleType.heading("Vehicle Type (Motorist)")
            binding.spinnerVehicleType.mandatory(true)
            binding.spinnerVehicleType.spinnerWidthPercent(60)
            binding.spinnerVehicleType.addItems(
                list,
                object : Spinner.OnItemSelectedListener {
                    override fun <T> selected(item: SpinnerData<T>) {
                        vehicleInformation.motoristModel = item.data as VehicleMotoristModel
                        vehicleInformation.vehicle_type = vehicleInformation.motoristModel.type_name
                    }
                }
            )
            list.forEach { item ->
                if (item.data.motorist_vehicle_type_id == vehicleInformation.vehicle_type)
                    binding.spinnerVehicleType.select(
                        data = item.data
                    )
            }

            if (vehicleInformation.plate_no.isNotEmpty())
                binding.editTextPlateNo.setText(
                    vehicleInformation.plate_no
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            VehicleInformationListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return vehicleInformationList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(vehicleInformationList[position])
    }
}