package com.met.atims_reporter.ui.notification_list

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.databinding.ActivityNotificationListBinding
import com.met.atims_reporter.databinding.NotificationReadUnreadDialogBinding
import com.met.atims_reporter.enums.SuperActivityStatusBarColor
import com.met.atims_reporter.model.NotificationListResponce
import com.met.atims_reporter.model.NotificationObject
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.notification_list.adapter.NotificationListAdapter
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.model.Result
import com.met.atims_reporter.util.repository.Event
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class NotificationListActivity : AtimsSuperActivity(SuperActivityStatusBarColor.PRIMARY),
    KodeinAware {

    private lateinit var binding: ActivityNotificationListBinding
    private lateinit var notificationListAdapter: NotificationListAdapter


    override val kodein: Kodein by kodein()
    private val viewModelProvider: ViewModelProvider by instance()
    private lateinit var viewModel: ViewModel
    private val mTitle = arrayOf("Read Notification", "Unread Notification")
    private lateinit var notificationList: NotificationListResponce
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_notification_list)
        viewModel = androidx.lifecycle.ViewModelProvider(this, viewModelProvider)
            .get(ViewModel::class.java)
        setPageTitle("NOTIFICATION")

        enableBackButton()
        willHandleBackNavigation()
        getNotificationList()
        initView()
        bindToViewModel()
    }

    private fun initView() {
        binding.textNewMessage.setOnClickListener {
            try {
                var list = notificationList.unread
                if (list != null && list.size != 0) {
                    binding.textNoDataFound.visibility = View.GONE
                    binding.recycleviewNotificationList.visibility = View.VISIBLE
                    binding.recycleviewNotificationList.layoutManager =
                        LinearLayoutManager(this)

                    notificationListAdapter =
                        NotificationListAdapter(
                            this,
                            list,
                            object : NotificationListAdapter.Callback {
                                override fun onClick(nofiOBJ: NotificationObject) {
                                    notificationRead(nofiOBJ)
                                }

                            })
                    binding.recycleviewNotificationList.isNestedScrollingEnabled = false
                    binding.recycleviewNotificationList.adapter = notificationListAdapter
                } else {
                    binding.textNoDataFound.visibility = View.VISIBLE
                    binding.recycleviewNotificationList.visibility = View.GONE
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            binding.textNewMessage.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            binding.textOldMessage.setBackgroundColor(resources.getColor(R.color.textBlackHint))

        }
        binding.textOldMessage.setOnClickListener {
            try {
                var list = notificationList.read

                if (list != null && list.size != 0) {
                    binding.textNoDataFound.visibility = View.GONE
                    binding.recycleviewNotificationList.visibility = View.VISIBLE
                    binding.recycleviewNotificationList.layoutManager =
                        LinearLayoutManager(this)

                    notificationListAdapter =
                        NotificationListAdapter(
                            this,
                            list,
                            object : NotificationListAdapter.Callback {
                                override fun onClick(nofiOBJ: NotificationObject) {
                                    notificationUnRead(nofiOBJ)
                                }

                            })
                    binding.recycleviewNotificationList.isNestedScrollingEnabled = false
                    binding.recycleviewNotificationList.adapter = notificationListAdapter


                } else {
                    binding.textNoDataFound.visibility = View.VISIBLE
                    binding.recycleviewNotificationList.visibility = View.GONE
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            binding.textNewMessage.setBackgroundColor(resources.getColor(R.color.textBlackHint))
            binding.textOldMessage.setBackgroundColor(resources.getColor(R.color.colorPrimary))

        }
    }

    private fun getNotificationList() {
        showProgress()
        viewModel.getNotificationList()
    }

    override fun goBack() {
        viewModel.giveRepository().apply {
            getHomeGridItems()
            startActivity(
                UiUtil.clearStackAndStartNewActivity(
                    Intent(
                        this@NotificationListActivity,
                        Dashboard::class.java
                    )
                )
            )
            finish()
        }
    }

    private fun bindToViewModel() {
        viewModel.mediatorLiveDataNoificationList.observe(
            this,
            Observer<Event<NotificationListResponce>> { t ->
                hideProgress()
                if (t.shouldReadContent())
                    setAdapter(t.getContent()!!)
            }
        )
        viewModel.mediatorLiveDataNotificationListError.observe(
            this,
            Observer<Event<Result>> { t ->

                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

        viewModel.mediatorLiveDataNoificationReadUnRead.observe(
            this,
            Observer<Event<String>> { t ->
                hideProgress()
                if (t.shouldReadContent()) {
                    t.readContent()
                    getNotificationList()
                }
            }
        )
        viewModel.mediatorLiveDataNotificationListError.observe(
            this,
            Observer<Event<Result>> { t ->

                t?.let {
                    if (it.shouldReadContent())
                        handleGenericResult(it.getContent()!!)
                }
            }
        )

    }

    private fun setAdapter(notificationList: NotificationListResponce) {

        var list: ArrayList<NotificationObject> = ArrayList()
        list = notificationList.unread
        this.notificationList = notificationList

        if (list != null) {
            binding.textNoDataFound.visibility = View.GONE
            binding.recycleviewNotificationList.visibility = View.VISIBLE
            binding.recycleviewNotificationList.layoutManager =
                LinearLayoutManager(this)

            notificationListAdapter =
                NotificationListAdapter(this, list, object : NotificationListAdapter.Callback {
                    override fun onClick(nofiOBJ: NotificationObject) {
                        notificationRead(nofiOBJ)
                    }

                })
            binding.recycleviewNotificationList.isNestedScrollingEnabled = false
            binding.recycleviewNotificationList.adapter = notificationListAdapter
            binding.textNewMessage.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            binding.textOldMessage.setBackgroundColor(resources.getColor(R.color.textBlackHint))
        } else {
            binding.textNoDataFound.visibility = View.VISIBLE
            binding.recycleviewNotificationList.visibility = View.GONE
        }

    }

    @SuppressLint("SetTextI18n")
    private fun notificationRead(notificationObject: NotificationObject) {
        val dialogBinding = NotificationReadUnreadDialogBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )
        val customDialog = Dialog(this)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setContentView(dialogBinding.root)
        dialogBinding.textNotiTitle.text = notificationObject.title
        dialogBinding.textNotiDate.text = notificationObject.created_ts
        dialogBinding.textNotificationDesc.text = notificationObject.message

        dialogBinding.buttonAction.text = "Mark as read"

        customDialog.window
            ?.setLayout(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )

        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customDialog.setCancelable(true)
        customDialog.show()

        dialogBinding.imgClose.setOnClickListener {
            customDialog.dismiss()
        }
        dialogBinding.buttonAction.setOnClickListener {
            customDialog.dismiss()
            showProgress()

            viewModel.NotificationReadUnread(
                notificationManager = notificationObject.notification_master_id,
                isRead = "1"
            )

        }
    }

    @SuppressLint("SetTextI18n")
    private fun notificationUnRead(notificationObject: NotificationObject) {
        val dialogBinding = NotificationReadUnreadDialogBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )
        val customDialog = Dialog(this)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.setContentView(dialogBinding.root)
        dialogBinding.textNotiTitle.text = notificationObject.title
        dialogBinding.textNotiDate.text = notificationObject.created_ts
        dialogBinding.textNotificationDesc.text = notificationObject.message

        dialogBinding.buttonAction.text = "Mark as unread"

        customDialog.window
            ?.setLayout(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT
            )

        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customDialog.setCancelable(true)
        customDialog.show()

        dialogBinding.imgClose.setOnClickListener {
            customDialog.dismiss()
        }
        dialogBinding.buttonAction.setOnClickListener {
            customDialog.dismiss()
            showProgress()
            viewModel.NotificationReadUnread(
                notificationManager = notificationObject.notification_master_id,
                isRead = "0"
            )

        }
    }
}
