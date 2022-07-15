package com.tigcal.samples.annuaire.model

import com.squareup.moshi.Json

data class User(
    val id: String? = "",
    @Json(name = "firstName")
    val firstName: String? = "(Not Available)",
    @Json(name = "lastName")
    val lastName: String? = "(Not Available)",
    @Json(name = "image")
    val image: String? = "",
    @Json(name = "phone")
    val phone: String? = "(Not available)",
) {
    val fullName = "$firstName $lastName"
}

data class UserResponse(val users: List<User>)
