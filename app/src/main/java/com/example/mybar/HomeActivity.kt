package com.example.mybar

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.mybar.databinding.ActivityHomeBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.mybar.fragment.HomeFragment
import com.example.mybar.service.LocationService
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.app_bar_home.*


const val REQUEST_CODE_LOCATION_PERMISSION=1122
const val AUTOCOMPLETE_REQUEST_CODE = 1
private const val TAG ="HomeActivity"
class HomeActivity : AppCompatActivity() {
    companion object{
        var LOCATION_SERVICE_ID:Int=175
        var ACTION_START_LOCATION:String="startLocationService"
        var ACTION_STOP_LOCATION:String="stopLocationService"
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout:DrawerLayout
    var back_press :Long=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //check connection//
        if (!isNetworkAvailable(this)){
            startActivity(Intent(this,NoInternetActivity::class.java))
            overridePendingTransition(R.anim.from_right_in, R.anim.from_left_out);
        }
        Log.i(TAG,"Internet ${isNetworkAvailable(this)}")

        ///
        if (FirebaseAuth.getInstance().currentUser==null){
            logoutUser()
        }

         binding = ActivityHomeBinding.inflate(layoutInflater)
         setContentView(binding.root)
        binding.appBarHome.fab.setOnClickListener { view ->
            val app=LocationService
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            val currentLocation=LocationService
            val latitude=currentLocation.latitude
            val longitude=currentLocation.longitude
        }
        drawerLayout= binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home), drawerLayout)
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {  menuItem ->
            when (menuItem.itemId){
                R.id.nav_signout -> {
                    FirebaseAuth.getInstance().signOut()
                    logoutUser()
                    true
                }
                R.id.nav_home -> {
                    val fragmentTransaction=supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_home,HomeFragment()).commit()
                    drawerLayout.close()
                    true
                }
                R.id.nav_setting->{
                    startActivity(Intent(this,EditUserProfileActivity::class.java))
                    true
                }
                R.id.nav_history->{
                    Toast.makeText(this,"History Mode",Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_driver->{
                    Toast.makeText(this,"Diver Switch Mode",Toast.LENGTH_SHORT).show()
                    true
                }
                else->{
                    true
                }
            }
        }
        toolbar.setOnClickListener {
            drawerLayout.open()
        }

        //Auto Complite Place here
//        Places.initialize(applicationContext,"AIzaSyC1rk3DDuCKf1j0jO1oz3RmA8GhAKe4Vww");
//        val placesClient = Places.createClient(this)
//        val autocompleteFragment =
//            supportFragmentManager.findFragmentById(R.id.autoCompletePlace)
//                    as AutocompleteSupportFragment
//        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS)
//        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
//            LatLng(-33.880490, 151.184363),
//            LatLng(-33.858754, 151.229596)
//        ))
//        autocompleteFragment.setCountries("BD")
//        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME))
//        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
//            override fun onError(status: Status) {
//                Log.i(TAG, "An error occurred: $status")
//                val parentLayout: View = findViewById(android.R.id.content)
//                Snackbar.make(parentLayout, "Retry Again $status", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//            }
//
//            override fun onPlaceSelected(place: Place) {
//                Log.i(TAG, "Place: ${place.name}, ${place.id}")
//            }
//
//        })
        ///end
        searchLocation.setOnClickListener {

        }

    }

    private fun logoutUser() {
        startActivity(Intent(this,LoginActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.action_logout){
            FirebaseAuth.getInstance().signOut()
            logoutUser()
        }
        if (item.itemId==R.id.action_switch_to_drive){
            Toast.makeText(this,"Switch To Dive Mode",Toast.LENGTH_SHORT).show()
        }
        if (item.itemId==R.id.action_settings){
            Toast.makeText(this,"Setting Mode",Toast.LENGTH_SHORT).show()
        }
        if (item.itemId==R.id.action_history){
            Toast.makeText(this,"History Mode",Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (back_press+1000> System.currentTimeMillis()){
            super.onBackPressed()
        }
        else{
            Toast.makeText(this,"Please Press Again To Exit",Toast.LENGTH_SHORT).show()
        }
        Log.i(TAG,"BackPress $back_press")
        back_press = System.currentTimeMillis()
    }
    ///Network Check
    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
    ///////////
    private fun isLocationServiceRunning():Boolean{
        val activityManager=getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (activityManager!=null){
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)){
                if (LocationService::class.java.name.equals(service.service.className)){
                    if (service.foreground){
                        return true
                    }
                }
            }
            return false
        }
        return false
    }

    override fun onStart() {
        if (ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION_PERMISSION)
            Log.i(TAG,"Location Permission No Permission")
        }
        else{
            Log.i(TAG,"Location Permission ok")
            startLocationService()
        }
        super.onStart()
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()){
            Log.i(TAG,"startLocationService ${isLocationServiceRunning()}")
            val intent=Intent(applicationContext,LocationService::class.java)
            intent.action = ACTION_START_LOCATION
            startService(intent)
        }
        Log.i(TAG,"startLocationService ${isLocationServiceRunning()}")
    }
    private fun stopLocationService() {
        if (isLocationServiceRunning()){
            val intent=Intent(applicationContext,LocationService::class.java)
            intent.action = ACTION_STOP_LOCATION
            stopService(intent)
        }
    }

    override fun onStop() {
        stopLocationService()
        super.onStop()
    }
}