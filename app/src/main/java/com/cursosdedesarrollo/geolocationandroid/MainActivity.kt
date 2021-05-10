package com.cursosdedesarrollo.geolocationandroid

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*


class MainActivity : AppCompatActivity(), LocationListener {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationCallback: LocationCallback

    private var texto: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        texto = findViewById<View>(R.id.texto) as TextView
        setSupportActionBar(toolbar)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    val posicion = "Longitud: " + location.longitude + ":Latitud: " + location.latitude
                    texto!!.text = posicion

                }
            }
        }

    }

    private val MY_PERMISSIONS_FINE_LOCATION = 1
    fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Toast.makeText(this, "Please grant permissions to fine location", Toast.LENGTH_LONG)
                    .show()

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_FINE_LOCATION
                )
                Log.d("app", "Pidiendo permisos")
            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_FINE_LOCATION
                )
                Log.d("app", "Mostrando diálogo")
            }
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

            //Go ahead with recording audio now
            Toast.makeText(this, "Permissions granted to fine location", Toast.LENGTH_LONG).show()
            requestPosition()
        }
    }

    //Handling callback
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        Log.d("app", "Cuando devuelven resultados de permisos")
        when (requestCode) {
            MY_PERMISSIONS_FINE_LOCATION -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay!
                    requestPosition()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permissions Denied to fine location", Toast.LENGTH_LONG)
                        .show()
                }
                return
            }
        }
    }

    private fun requestPosition() {
        Log.d("app", "Pidiendo posición")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        }
        mFusedLocationClient!!.lastLocation
            .addOnSuccessListener(
                this
            ) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    Log.d("app", "" + location)
                    texto!!.text =
                        "Longitud: " + location.longitude + ":Latitud: " + location.latitude
                } else {
                    Log.d("app", "Location es null")
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_get_location) {
            requestLocationPermissions()
            return true
        }
        if (id == R.id.action_watch_location) {
            watchLocation()
            return true
        }
        if (id == R.id.action_clear_watch_location) {
            clearWatchLocation()
            return true
        }
        if (id == R.id.action_get_location_older) {
            getLocationOlder()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    //define the listener
    private val locationListener = object : android.location.LocationListener {

        override fun onLocationChanged(location: Location) {
            val posicion =  "Longitud: " + location.longitude + ":Latitud: " + location.latitude
            texto!!.text = posicion
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Log.d("app","status = $status")
        }


    }
    private fun getLocationOlder(){

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        if (hasGps){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 5f, locationListener)
        }

    }
    private fun watchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        mFusedLocationClient?.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())



    }

    private fun clearWatchLocation() {
        mFusedLocationClient?.removeLocationUpdates(locationCallback)

    }
    /*
    override fun onResume() {
        super.onResume()
        watchLocation()
    }

    override fun onPause() {
        super.onPause()
        clearWatchLocation()
    }

     */

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}
    override fun onLocationChanged(location: Location) {
        val posicion =  "Longitud: " + location.longitude + ":Latitud: " + location.latitude
        texto!!.text = posicion

    }
}
