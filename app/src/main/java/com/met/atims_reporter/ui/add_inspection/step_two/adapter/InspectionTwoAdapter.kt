package com.met.atims_reporter.ui.add_inspection.step_two.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ChildPreOpsVehicleBinding
import com.met.atims_reporter.model.InsectionQuestionLisResponse
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.widget.edittext.EditText
import com.met.atims_reporter.widget.edittext.EditTextInputMode
import com.met.atims_reporter.widget.edittext.MultiLineConfig
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData

class InspectionTwoAdapter(
    private val questionList: ArrayList<InsectionQuestionLisResponse>,
    private val callback: Callback,
    private val showMessageCallback: ShowMessageCallback
) :
    RecyclerView.Adapter<InspectionTwoAdapter.ViewHolder>() {

    init {
        questionList.forEach {
            it.answer = "yes"
        }
    }

    fun getList() = questionList

    interface Callback {
        fun takePicture(questionLisResponse: InsectionQuestionLisResponse)
    }

    fun valid(): Boolean {
        var valid = true
        questionList.forEach {
            if (it.imagePath == null || it.comments == "") {
//                valid = false
            }
        }

        if (!valid) {
            showMessageCallback.showMessageInDialog("Please provide image and comments for all questions")
        }

        return valid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildPreOpsVehicleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return questionList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(questionList[position])
    }

    inner class ViewHolder(private val binding: ChildPreOpsVehicleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(question: InsectionQuestionLisResponse) {
            val selections = arrayListOf(
                SpinnerData("yes", "yes"),
                SpinnerData("no", "no")
            )

            question.answer ?: run {
                question.answer = selections[0].data
            }

            binding.spinner.apply {
                heading(
                    question.inspection_vehicle_question
                )

                spinnerWidthPercent(40)

                addItems(
                    selections
                    ,
                    object : Spinner.OnItemSelectedListener {
                        override fun <T> selected(item: SpinnerData<T>) {
                            question.answer = item.data as String
                        }
                    }
                )

                if (question.answer != null) {
                    select<SpinnerData<String>>(question.answer!!)
                }
            }

            binding.editTextMessage.apply {
                inputMode(
                    EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                    MultiLineConfig(1, 3)
                )
                mandatory(false)
            }

            binding.appcompatImageViewCameraFilled.visibility = View.GONE

            if (question.imagePath != null) {
                binding.appcompatImageViewCameraStroked.visibility = View.INVISIBLE
                binding.appcompatImageViewCameraFilled.visibility = View.VISIBLE
            }

            if (question.comments != "") {
                binding.editTextMessage.apply {
                    visibility = View.VISIBLE
                    setText(question.comments)
                }
            }

            binding.appcompatImageViewMessage.setOnClickListener {
                binding.editTextMessage.visibility = View.VISIBLE
            }

            binding.appcompatImageViewCameraStroked.setOnClickListener {
                callback.takePicture(question)
            }
            binding.appcompatImageViewCameraFilled.setOnClickListener {
                callback.takePicture(question)
            }

            binding.editTextMessage.watchForTextChange(
                object : EditText.TextWatcher {
                    override fun textChanged(text: String) {
                        question.comments = text
                    }
                }
            )
        }
    }

}