package com.tigcal.samples.annuaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tigcal.samples.annuaire.model.Response
import com.tigcal.samples.annuaire.network.UserService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val service: UserService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {
    private var _response: MutableStateFlow<Response> = MutableStateFlow(Response.Loading)
    val response: StateFlow<Response> = _response

    fun getUsers() {
        _response.value = Response.Loading
        viewModelScope.launch(dispatcher) {
            delay(1_000)
            try {
                val users = service.getUsers().users.sortedBy { it.fullName }
                _response.value =
                    if (users.isNotEmpty()) Response.Success(users)
                    else Response.Failure(Exception("There are no users to display"))
            } catch (exception: Exception) {
                _response.value = Response.Failure(exception)
            }
        }
    }
}