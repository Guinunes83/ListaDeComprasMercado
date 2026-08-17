package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.data.AppDatabase
import com.example.data.ShoppingRepository
import com.example.ui.EloraApp
import com.example.ui.ShoppingViewModel
import com.example.ui.ShoppingViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize DB, Repository, and ViewModel manually for simplicity
        val database = AppDatabase.getDatabase(this)
        val repository = ShoppingRepository(database.shoppingDao())
        val factory = ShoppingViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[ShoppingViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EloraApp(viewModel = viewModel)
            }
        }
    }
}
