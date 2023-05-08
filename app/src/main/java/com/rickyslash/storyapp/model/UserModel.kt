package com.rickyslash.storyapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel (
    var name: String? = null,
    var email: String? = null,
    var isLogin: Boolean = false,
    var token: String? = null
): Parcelable