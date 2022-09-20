package com.met.atims_reporter.ui.splash

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.met.atims_reporter.R
import com.met.atims_reporter.core.AtimsSuperActivity
import com.met.atims_reporter.core.KeyWordsAndConstants.NON_CRITICAL_UPDATE_LAST_SHOWN
import com.met.atims_reporter.core.KeyWordsAndConstants.TIME_DIFF_TO_SHOW_NON_CRITICAL_UPDATE
import com.met.atims_reporter.databinding.ActivitySplashBinding
import com.met.atims_reporter.enums.SuperActivityStatusBarColor
import com.met.atims_reporter.model.CheckVersionResponse
import com.met.atims_reporter.repository.Repository
import com.met.atims_reporter.ui.dashboard.Dashboard
import com.met.atims_reporter.ui.login.LoginActivity
import com.met.atims_reporter.util.DialogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class SplashActivity : AtimsSuperActivity(SuperActivityStatusBarColor.WHITE), KodeinAware {

    override val kodein: Kodein by kodein()

    private val repository: Repository by instance()
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setLayout(R.layout.activity_splash)
        //showSplashBG()

        Handler().postDelayed(
            {
                checkForVersionUpdate()
            },
            1000
        )
    }

    private fun checkForVersionUpdate() {
        repository.mutableLiveDataCheckVersion.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        showUpdateAppMessage(it.getContent()!!)
                    }
                }
            }
        )
        repository.mutableLiveDataCheckVersionError.observe(
            this,
            Observer {
                it?.let {
                    if (it.shouldReadContent()) {
                        it.readContent()
                        moveToNext()
                    }
                }
            }
        )
        repository.checkVersion()
    }

    private fun moveToNext() {
        if (repository.isLoggedIn())
            startActivity(
                Intent(
                    this,
                    Dashboard::class.java
                )
            )
        else
            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                )
            )
        finish()
    }

    private fun showUpdateAppMessage(checkVersionResponse: CheckVersionResponse) {
        when (checkVersionResponse.update_type) {
            "critical" -> {
                showMessageWithOneButton(
                    message = "You must update to latest version of SSP Reporter App to continue using it.",
                    buttonText = "Update Now",
                    cancellable = false,
                    callback = object : DialogUtil.CallBack {
                        override fun buttonClicked() {
                            super.buttonClicked()

                            showAppInPlayStore()
                        }
                    }
                )
            }
            "normal" -> {
                if (shouldShowNonCriticalUpdateMessage())
                    showMessageWithTwoButton(
                        message = "New version of SSP Reporter App is available in play store. Kindly update the app now.",
                        cancellable = false,
                        buttonOneText = "Update",
                        buttonTwoText = "Later",
                        callback = object : DialogUtil.MultiButtonCallBack {

                            override fun buttonOneClicked() {
                                super.buttonOneClicked()

                                showAppInPlayStore()
                            }

                            override fun buttonTwoClicked() {
                                super.buttonTwoClicked()

                                moveToNext()
                            }
                        }
                    )
                else {
                    moveToNext()
                }
            }
        }
    }

    private fun showAppInPlayStore() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (exception: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }

        finish()
    }

    fun shouldShowNonCriticalUpdateMessage(): Boolean {
        val nonCriticalUpdateLastShownOn = repository.getSharedPref().getLong(
            NON_CRITICAL_UPDATE_LAST_SHOWN, 0L
        )

        if (nonCriticalUpdateLastShownOn == 0L) {
            repository.getSharedPref()
                .edit()
                .putLong(
                    NON_CRITICAL_UPDATE_LAST_SHOWN,
                    System.currentTimeMillis()
                )
                .apply()
            return true
        }

        if (
            System.currentTimeMillis() - nonCriticalUpdateLastShownOn >
            TIME_DIFF_TO_SHOW_NON_CRITICAL_UPDATE
        ) {
            repository.getSharedPref()
                .edit()
                .putLong(
                    NON_CRITICAL_UPDATE_LAST_SHOWN,
                    System.currentTimeMillis()
                )
                .apply()
            return true
        }
        return false
    }
}
