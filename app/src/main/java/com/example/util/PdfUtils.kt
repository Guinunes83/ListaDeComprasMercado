package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.ShoppingItemWithProduct
import java.io.File
import java.io.FileOutputStream

object PdfUtils {
    fun generateAndSharePdf(context: Context, shoppingList: List<ShoppingItemWithProduct>) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 approx
        var page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = page.canvas
        val paint = Paint()

        paint.color = Color.BLACK
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Lista de Compras Mercado", 40f, 50f, paint)

        // Draw Table Header
        paint.textSize = 12f
        var y = 100f
        
        fun drawRow(check: String, desc: String, unit: String, qty: String, lastPrice: String, currentPrice: String, yPos: Float, isHeader: Boolean) {
            val originalTypeface = paint.typeface
            if (isHeader) {
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            } else {
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            
            canvas.drawText(check, 40f, yPos, paint)
            canvas.drawText(desc, 90f, yPos, paint)
            canvas.drawText(unit, 290f, yPos, paint)
            canvas.drawText(qty, 340f, yPos, paint)
            canvas.drawText(lastPrice, 390f, yPos, paint)
            canvas.drawText(currentPrice, 480f, yPos, paint)
            
            // Draw a subtle line under the row
            paint.strokeWidth = 0.5f
            paint.color = Color.LTGRAY
            canvas.drawLine(40f, yPos + 8f, 555f, yPos + 8f, paint)
            paint.color = Color.BLACK // reset
            
            paint.typeface = originalTypeface
        }

        drawRow("Check", "Descrição", "Unid.", "Qtd", "Último Valor", "Valor Atual", y, true)
        y += 25f

        var total = 0.0

        for (itemWithProduct in shoppingList) {
            val product = itemWithProduct.product
            val item = itemWithProduct.item
            
            // Add to total using ONLY lastPrice as requested
            if (product.lastPrice > 0) {
                total += (product.lastPrice * item.quantity)
            }

            val checkbox = "[   ]"
            val desc = if (product.name.length > 28) product.name.substring(0, 25) + "..." else product.name
            val lastPriceStr = if (product.lastPrice > 0) String.format("R$ %.2f", product.lastPrice) else "-"
            
            drawRow(
                check = checkbox, 
                desc = desc, 
                unit = product.unit, 
                qty = item.quantity.toString(), 
                lastPrice = lastPriceStr, 
                currentPrice = "R$ _________", 
                yPos = y, 
                isHeader = false
            )
            
            y += 25f
            
            if (y > 780f) {
                pdfDocument.finishPage(page)
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 50f
                drawRow("Check", "Descrição", "Unid.", "Qtd", "Último Valor", "Valor Atual", y, true)
                y += 25f
            }
        }
        
        y += 30f
        if (y > 780f) {
            pdfDocument.finishPage(page)
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            y = 50f
        }
        
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 14f
        canvas.drawText("Total da Compra (Estimativa Histórica): R$ ${String.format("%.2f", total)}", 40f, y, paint)
        
        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "lista_compras.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        pdfDocument.close()

        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar Lista em PDF"))
    }
}
