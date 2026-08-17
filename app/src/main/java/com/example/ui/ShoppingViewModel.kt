package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Product
import com.example.data.ShoppingItem
import com.example.data.ShoppingItemWithProduct
import com.example.data.ShoppingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingViewModel(private val repository: ShoppingRepository) : ViewModel() {

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val shoppingList: StateFlow<List<ShoppingItemWithProduct>> = repository.shoppingList
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addProduct(name: String, unit: String, price: Double) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                repository.addProduct(name, unit, price)
            }
        }
    }

    fun addShoppingItem(product: Product) {
        viewModelScope.launch {
            val currentList = shoppingList.value
            if (currentList.none { it.product.id == product.id }) {
                repository.addShoppingItem(product)
            }
        }
    }

    fun addShoppingItems(products: List<Product>) {
        viewModelScope.launch {
            val currentList = shoppingList.value
            for (product in products) {
                if (currentList.none { it.product.id == product.id }) {
                    repository.addShoppingItem(product)
                }
            }
        }
    }

    fun updateShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.updateShoppingItem(item)
        }
    }
    
    fun removeShoppingItem(item: ShoppingItem) {
        viewModelScope.launch {
            repository.deleteShoppingItem(item)
        }
    }

    fun toggleItemBought(item: ShoppingItem) {
        updateShoppingItem(item.copy(isBought = !item.isBought))
    }

    fun updateItemQuantity(item: ShoppingItem, quantity: Int) {
        if (quantity > 0) {
            updateShoppingItem(item.copy(quantity = quantity))
        }
    }

    fun updateItemPrice(item: ShoppingItem, price: Double) {
        updateShoppingItem(item.copy(price = price))
    }

    fun finishPurchase() {
        viewModelScope.launch {
            repository.finishPurchase(shoppingList.value)
        }
    }
}
