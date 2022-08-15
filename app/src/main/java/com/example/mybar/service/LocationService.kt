package com.example.mybar.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.mybar.R
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng

private const val TAG="LocationService"
open class LocationService : Service() {
    companion object{
        var LOCATION_SERVICE_ID:Int=175
        var ACTION_START_LOCATION:String="startLocationService"
        var ACTION_STOP_LOCATION:String="stopLocationService"
        public var latitude: Double = 0.0
        public var longitude: Double = 0.0
    }
    private var locationCallback: LocationCallback?=object :LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val locations = p0.locations
            latitude= p0.lastLocation?.latitude!!
            longitude= p0.lastLocation?.longitude!!
            Log.i(TAG,"Location $latitude and $longitude")
            //Toast.makeText(this@LocationService,"Location $latitude and $longitude and $locations",Toast.LENGTH_SHORT).show()
            super.onLocationResult(p0)
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        Log.i(TAG,"Location onBind")
        throw UnsupportedOperationException("Not Yet Implement")
    }
    private fun StartLocationService(){
        val channelid="locationNotificationChannel"
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent=Intent()
        val pendingIntent=PendingIntent.getActivity(applicationContext,0,resultIntent,FLAG_IMMUTABLE)
        val builder=NotificationCompat.Builder(
            applicationContext,channelid
        )
        builder.setSmallIcon(R.drawable.gublogo)
        builder.setContentTitle("Access Your Location ")
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        builder.setContentText("Running")
        builder.setContentIntent(pendingIntent)
        builder.setPriority(NotificationCompat.PRIORITY_MAX)
        builder.setAutoCancel(false)

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if (notificationManager.getNotificationChannel(channelid)==null){
                val notificationChannel=NotificationChannel(
                    channelid,
                    "MyBar",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.description="This Channel use for Location"
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
        val locationRequest=LocationRequest.create()
        locationRequest.setInterval(4000)
        locationRequest.setFastestInterval(2000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {


            return
        }
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback!!,Looper.getMainLooper())
        startForeground(LOCATION_SERVICE_ID,builder.build())
    }
    private fun stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationCallback!!)
        stopForeground(true)
        stopSelf()
    }

    ///First Call this Class Method
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(TAG,"Location onStartCommand")
        if (intent!=null){
            Log.i(TAG,"Location onStartCommand intent!=null")
            val action=intent.action
            if (action!=null){
                Log.i(TAG,"Location onStartCommand action!=null $action")
                if (action.equals(ACTION_START_LOCATION)){
                    StartLocationService()
                    Log.i(TAG,"Location onStartCommand StartLocationService() $action")
                }
                else if (action.equals(ACTION_STOP_LOCATION)){
                    stopLocationService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}