package com.vayo.fitcheq.screens.auth

import com.vayo.fitcheq.R
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.data.model.AgeGroup
import com.vayo.fitcheq.data.model.BodyType
import com.vayo.fitcheq.data.model.HeightGroup
import com.vayo.fitcheq.data.model.PreferPlatform
import com.vayo.fitcheq.data.model.UserProfile

@Composable
fun SelectableCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
   @DrawableRes iconRes: Int? = null
) {
    val contentColor = if (isSelected) Color(0xFF195184) else Color.Black

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFCFDFF6) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            if (iconRes != null) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = text,
                    tint = contentColor,
                    modifier = Modifier.size(68.dp)
                )
            } else {
                Text(
                    text = text,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var ageGroup by remember { mutableStateOf(AgeGroup.UNSPECIFIED) }
    var preferplatform by remember { mutableStateOf(PreferPlatform.moderate) }
    var height by remember { mutableStateOf(HeightGroup.average) }
    var bodyType by remember { mutableStateOf(BodyType.average) }
    var phoneNumber by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // MAIN
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(WindowInsets.statusBars.asPaddingValues())
    ) {
        Spacer(modifier = Modifier.Companion.height(10.dp))
        // TITLE
        Text(
            text = "Tell Us About Yourself",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.Companion.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(color = Color(0xFFF8E1AB)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ps_info),
                contentDescription = "Info",
                tint = Color.Black,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(16.dp)
            )
            Text(
                text = "We use this information to improve user experience & suggestions",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }


        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.Companion.height(20.dp))
            Text("ENTER YOUR NAME", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(10.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { input ->
                    // Capitalize first letter of each word
                    name = input.split(" ")
                        .joinToString(" ") { word ->
                            word.lowercase().replaceFirstChar { it.uppercase() }
                        }
                },
                placeholder = { Text("Enter Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.LightGray,
                    disabledBorderColor = Color.LightGray
                )
            )
            Spacer(modifier = Modifier.Companion.height(20.dp))

            Text("PHONE NUMBER", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(10.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    // Only allow digits
                    val filtered = newValue.filter { it.isDigit() }
                    phoneNumber = filtered
                    isError = false // Clear error when user types
                },
                placeholder = { Text("Phone Number (+91)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone"
                    )
                },
                shape = MaterialTheme.shapes.medium,
                isError = isError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color.LightGray,
                    disabledBorderColor = Color.LightGray
                )

            )
            Spacer(modifier = Modifier.Companion.height(20.dp))

            // Gender Selection
            Text("GENDER:", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectableCard(
                        text = "Male",
                        isSelected = gender == "Male",
                        onClick = { gender = "Male" },
                        iconRes = R.drawable.male_icon
                    )
                    Text("Male", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.Companion.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectableCard(
                        text = "Female",
                        isSelected = gender == "Female",
                        onClick = { gender = "Female" },
                        iconRes = R.drawable.female_icon
                    )
                    Text("Female", fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.Companion.height(20.dp))

            //Age GROUP SELECTION
            Text("AGE", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableCard(
                    text = "  ${AgeGroup.BELOW_18.displayName}  ",
                    isSelected = ageGroup == AgeGroup.BELOW_18,
                    onClick = { ageGroup = AgeGroup.BELOW_18 }
                )
                SelectableCard(
                    text = "  ${AgeGroup.AGE_18_25.displayName}  ",
                    isSelected = ageGroup == AgeGroup.AGE_18_25,
                    onClick = { ageGroup = AgeGroup.AGE_18_25 }
                )
                SelectableCard(
                    text = "  ${AgeGroup.AGE_25_30.displayName}  ",
                    isSelected = ageGroup == AgeGroup.AGE_25_30,
                    onClick = { ageGroup = AgeGroup.AGE_25_30 }
                )
                SelectableCard(
                    text = "  ${AgeGroup.AGE_30_PLUS.displayName}  ",
                    isSelected = ageGroup == AgeGroup.AGE_30_PLUS,
                    onClick = { ageGroup = AgeGroup.AGE_30_PLUS }
                )
            }
            Spacer(modifier = Modifier.Companion.height(20.dp))

            //Height SELECTION
            Text("HEIGHT", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(10.dp))
            // First row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableCard(
                    text = HeightGroup.short.displayName,
                    isSelected = height == HeightGroup.short,
                    onClick = { height = HeightGroup.short }
                )
                SelectableCard(
                    text = HeightGroup.average.displayName,
                    isSelected = height == HeightGroup.average,
                    onClick = { height = HeightGroup.average }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Second row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableCard(
                    text = HeightGroup.tall.displayName,
                    isSelected = height == HeightGroup.tall,
                    onClick = { height = HeightGroup.tall }
                )
                SelectableCard(
                    text = HeightGroup.very_tall.displayName,
                    isSelected = height == HeightGroup.very_tall,
                    onClick = { height = HeightGroup.very_tall }
                )
            }
            Spacer(modifier = Modifier.Companion.height(20.dp))

            // OCCUPATION SELECTION
            Text("OCCUPATION", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(10.dp))
            // First row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableCard(
                    text = "College Student",
                    isSelected = occupation == "College Student",
                    onClick = { occupation = "College Student" }
                )
                SelectableCard(
                    text = "Working Professional",
                    isSelected = occupation == "Working Professional",
                    onClick = { occupation = "Working Professional" }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Second row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableCard(
                    text = "School Student",
                    isSelected = occupation == "School Student",
                    onClick = { occupation = "School Student" }
                )
                SelectableCard(
                    text = "Other",
                    isSelected = occupation == "Other",
                    onClick = { occupation = "Other" }
                )
            }
            Spacer(modifier = Modifier.Companion.height(20.dp))

            // BRANDS KON KON SI
            Text(
                "WHICH BRANDS USUALLY MAKE UP YOUR WARDROBE?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Companion.Bold
            )
            Spacer(modifier = Modifier.Companion.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ImageSelectableCard(
                        images = listOf(R.drawable.logo_meesho, R.drawable.logo_amazon, R.drawable.logo_flipkart2,R.drawable.logo_tigc, R.drawable.logo_vastrado),
                        isSelected = preferplatform == PreferPlatform.cheap,
                        onClick = { preferplatform = PreferPlatform.cheap }
                    )
                    ImageSelectableCard(
                        images = listOf(R.drawable.logo_myntra, R.drawable.logo_hm,R.drawable.logo_ajio, R.drawable.logo_savana, R.drawable.logo_newme),
                        isSelected = preferplatform == PreferPlatform.moderate,
                        onClick = { preferplatform = PreferPlatform.moderate }
                    )
                    ImageSelectableCard(
                        images = listOf(R.drawable.logo_zara, R.drawable.logo_nykaa,R.drawable.logo_cray, R.drawable.logo_only ,R.drawable.logo_bershka),
                        isSelected = preferplatform == PreferPlatform.expensive,
                        onClick = { preferplatform = PreferPlatform.expensive }
                    )
                }
            }
            Spacer(modifier = Modifier.Companion.height(20.dp))

            //BODY TYPE
            Text("BODY TYPE", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(10.dp))
            // First row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectableCard(
                        text = BodyType.slim.displayName,
                        isSelected = bodyType == BodyType.slim,
                        onClick = { bodyType = BodyType.slim },
                        iconRes = R.drawable.male_skinny
                    )
                    Text(BodyType.slim.displayName, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectableCard(
                        text = BodyType.athletic.displayName,
                        isSelected = bodyType == BodyType.athletic,
                        onClick = { bodyType = BodyType.athletic },
                        iconRes = R.drawable.male_athletic
                    )
                    Text(BodyType.athletic.displayName, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectableCard(
                        text = BodyType.average.displayName,
                        isSelected = bodyType == BodyType.average,
                        onClick = { bodyType = BodyType.average },
                        iconRes = R.drawable.male_normal
                    )
                    Text(BodyType.average.displayName, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Second row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectableCard(
                        text = BodyType.muscular.displayName,
                        isSelected = bodyType == BodyType.muscular,
                        onClick = { bodyType = BodyType.muscular },
                        iconRes = R.drawable.male_athletic
                    )
                    Text(BodyType.muscular.displayName, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectableCard(
                        text = BodyType.plus_size.displayName,
                        isSelected = bodyType == BodyType.plus_size,
                        onClick = { bodyType = BodyType.plus_size },
                        iconRes = R.drawable.male_plus_size
                    )
                    Text(BodyType.plus_size.displayName, fontSize = 14.sp)
                }

            }
            Spacer(modifier = Modifier.Companion.height(20.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    if (name.isBlank() || gender.isBlank() || occupation.isBlank()) {
                        Toast.makeText(context, "Required field cannot be empty", Toast.LENGTH_SHORT).show()
                    } else {
                        val userProfile = UserProfile(
                            uId = currentUser?.uid ?: "",
                            name = name,
                            gender = gender,
                            phone = phoneNumber,
                            ageGroup = AgeGroup.valueOf(ageGroup.name),
                            occupation = occupation,
                            preferPlatform = PreferPlatform.valueOf(preferplatform.name),
                            profileCompleted = true,
                            height = HeightGroup.valueOf(height.name),
                            bodyType = BodyType.valueOf(bodyType.name)
                        )
                        firestore.collection("users").document(currentUser?.uid ?: "")
                            .set(userProfile)
                            .addOnSuccessListener {
                                // Save to SharedPreferences
                                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                val gson = Gson()
                                val json = gson.toJson(userProfile)
                                prefs.edit().putString("user_profile", json).apply()
                                Log.d("UserProfile", "UserProfile saved to JSON ✅: $json")
                                Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
                                navController.navigate(if (gender == "Male") AuthScreen.MaleHome.route else AuthScreen.FemaleHome.route) {
                                    popUpTo(AuthScreen.UserProfile.route) { inclusive = true }
                                }
                            }
                    }
                })
            {
                Text(
                    text = "Save Profile",
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun ImageSelectableCard(
    images: List<Int>, // List of drawable resource IDs (max 5)
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isSelected) Color(0xFF195184) else Color.Black
    val radioButtonColor = if (isSelected) Color(0xFF195184) else Color.Gray

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFCFDFF6) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio button on the left
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = radioButtonColor,
                    unselectedColor = radioButtonColor
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Images row with equal spacing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                images.take(5).forEach { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}