package com.vayo.fitcheq.data.model

data class InstaUsersData(
    val id: String,
    val link: String,
    val username: String,
    val name: String,
    val profilePic: String
)

val instagramUsers = listOf(
    InstaUsersData("1", link = "https://www.instagram.com/lukeliiu/" , username = "lukeliiu", name =  "Luke Liu", profilePic =  "https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/lukeliu.jpg"),
    InstaUsersData("2", link = "https://www.instagram.com/kadenhammond/", username = "kadenhammond", name = "Kaden Hammond", profilePic = "https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/kadenhammond.jpg"),
    InstaUsersData("3","https://www.instagram.com/andrikalvarezz/","andrikalvarezz","Andrik Alvarez Linazasoro","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/andrik.jpg"),
    InstaUsersData("4","https://www.instagram.com/aituar.kaddy/","aituar.kaddy","Aituar","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/aituar.jpg"),
    InstaUsersData("5","https://www.instagram.com/uhmatty/","uhmatty","Uhmatty","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/uhmatty.jpg"),
    InstaUsersData("6","https://www.instagram.com/ethan_kieffer/","ethan_kieffer","Ethan Kieffer","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/ethan.jpg"),
    InstaUsersData("7","https://www.instagram.com/itsrovski/","itsrovski","Kristijan Todorovski","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/kristijan.jpg"),
    InstaUsersData("8","https://www.instagram.com/kyletheyuan/","kyletheyuan","Kyle","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/kyle.jpg")
)

val indianInstaUsers = listOf(
    InstaUsersData("1", link = "https://www.instagram.com/aayushbhatiaa/", username = "aayushbhatiaa", name = "Aayush Bhatiaa", profilePic = "https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/indian/aayush.webp"),
    InstaUsersData("2","https://www.instagram.com/mencrucials/","mencrucials","Shrawan Chhetri","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/indian/shrawan.jpg"),
    InstaUsersData("3","https://www.instagram.com/chiragkhannaa/","chiragkhannaa","Chirag Khanna","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/indian/chirag.webp"),
    InstaUsersData("4","https://www.instagram.com/sankalra/","sankalra","San Kalra","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/indian/san.webp"),
    InstaUsersData("5","https://www.instagram.com/sushilbhagtanii/","sushilbhagtanii","Sushil Bhagtani","https://raw.githubusercontent.com/vishalyadav-77/fitcheq-assests/refs/heads/main/InfluencersData/indian/sushil.webp")
)
