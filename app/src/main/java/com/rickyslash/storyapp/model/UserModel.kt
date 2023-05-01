package com.rickyslash.storyapp.model

data class UserModel (
    val name: String,
    val email: String,
    val isLogin: Boolean,
    val token: String
)