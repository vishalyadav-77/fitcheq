package com.vayo.fitcheq.screens.auth

import com.vayo.fitcheq.R
import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    val contentColor = if (isSelected) Color(0xFF1E88E5) else Color.Black

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFBBDEFB) else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
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
                    modifier = Modifier.size(48.dp)
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


//@Preview(showSystemUi = true)
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

    // Title section
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp,
                start = 10.dp,
                end = 10.dp,
            )
    ) {
        Text(
            text = "TELL US ABOUT YOURSELF",
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )


        Column(
            modifier = Modifier.Companion.fillMaxSize()
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Companion.Start
        ) {
            Text("Enter Your Name:", fontSize = 18.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors()
            )
          Spacer(modifier = Modifier.Companion.height(30.dp))

            // Gender Selection
            Text("GENDER:", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 8.dp)
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

            Spacer(modifier = Modifier.Companion.height(30.dp))

            //Age GROUP SELECTION
            Text("AGE:", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)

            // First row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableCard(
                    text = AgeGroup.BELOW_18.displayName,
                    isSelected = ageGroup == AgeGroup.BELOW_18,
                    onClick = { ageGroup = AgeGroup.BELOW_18 }
                )

                SelectableCard(
                    text = AgeGroup.AGE_18_25.displayName,
                    isSelected = ageGroup == AgeGroup.AGE_18_25,
                    onClick = { ageGroup = AgeGroup.AGE_18_25 }
                )

                SelectableCard(
                    text = AgeGroup.AGE_25_30.displayName,
                    isSelected = ageGroup == AgeGroup.AGE_25_30,
                    onClick = { ageGroup = AgeGroup.AGE_25_30 }
                )

                SelectableCard(
                    text = AgeGroup.AGE_30_PLUS.displayName,
                    isSelected = ageGroup == AgeGroup.AGE_30_PLUS,
                    onClick = { ageGroup = AgeGroup.AGE_30_PLUS }
                )
            }


            Spacer(modifier = Modifier.Companion.height(30.dp))

            //Height SELECTION
            Text("HEIGHT", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)

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


            Spacer(modifier = Modifier.Companion.height(30.dp))
// OCCUPATION SELECTION
            Text("OCCUPATION", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
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


            Spacer(modifier = Modifier.Companion.height(30.dp))

//BRANDS KON KON SI
            Text("WHICH BRANDS USUALLY MAKE UP YOUR WARDROBE? :", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                    SelectableCard(
                        text = PreferPlatform.cheap.displayName,
                        isSelected = preferplatform == PreferPlatform.cheap,
                        onClick = { preferplatform = PreferPlatform.cheap }
                    )

                    SelectableCard(
                        text = PreferPlatform.moderate.displayName,
                        isSelected = preferplatform == PreferPlatform.moderate,
                        onClick = { preferplatform = PreferPlatform.moderate }
                    )

                    SelectableCard(
                        text = PreferPlatform.expensive.displayName,
                        isSelected = preferplatform == PreferPlatform.expensive,
                        onClick = { preferplatform = PreferPlatform.expensive }
                    )
                }
            }

            Spacer(modifier = Modifier.Companion.height(30.dp))
//BODY TYPE
            Text("BODY TYPE:", fontSize = 16.sp, fontWeight = FontWeight.Companion.Bold)

            // First row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableCard(
                    text = BodyType.slim.displayName,
                    isSelected = bodyType == BodyType.slim,
                    onClick = { bodyType = BodyType.slim }
                )
                SelectableCard(
                    text = BodyType.athletic.displayName,
                    isSelected = bodyType == BodyType.athletic,
                    onClick = { bodyType = BodyType.athletic }
                )
                SelectableCard(
                    text = BodyType.average.displayName,
                    isSelected = bodyType == BodyType.average,
                    onClick = { bodyType = BodyType.average }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

// Second row
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableCard(
                    text = BodyType.muscular.displayName,
                    isSelected = bodyType == BodyType.muscular,
                    onClick = { bodyType = BodyType.muscular }
                )
                SelectableCard(
                    text = BodyType.plus_size.displayName,
                    isSelected = bodyType == BodyType.plus_size,
                    onClick = { bodyType = BodyType.plus_size }
                )
            }


            Button(modifier = Modifier.padding(8.dp), onClick = {
                val userProfile = UserProfile(
                    uId = currentUser?.uid ?: "",
                    name = name,
                    gender = gender,
                    ageGroup = ageGroup,
                    occupation = occupation,
                    preferPlatform = preferplatform,
                    profileCompleted = true,
                    height = height,
                    bodyType = bodyType
                )
                firestore.collection("users").document(currentUser?.uid ?: "").set(userProfile)
                    .addOnSuccessListener {
                        // Save to SharedPreferences
                        val sharedPreferences =
                            context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                        sharedPreferences.edit()
                            .putBoolean("profile_completed", true)
                            .putString("user_gender", gender)
                            .apply()

                        Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
                        navController.navigate(if (gender == "Male") AuthScreen.MaleHome.route else AuthScreen.FemaleHome.route) {
                            popUpTo(AuthScreen.UserProfile.route) { inclusive = true }
                        }
                    }
            }) {
                Text(
                    text = "Save Profile",
                    modifier = Modifier.Companion.fillMaxWidth(),
                    textAlign = TextAlign.Companion.Center
                )
            }
        }
    }
}