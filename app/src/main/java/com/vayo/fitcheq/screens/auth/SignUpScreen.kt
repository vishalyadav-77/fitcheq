package com.vayo.fitcheq.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.R
import com.vayo.fitcheq.viewmodels.AuthViewModel

@Composable
fun SignUpScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val toastMessage by authViewModel.toastMessage.collectAsStateWithLifecycle()
    val myTitleFont = FontFamily(Font(R.font.title_syarifa))
    val headingFont = FontFamily.Serif
    var passwordVisible by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val focusManager = LocalFocusManager.current
    val passwordFocusRequester = remember { FocusRequester() }
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_full),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.White),
                        startY = 0f,
                        endY = screenHeightPx * 0.3f // fade by 30% of screen height
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Text(
                "Fit Cheq",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = myTitleFont
            )
            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text(
                    "Lets Register \nAccount", fontSize = 42.sp, color = Color.Black,
                    fontWeight = FontWeight.ExtraBold, fontFamily = headingFont, lineHeight = 40.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text(
                    "Welcome!\nYour fashion journey begins here.",
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = headingFont
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                "Email",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = Color.Gray) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Red,
                    focusedContainerColor = Color(0xFFEEEEEE), //light gray
                    unfocusedContainerColor = Color(0xFFEEEEEE),
                    disabledContainerColor = Color(0xFFE0E0E0)
                )
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Password",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.Gray) },
                visualTransformation = if(passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().focusRequester(passwordFocusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus() // Hides keyboard
                    }
                ),
                trailingIcon = {
                val icon = if (passwordVisible) R.drawable.visibility_24px else R.drawable.visibility_off_24px
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = description,
                        tint = Color.Gray
                    )
                }
            },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    errorBorderColor = Color.Red,
                    focusedContainerColor = Color(0xFFEEEEEE),
                    unfocusedContainerColor = Color(0xFFEEEEEE),
                    disabledContainerColor = Color(0xFFE0E0E0)
                )
            )
            Spacer(modifier = Modifier.Companion.height(20.dp))

            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                    } else if (!isValidEmail(email)) {
                        Toast.makeText(context, "Please enter valid email", Toast.LENGTH_SHORT).show()
                    } else if (password.length < 6) {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    } else if (password.isBlank()) {
                    Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        authViewModel.signUp(email, password, navController)
                    }
                })
            {
                Text("Sign Up", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.Companion.height(20.dp))

            Row {
                Text("Already have an account? ")
                Text(
                    "Login", fontWeight = FontWeight.ExtraBold, color = Color.Blue,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    })
            }
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