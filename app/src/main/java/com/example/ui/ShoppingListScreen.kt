package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.data.ShoppingItem
import com.example.data.ShoppingItemWithProduct
import com.example.util.PdfUtils

@Composable
fun ShoppingListScreen(
    shoppingList: List<ShoppingItemWithProduct>,
    products: List<Product>,
    onToggleBought: (ShoppingItem) -> Unit,
    onUpdateQuantity: (ShoppingItem, Int) -> Unit,
    onUpdatePrice: (ShoppingItem, Double) -> Unit,
    onRemoveItem: (ShoppingItem) -> Unit,
    onFinishPurchase: () -> Unit,
    onAddItems: (List<Product>) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val totalCompra = shoppingList.sumOf { 
        if (it.product.lastPrice > 0) (it.product.lastPrice * it.item.quantity) else 0.0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bento Grid Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp),
                shape = androidx.compose.ui.graphics.RectangleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Adicionar",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text("Adicionar Item", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
            }

            Surface(
                onClick = { /* Placeholder for QR scanner */ },
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp),
                shape = androidx.compose.ui.graphics.RectangleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.QrCodeScanner,
                        contentDescription = "Escanear",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "ESCANEAR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Surface(
                onClick = { PdfUtils.generateAndSharePdf(context, shoppingList) },
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp),
                shape = androidx.compose.ui.graphics.RectangleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.PictureAsPdf,
                        contentDescription = "Gerar PDF",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "GERAR PDF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Main List Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = androidx.compose.ui.graphics.RectangleShape,
            color = Color.White,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // List Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(com.example.ui.theme.HeaderBackgroundColor)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Itens na Lista (${shoppingList.size})",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        Icons.Default.Sort,
                        contentDescription = "Sort",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Divider(color = MaterialTheme.colorScheme.outline)

                // List Items
                if (shoppingList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Sua lista está vazia.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(0.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        items(shoppingList) { itemWithProduct ->
                            ShoppingItemCard(
                                itemWithProduct = itemWithProduct,
                                onToggleBought = onToggleBought,
                                onUpdateQuantity = onUpdateQuantity,
                                onUpdatePrice = onUpdatePrice,
                                onRemoveItem = onRemoveItem
                            )
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outline)

                // Bottom Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(com.example.ui.theme.HeaderBackgroundColor)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TOTAL DA COMPRA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = String.format("R$ %.2f", totalCompra),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(
                        onClick = onFinishPurchase,
                        enabled = shoppingList.any { it.item.isBought },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text("Finalizar", fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductToListDialog(
            products = products,
            shoppingList = shoppingList,
            onDismiss = { showAddDialog = false },
            onProductsSelected = { 
                onAddItems(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ShoppingItemCard(
    itemWithProduct: ShoppingItemWithProduct,
    onToggleBought: (ShoppingItem) -> Unit,
    onUpdateQuantity: (ShoppingItem, Int) -> Unit,
    onUpdatePrice: (ShoppingItem, Double) -> Unit,
    onRemoveItem: (ShoppingItem) -> Unit
) {
    val item = itemWithProduct.item
    val product = itemWithProduct.product
    val isBought = item.isBought

    var expanded by remember { mutableStateOf(false) }

    val containerColor = if (isBought) MaterialTheme.colorScheme.background else Color.White
    val borderColor = if (isBought) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outlineVariant

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = androidx.compose.ui.graphics.RectangleShape,
        color = containerColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Custom Checkbox
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(androidx.compose.ui.graphics.RectangleShape)
                        .background(if (isBought) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .border(
                            2.dp,
                            if (isBought) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            androidx.compose.ui.graphics.RectangleShape
                        )
                        .clickable { onToggleBought(item) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isBought) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "${product.name} (${product.unit})",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                
                Text(
                    text = "${item.quantity}x",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (product.lastPrice > 0) {
                    Text(
                        text = String.format("R$ %.2f", product.lastPrice),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Text(
                        text = "-",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                // Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Price Input
                    var priceText by remember(item.price) { mutableStateOf(if (item.price > 0) item.price.toString() else "") }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .background(Color.White, androidx.compose.ui.graphics.RectangleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.ui.graphics.RectangleShape)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("R$", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(4.dp))
                        BasicTextField(
                            value = priceText,
                            onValueChange = { 
                                priceText = it
                                val newPrice = it.toDoubleOrNull()
                                if (newPrice != null) {
                                    onUpdatePrice(item, newPrice)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (priceText.isEmpty()) {
                                    Text(
                                        text = "0.00",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    // Quantity Controls
                    Row(
                        modifier = Modifier
                            .height(36.dp)
                            .background(com.example.ui.theme.InputBackgroundColor, androidx.compose.ui.graphics.RectangleShape),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "–",
                            modifier = Modifier
                                .clickable { onUpdateQuantity(item, item.quantity - 1) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.quantity.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "+",
                            modifier = Modifier
                                .clickable { onUpdateQuantity(item, item.quantity + 1) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(Modifier.width(4.dp))
                    
                    IconButton(onClick = { onRemoveItem(item) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductToListDialog(
    products: List<Product>,
    shoppingList: List<ShoppingItemWithProduct>,
    onDismiss: () -> Unit,
    onProductsSelected: (List<Product>) -> Unit
) {
    val inListIds = shoppingList.map { it.product.id }.toSet()
    val availableProducts = products.filter { it.id !in inListIds }
    val selectedProducts = remember { mutableStateListOf<Product>() }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text("Adicionar à Lista", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            if (availableProducts.isEmpty()) {
                Text("Nenhum produto disponível ou todos já estão na lista.")
                Spacer(modifier = Modifier.height(32.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(availableProducts) { product ->
                        val isSelected = selectedProducts.contains(product)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isSelected) {
                                        selectedProducts.remove(product)
                                    } else {
                                        selectedProducts.add(product)
                                    }
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedProducts.add(product)
                                    } else {
                                        selectedProducts.remove(product)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = "${product.name} (${product.unit})", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    text = "Média mensal: ${product.monthlyAverageQuantity} | Último: R$ ${product.lastPrice}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onProductsSelected(selectedProducts.toList()) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = selectedProducts.isNotEmpty()
                ) {
                    Text("Adicionar Selecionados (${selectedProducts.size})")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
