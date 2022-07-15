package com.tigcal.samples.annuaire.model

sealed class Response {
    data class Success(val result: Any) : Response()
    data class Failure(val error: Exception) : Response()
    object Loading : Response()
}