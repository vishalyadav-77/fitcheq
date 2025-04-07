package com.vayo.fitcheq.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.viewmodels.AuthViewModel

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val toastMessage by authViewModel.toastMessage.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.Companion.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.Companion.height(50.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.Companion.height(10.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.Companion.height(20.dp))

        Button(onClick = {
            authViewModel.signUp(email, password, navController)
        }) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.Companion.height(10.dp))

        Button(onClick = { navController.navigate(AuthScreen.Login.route) }) {
            Text("Already have an account? Login")
        }
    }

    // Show toast messages from AuthViewModel
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            authViewModel.clearToastMessage()
        }
    }

    LaunchedEffect(authState) {
        authState?.let {
            if (it) {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(AuthScreen.UserProfile.route) {
                    popUpTo(AuthScreen.SignUp.route) { inclusive = true }
                }
            }
        }
    }
}