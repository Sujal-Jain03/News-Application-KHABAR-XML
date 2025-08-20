package com.example.khabar.data.model

data class User(
    val uid: String ="",
    val email: String ="",
    val fullName: String ="",
    val phoneNumber: String ="",
    var profilePictureUrl: String? = null,
    val createdAt: Long = 0L,
)