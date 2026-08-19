package com.example.data

import kotlinx.coroutines.flow.Flow

class ShoppingRepository(private val dao: ShoppingDao) {
    val allProducts: Flow<List<Product>> = dao.getAllProducts()
    val shoppingList: Flow<List<ShoppingItemWithProduct>> = dao.getShoppingList()

    suspend fun addProduct(name: String, unit: String, price: Double, category: String) {
        dao.insertProduct(Product(name = name, unit = unit, lastPrice = price, category = category))
    }

    suspend fun addShoppingItem(product: Product) {
        val q = if (product.monthlyAverageQuantity > 0) product.monthlyAverageQuantity else 1
        val newItem = ShoppingItem(
            productId = product.id,
            quantity = q,
            price = product.lastPrice
        )
        dao.insertShoppingItem(newItem)
    }

    suspend fun updateShoppingItem(item: ShoppingItem) {
        dao.updateShoppingItem(item)
    }
    
    suspend fun deleteShoppingItem(item: ShoppingItem) {
        dao.deleteShoppingItem(item)
    }

    suspend fun finishPurchase(items: List<ShoppingItemWithProduct>) {
        val boughtItems = items.filter { it.item.isBought }
        for (bought in boughtItems) {
            val product = bought.product
            val newQuantity = product.totalQuantityBought + bought.item.quantity
            val newTimesBought = product.timesBought + 1
            val newFirstPurchase = if (product.firstPurchaseTimestamp == 0L) System.currentTimeMillis() else product.firstPurchaseTimestamp
            
            // Update with user-entered price, or keep last if they left it at 0
            val updatedPrice = if (bought.item.price > 0.0) bought.item.price else product.lastPrice
            
            val updatedProduct = product.copy(
                lastPrice = updatedPrice,
                totalQuantityBought = newQuantity,
                timesBought = newTimesBought,
                firstPurchaseTimestamp = newFirstPurchase
            )
            dao.updateProduct(updatedProduct)
        }
        dao.clearBoughtItems()
    }
}
