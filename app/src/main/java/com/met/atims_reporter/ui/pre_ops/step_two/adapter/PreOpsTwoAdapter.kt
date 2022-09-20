package com.met.atims_reporter.ui.pre_ops.step_two.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.databinding.ChildPreOpsVehicle2Binding
import com.met.atims_reporter.model.QuestionLisResponse
import com.met.atims_reporter.util.activity.ShowMessageCallback
import com.met.atims_reporter.widget.spinner.Spinner
import com.met.atims_reporter.widget.spinner.SpinnerData

class PreOpsTwoAdapter(
    private val questionList: ArrayList<QuestionLisResponse>,
    private val callback: Callback,
    private val showMessageCallback: ShowMessageCallback, val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<PreOpsTwoAdapter.ViewHolder>() {

    init {
        questionList.forEach {
            it.answer = "no"
        }
    }

    fun getList() = questionList

    interface Callback {
        fun takePicture(questionLisResponse: QuestionLisResponse)
    }

    interface OnItemClick {
        fun onClick(view: View, position: Int)
    }

    fun valid(): Boolean {
        var valid = true
        questionList.forEach {
            if (it.answer == "yes") {
                if (it.imagePath == null || it.comments == "") {
                    valid = false
                }
            }
        }

        if (!valid) {
//            showMessageCallback.showMessageInDialog("Please provide image and comments for all questions")
        }

        return valid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChildPreOpsVehicle2Binding.inflate(
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

        holder.bind()
    }

    inner class ViewHolder(private val binding: ChildPreOpsVehicle2Binding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind() {
//            binding.editTextMessage.setText("")
            val newVal = questionList[adapterPosition].comments
            Log.i("Sfbfdsb", " new val $newVal")
            binding.editTextMessage.setText(newVal)
            val selections = arrayListOf(
                SpinnerData("No", "no"),
                SpinnerData("Yes", "yes")
            )

            questionList[adapterPosition].answer ?: run {
                questionList[adapterPosition].answer = selections[0].data
            }

            binding.spinner.apply {
                heading(
                    questionList[adapterPosition].preops_vehicle_question
                )

                mandatory(false)

                spinnerWidthPercent(40)

                addItems(
                    selections,
                    object : Spinner.OnItemSelectedListener {
                        override fun <T> selected(item: SpinnerData<T>) {
                            questionList[adapterPosition].answer = item.toShow
                        }
                    }
                )

                if (questionList[adapterPosition].answer != null) {
                    select<SpinnerData<String>>(questionList[adapterPosition].answer!!)
                }
            }

            /* binding.editTextMessage.apply {
                 inputMode(
                     EditTextInputMode.INPUT_TEXT_MULTI_LINE,
                     MultiLineConfig(1, 3)
                 )

                 mandatory(false)
             }*/

            binding.appcompatImageViewCameraFilled.visibility = View.GONE

            if (questionList[adapterPosition].imagePath != null) {
                binding.appcompatImageViewCameraStroked.visibility = View.INVISIBLE
                binding.appcompatImageViewCameraFilled.visibility = View.VISIBLE
            }

            /*if (question.comments != "") {
                binding.editTextMessage.apply {
                    visibility = View.VISIBLE
                    setText(question.comments)
                }
                question.isCommentVisible = true
              //  notifyDataSetChanged()
            }*/

            if (questionList[adapterPosition].isCommentVisible) {
                binding.editTextMessage.apply {
                    visibility = View.VISIBLE
                }

            } else {
                binding.editTextMessage.visibility = View.GONE
            }


            binding.appcompatImageViewMessage.setOnClickListener {
                onItemClick.onClick(it, adapterPosition)
            }
            binding.appcompatImageViewCameraStroked.setOnClickListener {
                callback.takePicture(questionList[adapterPosition])
            }
            binding.appcompatImageViewCameraFilled.setOnClickListener {
                callback.takePicture(questionList[adapterPosition])
            }
            binding.editTextMessage.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.toString().isNotEmpty()) {
                        questionList[adapterPosition].comments = p0.toString()
                    } else {
                        questionList[adapterPosition].comments = ""
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })

            /*binding.editTextMessage.watchForTextChange(
                object : EditText.TextWatcher {
                    override fun textChanged(text: String) {
                        question.comments = text
                    }
                }
            )*/

        }

    }


}