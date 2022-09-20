package com.met.atims_reporter.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.met.atims_reporter.core.KeyWordsAndConstants.DEVICE_FCM_TOKEN
import com.met.atims_reporter.repository.Repository
import com.sagar.android.logutilmaster.LogUtil
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class FirebaseMessagingService : FirebaseMessagingService(), KodeinAware {

    override val kodein: Kodein by kodein()

    private val repository: Repository by instance()
    private val logUtil: LogUtil by instance()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        logUtil.logI("got new token :$token")

        repository.getSharedPref()
            .edit()
            .putString(
                DEVICE_FCM_TOKEN,
                token
            )
            .apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            logUtil.logV(
                """
                    notification body :
                    ${it.body}
                """.trimIndent()
            )
        }

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            logUtil.logV(
                """
                    data payload :
                    ${remoteMessage.data}
                """.trimIndent()
            )
            try {
                val map: Map<String, String> = remoteMessage.data
                logUtil.logV(
                    """
                    data payload json :
                    ${JSONObject(map)}
                """.trimIndent()
                )
                repository.gotPushNotification(JSONObject(map))
            } catch (ex: Exception) {
                logUtil.logV("error $ex")
            }
        }
    }
}