package com.example.mybar.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mybar.R
import com.example.mybar.distance.MapData
import com.example.mybar.service.LocationService
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request


open class HomeFragment : Fragment(),OnMapReadyCallback{
    private lateinit var mMap: GoogleMap
    private lateinit var currentPolyline:Polyline
    companion object{
        private var originLatitude: Double = 23.8103
        private var originLongitude: Double = 90.4125

        // Coordinates of a park nearby
        private var destinationLatitude: Double = 23.8203
        private var destinationLongitude: Double = 90.4225
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val currentLocation=LocationService
        val latitude=currentLocation.latitude
        val longitude=currentLocation.longitude

        val view= inflater.inflate(R.layout.fragment_home,container,false)
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyC1rk3DDuCKf1j0jO1oz3RmA8GhAKe4Vww")
        }
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment!!.getMapAsync { mMap ->
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear() //clear old markers

            val googlePlex = CameraPosition.builder()
                .target(LatLng(latitude, longitude))
                .zoom(12f)
                .bearing(0f)
                .tilt(45f)
                .build()


            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 4000, null)

            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(longitude, latitude))
                    .title("Current Location")
                    .icon(bitmapDescriptorFromVector(activity, R.drawable.ic_home_24))
            )
            mMap.setMapStyle(context?.let { MapStyleOptions.loadRawResourceStyle(it,R.raw.style) })
        }
    ///rote map
        mapFragment.getMapAsync {
            mMap = it
            val originLocation = LatLng(originLatitude, originLongitude)
            mMap.addMarker(MarkerOptions().position(originLocation))
            val destinationLocation = LatLng(destinationLatitude, destinationLongitude)
            mMap.addMarker(MarkerOptions().position(destinationLocation))
            val urll = getDirectionURL(originLocation, destinationLocation,"AIzaSyC1rk3DDuCKf1j0jO1oz3RmA8GhAKe4Vww")
            GetDirection(urll).execute()
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 14F))
        }

        return view
    }

    private fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = context?.let { ContextCompat.getDrawable(it, vectorResId) }
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap =
            Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        val originLocation = LatLng(originLatitude, originLongitude)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(originLocation))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLocation, 18F))
    }
    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }
    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    //inner class
    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result = ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, MapData::class.java)
                val path = ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.GREEN)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }



}