package com.met.atims_reporter.application

import android.app.Application
import com.bugfender.sdk.Bugfender
import com.met.atims_reporter.BuildConfig
import com.met.atims_reporter.core.KeyWordsAndConstants.LOG_TAG
import com.met.atims_reporter.di.NetworkModule
import com.met.atims_reporter.di.SharedPrefModule
import com.met.atims_reporter.model.AddIncidentRequest
import com.met.atims_reporter.notification.NotificationMaster
import com.met.atims_reporter.repository.Repository
import com.sagar.android.logutilmaster.LogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ApplicationClass : Application(), KodeinAware {

    private val logUtil: LogUtil by instance()
     var mAddIncidentRequest:AddIncidentRequest? = null
    override fun onCreate() {
        super.onCreate()

        logUtil.logV("^^^ ATIMS App started. ^^^")

        //Bugfender.init(this, "wSEBx3u288m3czzAnt5s5yFot8ureJPp", BuildConfig.DEBUG)
        //Bugfender.enableCrashReporting()
        //Bugfender.enableUIEventLogging(this)
       // Bugfender.enableLogcatLogging() // optional, if you want logs automatically collected from logcat
    }

    override val kodein = Kodein.lazy {

        import(androidXModule(this@ApplicationClass))

        bind() from singleton {
            LogUtil(
                LogUtil.Builder()
                    .setCustomLogTag(LOG_TAG)
                    .setShouldHideLogInReleaseMode(false, BuildConfig.DEBUG)
            )
        }

        bind() from singleton { NetworkModule(instance()).apiInterface }

        bind() from singleton { SharedPrefModule(instance()).pref }

        bind() from singleton { NotificationMaster(instance()) }

        bind() from singleton {
            Repository(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.home.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.incident_details.frags.video.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.login.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.dashboard.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.startshift.list.ViewModelProvider(
                instance()
            )
        }
        bind() from provider {
            com.met.atims_reporter.ui.startshift.truck.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.fuel_report.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_fuel_report.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.incidents.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_incident.data.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_incident.media.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.pre_ops.step_two.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.pre_ops.step_three.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.pre_ops.step_four.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.crash_report.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_crash_report.step_one.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_crash_report.step_three.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.faq.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.sos.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.maintenance_report.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_maintenance_report.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.admin.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.inspection_list.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.inspection_details.one.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_inspection.step_two.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_inspection.step_three.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.add_inspection.step_four.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {

            com.met.atims_reporter.ui.notification_list.ViewModelProvider(
                instance()
            )
        }
        bind() from provider {
            com.met.atims_reporter.ui.extra_time_list.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.profile.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.report.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.forgot_password.ViewModelProvider(
                instance()
            )
        }

        bind() from provider {
            com.met.atims_reporter.ui.change_password.ViewModelProvider(
                instance()
            )
        }
        bind() from provider {
            com.met.atims_reporter.ui.add_extra_time.ViewModelProvider(
                instance()
            )
        }
    }
}
