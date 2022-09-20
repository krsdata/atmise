package com.met.atims_reporter.notification

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.met.atims_reporter.R
import com.met.atims_reporter.model.PushNotification
import com.met.atims_reporter.ui.notification_list.NotificationListActivity

class NotificationMaster(private val context: Context) {

    companion object {
        const val CHANNEL_ID_HIGH_IMPORTANCE = "notification_app_channel_high_importance"

        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "High Importance"
            val descriptionText = "this is the high importance notification channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID_HIGH_IMPORTANCE, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @TargetApi(Build.VERSION_CODES.O)
    fun showNotification(pushNotification: PushNotification) {
        try {
            val intent = Intent(context, NotificationListActivity::class.java)
            /*val pendingIntent: PendingIntent = PendingIntent.getActivity(context,
                0, intent, 0)*/

            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // setting the mutability flag
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID_HIGH_IMPORTANCE)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(pushNotification.title)
                .setContentText(pushNotification.body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun getNotificationForLocationMonitoring(): Notification? {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID_HIGH_IMPORTANCE)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setContentTitle("ATIMS")
            .setContentText("Incident on progress.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(false)

        return builder.build()
    }
}