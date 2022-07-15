package com.tigcal.samples.annuaire.network

import com.tigcal.samples.annuaire.model.UserResponse
import retrofit2.http.GET

interface UserService {

    @GET("users")
    suspend fun getUsers(): UserResponse
}