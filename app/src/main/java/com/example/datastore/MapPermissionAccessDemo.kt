package com.example.datastore

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.datastore.ui.theme.DataStoreTheme

class MapPermissionAccessDemo : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoreTheme {
                GetPermissionAccess()
            }
        }
    }
}

@Composable
fun GetPermissionAccess(){
    val c = LocalContext.current;
    var status by remember { mutableStateOf("Location Permission Access") }

    val perLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()){

        permissions ->
        val fineLoc = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLoc = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true


        status = when{
            fineLoc -> "Fine Location Granted"
            coarseLoc -> "Coarse Location Granted"

            else -> "Access Denied"
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Button(onClick = {
            val fg = ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val cg = ContextCompat.checkSelfPermission(c, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            if(fg || cg){
                status = if(fg){
                    "Fine Location is already Granted"
                } else{
                    "Coarse Location is already Granted"
                }
            } else{
                perLauncher.launch((arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)))
            }
        }){
            Text("Request Access Permission")
        }

        Text(status)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    DataStoreTheme {
        GetPermissionAccess()
    }
}