package com.example.datastore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datastore.ui.theme.DataStoreTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CA3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStoreTheme {
                RepCounterScreen()
            }
        }
    }
}

class RepViewModel : ViewModel() {

    private val _reps = MutableStateFlow(0)
    val reps: StateFlow<Int> = _reps

    fun increment() {
        _reps.value++
    }

    fun reset() {
        _reps.value = 0
    }
}

@Composable
fun RepCounterScreen(
    viewModel: RepViewModel = viewModel()
) {
    val reps by viewModel.reps.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Rep Counter",
            fontSize = 28.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Reps: $reps",
            fontSize = 35.sp
        )

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = { viewModel.increment() }
        ) {
            Text("Increment")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { viewModel.reset() }
        ) {
            Text("Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    DataStoreTheme {
        RepCounterScreen()
    }
}
