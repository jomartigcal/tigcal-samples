package com.tigcal.samples.annuaire

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tigcal.samples.annuaire.model.User
import com.tigcal.samples.annuaire.model.Response
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val userViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel((application as UserDirectoryApp).userService) as T
        }
    }

    private val viewModel: UserViewModel by viewModels { userViewModelFactory }

    private val userAdapter = UserAdapter()

    private val swipeRefreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.swipe_refresh_layout)
    }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.recycler_view) }
    private val progressBar: ProgressBar by lazy { findViewById(R.id.progress_bar) }
    private val emptyText: TextView by lazy { findViewById(R.id.empty_text) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            viewModel.getUsers()
        }

        recyclerView.adapter = userAdapter

        viewModel.getUsers()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.response.collect { response ->
                    updateScreen(response)
                }
            }
        }
    }

    private fun updateScreen(response: Response) {
        swipeRefreshLayout.isRefreshing = false
        progressBar.isVisible = false
        emptyText.isVisible = false
        recyclerView.isVisible = false

        when (response) {
            is Response.Success -> {
                recyclerView.isVisible = true
                userAdapter.setUsers(response.result as? List<User> ?: emptyList())
            }
            is Response.Loading -> {
                progressBar.isVisible = true
            }
            is Response.Failure -> {
                emptyText.isVisible = true
                Snackbar.make(emptyText, "Errror: ${response.error.message}", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }
}