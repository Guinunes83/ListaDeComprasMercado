package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.Product

@Composable
fun ProductsScreen(
    products: List<Product>,
    onAddProduct: (String, String, Double) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum produto cadastrado.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductCard(product)
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Novo Produto")
        }
    }

    if (showAddDialog) {
        var newProductName by remember { mutableStateOf("") }
        var newProductUnit by remember { mutableStateOf("un") }
        var newProductPrice by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Novo Produto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newProductName,
                        onValueChange = { newProductName = it },
                        label = { Text("Nome do Produto") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Simple text fields for unit and price for now
                    OutlinedTextField(
                        value = newProductUnit,
                        onValueChange = { newProductUnit = it },
                        label = { Text("Unidade (pct, gf, kg, lt, cx, un)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newProductPrice,
                        onValueChange = { newProductPrice = it },
                        label = { Text("Valor (Opcional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newProductName.isNotBlank()) {
                            val price = newProductPrice.replace(",", ".").toDoubleOrNull() ?: 0.0
                            onAddProduct(newProductName, newProductUnit, price)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ProductCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${product.name} (${product.unit})", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Comprado ${product.timesBought} vezes | Média mensal: ${product.monthlyAverageQuantity}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Último preço: R$ ${product.lastPrice}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
