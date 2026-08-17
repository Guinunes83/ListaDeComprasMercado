package com.example.data

import androidx.room.*

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val unit: String = "un",
    val lastPrice: Double = 0.0,
    val totalQuantityBought: Int = 0,
    val timesBought: Int = 0,
    val firstPurchaseTimestamp: Long = 0L
) {
    val averageQuantity: Int
        get() = if (timesBought > 0) totalQuantityBought / timesBought else 1

    val monthlyAverageQuantity: Int
        get() {
            if (totalQuantityBought == 0) return 0
            if (firstPurchaseTimestamp == 0L) return totalQuantityBought
            val now = System.currentTimeMillis()
            val msInMonth = 1000L * 60 * 60 * 24 * 30
            val months = ((now - firstPurchaseTimestamp) / msInMonth).toInt()
            val effectiveMonths = if (months < 1) 1 else months
            return totalQuantityBought / effectiveMonths
        }
}

@Entity(
    tableName = "shopping_items",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId")]
)
data class ShoppingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val quantity: Int = 1,
    val price: Double = 0.0,
    val isBought: Boolean = false
)

data class ShoppingItemWithProduct(
    @Embedded val item: ShoppingItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product
)
