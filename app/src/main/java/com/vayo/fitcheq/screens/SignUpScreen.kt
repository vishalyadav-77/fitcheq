package com.vayo.fitcheq.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


//@Preview(showBackground = true)
//@Composable
//fun PreviewOtpScreen() {
//    val navController = rememberNavController() // Fake NavController
//    val fakeViewModel = FakeAuthViewModel() // Mock ViewModel
//
//    PhoneNumberScreen(navController, fakeViewModel)
//}

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(50.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(10.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            authViewModel.signUp(email, password)
        }) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { navController.navigate(AuthScreen.Login.route) }) {
            Text("Already have an account? Login")
        }
    }
    LaunchedEffect(authState) {
        authState?.let {
            if (it) {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(AuthScreen.Login.route)
            } else {
                Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
//class FakeAuthViewModel : ViewModel() {
//    private val _phoneNumber = MutableStateFlow("1234567890") // Fake phone number for preview
//    val phoneNumber: StateFlow<String> = _phoneNumber
//
//    fun setPhoneNumber(newNumber: String) {
//        _phoneNumber.value = newNumber
//    }
//
//    fun sendOtp() {
//        // Simulate sending OTP
//    }
//}