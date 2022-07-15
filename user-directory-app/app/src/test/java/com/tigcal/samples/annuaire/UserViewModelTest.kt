package com.tigcal.samples.annuaire

import app.cash.turbine.test
import com.tigcal.samples.annuaire.model.User
import com.tigcal.samples.annuaire.model.UserResponse
import com.tigcal.samples.annuaire.model.Response
import com.tigcal.samples.annuaire.network.UserService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class UserViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getUsers() {
        val expectedUsers =
            listOf(
                User(firstName = "Harry", lastName = "Potter"),
                User(firstName = "Hermione", lastName = "Granger")
            )
        val successfulResponse = Response.Success(expectedUsers)
        val userService: UserService = mock {
            onBlocking { getUsers() } doReturn UserResponse(expectedUsers)
        }
        val testDispatcher = StandardTestDispatcher()
        val viewModel = UserViewModel(userService, testDispatcher)

        runTest {
            viewModel.getUsers()
            testDispatcher.scheduler.advanceUntilIdle()
            viewModel.response.test {
                assertEquals(successfulResponse, awaitItem())
            }
        }
    }
}