package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EloraApp(viewModel: ShoppingViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }

    val products by viewModel.products.collectAsState()
    val shoppingList by viewModel.shoppingList.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Lista de Compras",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Sincronizado com histórico",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                tonalElevation = 0.dp,
                modifier = Modifier.height(80.dp)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.FormatListBulleted, contentDescription = "Lista") },
                    label = { Text("Lista") },
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.History, contentDescription = "Produtos") },
                    label = { Text("Produtos") },
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.QueryStats, contentDescription = "Insights") },
                    label = { Text("Insights") },
                    selected = false,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> ShoppingListScreen(
                    shoppingList = shoppingList,
                    products = products,
                    onToggleBought = viewModel::toggleItemBought,
                    onUpdateQuantity = viewModel::updateItemQuantity,
                    onUpdatePrice = viewModel::updateItemPrice,
                    onRemoveItem = viewModel::removeShoppingItem,
                    onFinishPurchase = viewModel::finishPurchase,
                    onAddItems = viewModel::addShoppingItems
                )
                1 -> ProductsScreen(
                    products = products,
                    onAddProduct = viewModel::addProduct
                )
            }
        }
    }
}
