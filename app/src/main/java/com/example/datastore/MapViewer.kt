package com.example.datastore

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.datastore.ui.theme.DataStoreTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

class MapViewer : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GetPermissionAccess(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun GetPermissionAccess(modifier: Modifier = Modifier) {
    val c = LocalContext.current
    val client = remember { LocationServices.getFusedLocationProviderClient(c) }

    var currentAddress by remember { mutableStateOf("Current Location") }
    var status by remember { mutableStateOf("Location Permission Access") }
    var currentPoint by remember { mutableStateOf<GeoPoint?>(null) }

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            c, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            c, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    val perLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLoc = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLoc = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        status = when {
            fineLoc -> "Fine Location Granted"
            coarseLoc -> "Coarse Location Granted"
            else -> "Access Denied"
        }
    }

    fun getCurrentLocation() {
        if (!hasPermission()) {
            status = "Please request permission first"
            return
        }
        val tokenSource = CancellationTokenSource()
        try {
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, tokenSource.token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        currentPoint = GeoPoint(location.latitude, location.longitude)
                        currentAddress =
                            "Latitude ${location.latitude}  Longitude ${location.longitude}"
                    } else {
                        currentAddress = "Location not available. Turn on GPS and try again."
                    }
                }
        } catch (e: SecurityException) {
            status = "Access Denied"
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (hasPermission()) {
                status = "Location is already Granted"
            } else {
                perLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {
            Text("Request Location Permission")
        }
        Text(status)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { getCurrentLocation() }) {
            Text("Check Location")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(currentAddress)
        Spacer(modifier = Modifier.height(16.dp))

        OsmMap(
            point = currentPoint,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = currentPoint != null,
            onClick = {
                currentPoint?.let {
                    val uri = Uri.parse("geo:${it.latitude},${it.longitude}?q=${it.latitude},${it.longitude}(You are here)")
                    c.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            }
        ) {
            Text("Open in Maps App")
        }
    }
}

@Composable
fun OsmMap(point: GeoPoint?, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->

            val cfg = Configuration.getInstance()
            cfg.load(ctx, ctx.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))
            cfg.userAgentValue = "Unit5MapClassDemo/1.0"


            cfg.osmdroidBasePath = File(ctx.cacheDir, "osmdroid")
            cfg.osmdroidTileCache = File(ctx.cacheDir, "osmdroid/tiles")

            MapView(ctx).apply {

                setTileSource(TileSourceFactory.MAPNIK)

                tileProvider.clearTileCache()

                setMultiTouchControls(true)
                controller.setZoom(5.0)
                controller.setCenter(GeoPoint(22.0, 79.0))
            }
        },
        update = { map ->
            if (point != null) {
                map.controller.setZoom(16.0)
                map.controller.setCenter(point)

                map.overlays.clear()
                val marker = Marker(map).apply {
                    position = point
                    title = "You are here"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                map.overlays.add(marker)
                map.invalidate()
            }
        }
    )
}