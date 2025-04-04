package com.vayo.fitcheq.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.AuthViewModel
import com.vayo.fitcheq.data.model.AgeGroup
import com.vayo.fitcheq.data.model.BodyType
import com.vayo.fitcheq.data.model.HeightGroup
import com.vayo.fitcheq.data.model.PreferPlatform
import com.vayo.fitcheq.data.model.UserProfile

//@Preview(showSystemUi = true)
@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel){
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = FirebaseAuth.getInstance().currentUser

    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var ageGroup by remember { mutableStateOf(AgeGroup.UNSPECIFIED) }
    var preferplatform by remember { mutableStateOf(PreferPlatform.moderate)}
    var height by remember { mutableStateOf(HeightGroup.average)}
    var bodyType by remember { mutableStateOf(BodyType.average)}

    Column(modifier = Modifier.fillMaxSize().padding(10.dp).verticalScroll(rememberScrollState()),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Enter Your Name:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = name, onValueChange = { name = it }, label = { Text("Enter Name") })

        Spacer(modifier = Modifier.height(20.dp))

        // Gender Selection
        Spacer(modifier = Modifier.height(20.dp))
        Text("GENDER:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row {
              Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = gender == "Male", onClick = { gender = "Male" })
                Text(text = "Male", fontSize = 18.sp, modifier = Modifier.padding(start = 1.dp))
            }
            Spacer(modifier = Modifier.width(50.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = gender == "Female", onClick = { gender = "Female" })
                Text(text = "Female", fontSize = 18.sp, modifier = Modifier.padding(start = 1.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Age GROUP SELECTION
        Text("AGE:", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = ageGroup == AgeGroup.BELOW_18, onClick = { ageGroup = AgeGroup.BELOW_18 })
                Text(text = AgeGroup.BELOW_18.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = ageGroup == AgeGroup.AGE_18_25, onClick = { ageGroup = AgeGroup.AGE_18_25 })
                Text(text = AgeGroup.AGE_18_25.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = ageGroup == AgeGroup.AGE_25_30, onClick = { ageGroup = AgeGroup.AGE_25_30 })
                Text(text = AgeGroup.AGE_25_30.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = ageGroup == AgeGroup.AGE_30_PLUS, onClick = { ageGroup = AgeGroup.AGE_30_PLUS })
                Text(text = AgeGroup.AGE_30_PLUS.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //Height SELECTION
        Text("HEIGHT", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = height == HeightGroup.short, onClick = { height= HeightGroup.short})
                Text(text = HeightGroup.short.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = height==HeightGroup.average, onClick = { height = HeightGroup.average})
                Text(text = HeightGroup.average.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = height == HeightGroup.tall, onClick = { height = HeightGroup.tall })
                Text(text = HeightGroup.tall.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = height== HeightGroup.very_tall, onClick = { height = HeightGroup.very_tall })
                Text(text = HeightGroup.very_tall.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Occupation Selection
        Text("WHAT DO YOU DO?", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = occupation == "College Student", onClick = { occupation = "College Student" })
                Text(text = "College Student", fontSize = 13.sp, modifier = Modifier.padding(start = 1.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = occupation == "Working Professional", onClick = { occupation = "Working Professional" })
                Text(text = "Working Professional", fontSize = 13.sp, modifier = Modifier.padding(start = 1.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Second Row
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = occupation == "School Student", onClick = { occupation= "School Student" })
                Text(text = "School Student", fontSize = 13.sp, modifier = Modifier.padding(start = 1.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = occupation == "Other", onClick = { occupation = "Other" })
                Text(text = "Other", fontSize = 13.sp, modifier = Modifier.padding(start = 1.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

//BRANDS KON KON SI
        Text("Which brands usually make up your wardrobe?:", fontSize = 16
            .sp, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = preferplatform == PreferPlatform.cheap, onClick = { preferplatform = PreferPlatform.cheap })
                Text(text = PreferPlatform.cheap.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = preferplatform == PreferPlatform.moderate, onClick = { preferplatform = PreferPlatform.moderate })
                Text(text = PreferPlatform.moderate.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = preferplatform == PreferPlatform.expensive, onClick = { preferplatform = PreferPlatform.expensive })
                    Text(text = PreferPlatform.expensive.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
                }

            }
        }

        Spacer(modifier = Modifier.height(30.dp))
//BODY TYPE
        Text("BODY TYPE:", fontSize = 16
            .sp, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = bodyType == BodyType.slim , onClick = { bodyType = BodyType.slim })
                Text(text = BodyType.slim.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 0.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = bodyType == BodyType.athletic, onClick = { bodyType = BodyType.athletic })
                Text(text = BodyType.athletic.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 0.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = bodyType== BodyType.average, onClick = { bodyType = BodyType.average })
                Text(text = BodyType.average.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 0.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = bodyType== BodyType.muscular, onClick = { bodyType = BodyType.muscular })
                Text(text = BodyType.muscular.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = bodyType== BodyType.plus_size, onClick = { bodyType = BodyType.plus_size })
                Text(text = BodyType.plus_size.displayName, fontSize = 13.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }

        Button(onClick = {
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
                    val sharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putBoolean("profile_completed", true)
                        .putString("user_gender", gender)
                        .apply()

                    Toast.makeText(context, "Profile Saved!", Toast.LENGTH_SHORT).show()
                    navController.navigate(if (gender == "Male") AuthScreen.MaleHome.route else AuthScreen.FemaleHome.route){
                        popUpTo(AuthScreen.UserProfile.route) { inclusive = true }
                    }
                }
        }) {
            Text(text =  "Save Profile", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}