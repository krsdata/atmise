package com.met.atims_reporter.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.os.Looper
import com.google.android.gms.location.*
import com.met.atims_reporter.repository.Repository
import com.sagar.android.logutilmaster.LogUtil
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LocationMonitorForegroundService : Service(), KodeinAware {

    override val kodein: Kodein by kodein()
    private val repository: Repository by instance()
    private val logUtil: LogUtil by instance()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        startForeground(
            124,
            repository.getForegroundNotificationForLocationUpdate()
        )

        startLocationMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()

        fusedLocationProviderClient.removeLocationUpdates(
            locationCallback
        )

        logUtil.logV("location monitoring service destroyed")
    }

    @SuppressLint("MissingPermission")
    private fun startLocationMonitoring() {
        val locationRequest = LocationRequest()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 * 60
            smallestDisplacement = 10F
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                super.onLocationResult(result)

                result?.let {
                    it.locations.forEach { location : Location ->
                        logUtil.logV(
                            """
                                  latitude : ${location.latitude}
                                  longitude : ${location.longitude}
                                  accuracy : ${location.accuracy}
                              """.trimIndent()
                        )

                        repository.gotLocation(
                            location.latitude.toString(),
                            location.longitude.toString()
                        )
                    }
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
}