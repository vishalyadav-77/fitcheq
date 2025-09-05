package com.vayo.fitcheq.screens.Home

import android.content.Intent
import android.graphics.Paint
import android.graphics.fonts.FontStyle
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.vayo.fitcheq.AuthScreen
import com.vayo.fitcheq.R
import com.vayo.fitcheq.data.model.UserProfile
import com.vayo.fitcheq.viewmodels.AuthViewModel
import com.vayo.fitcheq.navigation.ScreenContainer
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun MyProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    val context = LocalContext.current
    val myTitleFont = FontFamily(
        Font(R.font.title_syarifa)
    )
    val report_url = "https://tally.so/r/wAEG10"
    val faq_url = "https://tally.so/r/wboMv7"


    LaunchedEffect(Unit) {
       userProfile = authViewModel.loadUserProfileFromSharedPreferences(context)
    }

    val username = userProfile?.name ?: "..."

    ScreenContainer(navController = navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            // Title
            Text(
                text = "Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = Color.LightGray,
                thickness = 0.5.dp
            )

            // CONTENT
            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    // USER pfp and Name
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .border(width = 0.3.dp, color = Color.LightGray,shape = RoundedCornerShape(12.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(modifier= Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically){
                            Icon(
                                painter = painterResource(R.drawable.user_icon),
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape)
                                    .background(color = colorResource(id = R.color.sage))
                                    .padding(20.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.weight(0.2f))
                            Column( modifier = Modifier.align(Alignment.CenterVertically)){
                                Text(
                                    text = username,
                                    fontSize = 24.sp,
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                Text(
                                    text = "+91 9841XXXXXX",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // DETAILS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DETAILS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "EDIT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    // Details content
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 380.dp)
                            .border(width = 0.3.dp, color = Color.LightGray,shape = RoundedCornerShape(12.dp)),
                    ) {
                        Column(modifier = Modifier.wrapContentHeight().padding(8.dp)) {
                            userProfile?.let { profile ->
                                ProfileRow(label = "Gender", value = profile.gender)
                                ProfileRow(label = "Height", value = profile.height.displayName)
                                ProfileRow(label = "Age", value = profile.ageGroup.displayName)
                                ProfileRow(label = "Status", value = profile.occupation)
                                ProfileRow(label = "Body Type", value = profile.bodyType.displayName)
                                ProfileRow(
                                    label = "Favourite Platforms",
                                    value = profile.preferPlatform.displayName
                                )
                            } ?: run {
                                Text("Loading...")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    // CONTACT
                    Text( modifier = Modifier.fillMaxWidth(),
                        text = "CONTACT",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .border(width = 0.3.dp, color = Color.LightGray,shape = RoundedCornerShape(12.dp))
                            .padding(16.dp),
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(modifier = Modifier.size(18.dp), painter = painterResource(R.drawable.ps_info), contentDescription = "Info")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("About us", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                            Icon(imageVector = Icons.Outlined.KeyboardArrowRight, contentDescription = "Arrow")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()
                               .clickable {
                                   val intent = Intent(Intent.ACTION_VIEW, Uri.parse(report_url))
                                   context.startActivity(intent)
                        }
                            , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(modifier = Modifier.size(18.dp), painter = painterResource(R.drawable.alert_triangle), contentDescription = "Warning")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Report an issue", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                            Icon(imageVector = Icons.Outlined.KeyboardArrowRight, contentDescription = "Arrow")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(faq_url))
                                context.startActivity(intent)
                            }
                            , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(modifier = Modifier.size(16.dp), painter = painterResource(R.drawable.chat_faq), contentDescription = "faq")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(" FAQs", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            }
                            Icon(imageVector = Icons.Outlined.KeyboardArrowRight, contentDescription = "Arrow")
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    // Logout Button
                    Button(
                        onClick = {
                            authViewModel.logout()
                        },
                        modifier = Modifier.fillMaxWidth().border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(12.dp)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "LOG OUT",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Fit Cheq",
                        fontFamily = myTitleFont,
                        fontSize = 24.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1.0.0",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Thin,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

        }
    }
}
@Composable
fun ProfileRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp,horizontal = 12.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(text = value, fontSize = 14.sp, color = Color.DarkGray,fontWeight = FontWeight.Medium)
    }
}

