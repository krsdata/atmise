package com.met.atims_reporter.core

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.hold1.keyboardheightprovider.KeyboardHeightProvider
import com.met.atims_reporter.R
import com.met.atims_reporter.core.KeyWordsAndConstants.END_SELF
import com.met.atims_reporter.databinding.ActivityAtimsSuperBinding
import com.met.atims_reporter.databinding.ExtraTimeFilterDialogBinding
import com.met.atims_reporter.enums.SuperActivityStatusBarColor
import com.met.atims_reporter.interfaces.AtimsSuperActivityContract
import com.met.atims_reporter.interfaces.SuperActivityProfileCallback
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.login.LoginActivity
import com.met.atims_reporter.util.DialogUtil
import com.met.atims_reporter.util.UiUtil
import com.met.atims_reporter.util.activity.SuperActivity
import com.met.atims_reporter.widget.edittext.EditTextInputMode


open class AtimsSuperActivity(
    private val superActivityStatusBarColor: SuperActivityStatusBarColor = SuperActivityStatusBarColor.PRIMARY,
    private val canBeLastActivity: Boolean = false
) :
    SuperActivity(), AtimsSuperActivityContract, SuperActivityProfileCallback {

    private lateinit var superBinding: ActivityAtimsSuperBinding
    private lateinit var keyboardHeightProvider: KeyboardHeightProvider
    private var backButtonEnabled = false
    private var backHandledByChild = false


    private val endSelfBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    private fun registerForEndSelf() {
        registerReceiver(
            endSelfBroadcastReceiver,
            IntentFilter(
                END_SELF
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(endSelfBroadcastReceiver)
    }

    protected fun endAllActivities() {
        sendBroadcast(
            Intent(
                END_SELF
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atims_super)
        UiUtil.makeFullScreen(this)

        superBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_atims_super
        )
        superBinding.context = this

        ViewCompat.setOnApplyWindowInsetsListener(superBinding.content.container) { _, insets ->
            superBinding.content.constraintLayoutInnerContainer.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        cleanView()

        setUpKeyBoardHeightListener()

        registerForEndSelf()

        setUpStatusBarColor()
    }

    private fun cleanView() {
        superBinding.content.appcompatImageViewBack.visibility = View.GONE
        superBinding.content.textViewTitle.visibility = View.GONE
        superBinding.content.appcompatImageViewNotification.visibility = View.GONE
        superBinding.content.appcompatImageViewLogout.visibility = View.GONE
        superBinding.content.textViewNotificationCount.visibility = View.GONE
        superBinding.content.buttonEditProfile.visibility = View.GONE
        superBinding.content.buttonSaveProfile.visibility = View.GONE
        superBinding.content.constraintLayoutAddIncident.visibility = View.GONE
        superBinding.content.constraintLayoutAddFuelReport.visibility = View.GONE
        superBinding.content.appCompatImageViewFilterExtraTimeList.visibility = View.GONE
        superBinding.content.constraintLayoutAddCrashReport.visibility = View.GONE
        superBinding.content.constraintLayoutAddInspection.visibility = View.GONE
        superBinding.content.constraintLayoutAddMaintenanceReport.visibility = View.GONE
        superBinding.content.appcompatImageViewProfileBg.visibility = View.GONE
        superBinding.content.constraintLayoutCancel.visibility = View.GONE
        //superBinding.content.ivAddExtraTimeList.visibility = View.GONE
    }

    private fun getContainer(): NestedScrollView {
        return superBinding.content.frameContainer
    }

    private fun setView(view: View) {
        getContainer().addView(view)
    }

    protected fun <T : ViewDataBinding> setLayout(@Suppress("SameParameterValue") layout: Int): T {
        val binding: T = DataBindingUtil.inflate(
            layoutInflater,
            layout,
            null,
            false
        )
        setView(binding.root)
        return binding
    }

    protected fun enableBackButton() {
        backButtonEnabled = true
        superBinding.content.appcompatImageViewBack.visibility = View.VISIBLE
    }

    fun setPageTitle(title: String) {
        if (!backButtonEnabled)
            superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.textViewTitle.visibility = View.VISIBLE
        superBinding.content.textViewTitle.text = title
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (backHandledByChild){
            goBack()
        }
        else {
            /*if (!canBeLastActivity && isThisLastActivity()) {
                sendBroadcast(
                    Intent("getHomeGrid")
                )
                startActivity(
                    UiUtil.clearStackAndStartNewActivity(
                        Intent(
                            this,
                            Dashboard::class.java
                        )
                    )
                )
            }*/
            finish()
        }
    }

    protected fun willHandleBackNavigation() {
        backHandledByChild = true
    }

    private fun setUpKeyBoardHeightListener() {
        keyboardHeightProvider = KeyboardHeightProvider(this)
        keyboardHeightProvider.addKeyboardListener(
            object : KeyboardHeightProvider.KeyboardListener {
                override fun onHeightChanged(height: Int) {
                    val params = superBinding.content.filler.layoutParams
                    params.height = if (height == 0) 1 else height
                    superBinding.content.filler.layoutParams = params
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        keyboardHeightProvider.onResume()
        registerForNotAuthorisedBroadcast()

        isRunning(this)
    }

    override fun onPause() {
        super.onPause()
        keyboardHeightProvider.onPause()
        unregisterReceiver(broadcastReceiverNotAuthorised)
    }

    private fun setUpStatusBarColor() {
        when (superActivityStatusBarColor) {
            SuperActivityStatusBarColor.PRIMARY ->
                superBinding.content.container.setBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.colorPrimary,
                        null
                    )
                )
            SuperActivityStatusBarColor.WHITE ->
                superBinding.content.container.setBackgroundColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.white,
                        null
                    )
                )
        }
    }

    fun showDashboardActionbar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.appcompatImageViewNotification.visibility = View.VISIBLE
        superBinding.content.appcompatImageViewLogout.visibility = View.VISIBLE
    }

    protected fun notificationCount(count: Int) {
        if (count == 0) {
            superBinding.content.textViewNotificationCount.visibility = View.GONE
            return
        }
        superBinding.content.textViewNotificationCount.visibility = View.VISIBLE
        superBinding.content.textViewNotificationCount.text =
            if (count <= 99) count.toString() else "99+"
    }

    fun showProfileActionBar() {
        cleanView()
        superBinding.content.appcompatImageViewProfileBg.visibility = View.VISIBLE
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.buttonEditProfile.visibility = View.VISIBLE
    }

    protected fun editingProfile() {
        superBinding.content.buttonEditProfile.visibility = View.GONE
        superBinding.content.buttonSaveProfile.visibility = View.VISIBLE
    }

    protected fun doneSavingProfile() {
        superBinding.content.buttonEditProfile.visibility = View.VISIBLE
        superBinding.content.buttonSaveProfile.visibility = View.GONE
    }

    fun showIncidentsActionBar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.constraintLayoutAddIncident.visibility = View.VISIBLE
    }

    fun showSOSBar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.constraintLayoutCancel.visibility = View.VISIBLE
    }

    fun showFuelReportActionBar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.constraintLayoutAddFuelReport.visibility = View.VISIBLE
    }

    fun showFuelReportDeatilsActionBar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.constraintLayoutAddFuelReport.visibility = View.INVISIBLE
    }

    fun showCrashReportListActionBar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.constraintLayoutAddCrashReport.visibility = View.VISIBLE
    }

    fun showInspectionListActionBar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.constraintLayoutAddInspection.visibility = View.VISIBLE
    }

    fun showExtraTimeListActionBar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.appCompatImageViewFilterExtraTimeList.visibility = View.VISIBLE
      //  superBinding.content.ivAddExtraTimeList.visibility = View.VISIBLE
    }

    fun showExtraTimeFilterPopUp() {
        val extraTimeFilterDialogBinding = ExtraTimeFilterDialogBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )

        extraTimeFilterDialogBinding.editTextShiftStartTime.apply {
            heading("Start Date:")
            inputMode(EditTextInputMode.DATE)
        }
        extraTimeFilterDialogBinding.editTextShiftEndTime.apply {
            heading("End Date:")
            inputMode(EditTextInputMode.DATE)
        }

        val popup = PopupWindow(
            extraTimeFilterDialogBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popup.apply {
            setFinishOnTouchOutside(true)
            showAsDropDown(
                superBinding.content.appCompatImageViewFilterExtraTimeList
            )
        }

        extraTimeFilterDialogBinding.container.setOnClickListener {
            popup.dismiss()
        }

        extraTimeFilterDialogBinding.buttonApply.setOnClickListener {
            if (
                extraTimeFilterDialogBinding.editTextShiftStartTime.getText().isEmpty() ||
                extraTimeFilterDialogBinding.editTextShiftEndTime.getText().isEmpty()
            )
                return@setOnClickListener

            if (
                extraTimeFilterDialogBinding.editTextShiftStartTime.getTimeMills() >
                extraTimeFilterDialogBinding.editTextShiftEndTime.getTimeMills()
            )
                return@setOnClickListener

            filterExtraTimeList(
                extraTimeFilterDialogBinding.editTextShiftStartTime.getText(),
                extraTimeFilterDialogBinding.editTextShiftEndTime.getText()
            )

            popup.dismiss()
        }
    }

    fun showMaintenanceReportListListActionBar() {
        cleanView()
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.constraintLayoutAddMaintenanceReport.visibility = View.VISIBLE
    }

    fun showSplashBG() {
        cleanView()
        superBinding.content.appcompatImageViewSplashBg.apply {
            visibility = View.VISIBLE
            setImageDrawable(getDrawable(R.drawable.splash_bg))
        }
        superBinding.content.appcompatImageViewBack.visibility = View.INVISIBLE
        superBinding.content.buttonEditProfile.visibility = View.INVISIBLE
    }

    private val broadcastReceiverNotAuthorised = object : BroadcastReceiver() {

        override fun onReceive(p0: Context?, p1: Intent?) {
            hideProgressForced()
            showMessageWithOneButton(
                message = "You are loggedOut from server.",
                cancellable = false,
                callback = object : DialogUtil.CallBack {

                    override fun buttonClicked() {
                        endAllActivities()
                        startActivity(
                            UiUtil.clearStackAndStartNewActivity(
                                Intent(
                                    this@AtimsSuperActivity,
                                    LoginActivity::class.java
                                )
                            )
                        )
                        finish()
                    }
                }
            )
        }
    }

    private fun registerForNotAuthorisedBroadcast() {
        registerReceiver(
            broadcastReceiverNotAuthorised,
            IntentFilter(
                KeyWordsAndConstants.NOT_AUTHORISED
            )
        )
    }

    private fun isThisLastActivity(): Boolean {
        val nos = isTaskRoot

        return nos
    }

    open fun isRunning(ctx: Context) {
        val activityManager =
            ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks =
            activityManager.getRunningTasks(Int.MAX_VALUE)
        for (task in tasks) {
            Log.i("dfbgbdbg", "${task.baseActivity!!.className}")
            if (ctx.packageName.equals(task.baseActivity!!.packageName, ignoreCase = true)) {
            }
        }
    }
}
