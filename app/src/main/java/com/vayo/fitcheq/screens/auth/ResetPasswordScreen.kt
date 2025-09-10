package com.vayo.fitcheq.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.vayo.fitcheq.viewmodels.AuthViewModel


@Composable
fun ResetPasswordScreen(navController: NavController, viewModel: AuthViewModel){
    val headingFont = FontFamily.Serif
    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()
        .padding(WindowInsets.statusBars.asPaddingValues())
        .padding(horizontal = 20.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Start){
            IconButton(  onClick = { navController.navigateUp() },
                ) {
                Icon(
                    imageVector = Icons.Sharp.KeyboardArrowLeft,
                    contentDescription = "back button",
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Back")
        }
        Spacer(modifier = Modifier.height(30.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Reset password",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = headingFont
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Enter your email below and click on reset, we will send you link with instructions to reset your password",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = headingFont
            )
        }
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Companion.CenterHorizontally) {
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
            Spacer(modifier = Modifier.Companion.height(20.dp))

            Text(
                "Email address",
                modifier = Modifier.fillMaxWidth(),
                fontSize = 20.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    // do action from viewmodel
                })
            {
                Text("Reset", fontSize = 18.sp)
            }
        }
    }
}