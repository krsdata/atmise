package com.met.atims_reporter.ui.notification_list.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.met.atims_reporter.R
import com.met.atims_reporter.databinding.ChildNotificationListBinding
import com.met.atims_reporter.model.NotificationObject


class NotificationListAdapter(
    val mContext: Context,
    private val notificationList: ArrayList<NotificationObject>,   private val callback: Callback
) :
    RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(mContext).inflate(R.layout.child_notification_list, parent, false)
        return ViewHolder(
            ChildNotificationListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notificationList.get(position))
    }

    fun loadNewData(notificationList: ArrayList<NotificationObject>) {
        notificationList.clear()
        this.notificationList.addAll(notificationList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ChildNotificationListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(notificationList: NotificationObject) {
            binding.textNotiTitle.text = notificationList.title
            binding.textNotiDes.text = notificationList.message
            binding.textClockTime.text =
               (notificationList.created_ts)!!
            binding.llNoti.setOnClickListener {
                callback.onClick(notificationList)
            }
        }
    }

    interface Callback {
        fun onClick(nofiOBJ: NotificationObject)
    }
}