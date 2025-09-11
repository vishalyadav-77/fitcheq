package com.vayo.fitcheq.screens.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vayo.fitcheq.viewmodels.AuthViewModel


@Composable
fun ResetPasswordScreen(navController: NavController, viewModel: AuthViewModel){
    val context = LocalContext.current
    val headingFont = FontFamily.Serif
    var email by remember { mutableStateOf("") }
    var spamVisibility by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()
        .padding(WindowInsets.statusBars.asPaddingValues())
    ) {

        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically){
            IconButton(  onClick = { navController.navigateUp()},
                modifier = Modifier
                    .size(45.dp)
                ) {
                Icon(
                    imageVector = Icons.Sharp.KeyboardArrowLeft,
                    contentDescription = "back button",
                )
            }
            Text("Back",fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Reset password",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = headingFont
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    "Enter your email below and click on reset, we will send you an email with instructions to reset your password",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = headingFont
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Email address",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", color = Color.Gray) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
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
                if (spamVisibility){
                    Spacer(modifier = Modifier.Companion.height(10.dp))
                    Text(
                        "We've sent you an email. If you don't see it in your inbox, please check your spam or junk folder.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.Companion.height(20.dp))
                Button(
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        viewModel.resetPassword(email,
                            onSuccess = {
                            Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_LONG).show()
                                spamVisibility = true
                        },
                            onError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            })
                    })
                {
                    Text("Reset", fontSize = 18.sp)
                }

        }
    }
}

