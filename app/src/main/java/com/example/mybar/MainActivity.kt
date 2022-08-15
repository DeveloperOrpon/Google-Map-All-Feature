package com.example.mybar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mybar.service.LocationService
import com.google.android.gms.maps.SupportMapFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val mapFragment =supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
    }
}
