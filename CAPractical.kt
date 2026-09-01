package com.example.semester07

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.semester07.ui.theme.Semester07Theme

class CAPractical : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Semester07Theme {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    var name by remember { mutableStateOf("Ishitva Mishra") }
    var regNumber by remember { mutableStateOf("12307003") }
    var email by remember { mutableStateOf("ishitva.mishra@gmail.com") }
    var phone by remember { mutableStateOf("9876543210") }
    var bio by remember { mutableStateOf("Computer Science Student") }

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "My Profile", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(30.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row {
                Text(text = "Name: ", fontWeight = FontWeight.Bold)
                Text(text = name)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Text(text = "Registration No: ", fontWeight = FontWeight.Bold)
                Text(text = regNumber)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Text(text = "Email: ", fontWeight = FontWeight.Bold)
                Text(text = email)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Text(text = "Phone: ", fontWeight = FontWeight.Bold)
                Text(text = phone)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Text(text = "Bio: ", fontWeight = FontWeight.Bold)
                Text(text = bio)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = { showDialog = true }) {
            Text("Edit Profile")
        }

        if (showDialog) {
            var tempName by remember { mutableStateOf(name) }
            var tempReg by remember { mutableStateOf(regNumber) }
            var tempEmail by remember { mutableStateOf(email) }
            var tempPhone by remember { mutableStateOf(phone) }
            var tempBio by remember { mutableStateOf(bio) }

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Update Details") },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Name") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = tempReg, onValueChange = { tempReg = it }, label = { Text("Registration No") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = tempEmail, onValueChange = { tempEmail = it }, label = { Text("Email") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = tempPhone, onValueChange = { tempPhone = it }, label = { Text("Phone") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = tempBio, onValueChange = { tempBio = it }, label = { Text("Bio") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        name = tempName
                        regNumber = tempReg
                        email = tempEmail
                        phone = tempPhone
                        bio = tempBio
                        showDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview() {
    Semester07Theme {
        ProfileScreen()
    }
}
