package io.example.wedzy.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import io.example.wedzy.data.model.BudgetItem
import io.example.wedzy.data.model.Currency
import io.example.wedzy.data.model.Guest
import io.example.wedzy.data.model.Task
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {
    
    private const val PAGE_WIDTH = 595 // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 40
    
    fun exportBudgetToPdf(
        context: Context,
        items: List<BudgetItem>,
        totalBudget: Double,
        totalEstimated: Double,
        totalActual: Double,
        currency: Currency
    ): File? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }
            
            val headerPaint = Paint().apply {
                textSize = 16f
                isFakeBoldText = true
            }
            
            val textPaint = Paint().apply {
                textSize = 12f
            }
            
            var yPosition = MARGIN + 30f
            
            // Title
            canvas.drawText("Wedding Budget Report", MARGIN.toFloat(), yPosition, titlePaint)
            yPosition += 40
            
            // Summary
            canvas.drawText("Budget Summary", MARGIN.toFloat(), yPosition, headerPaint)
            yPosition += 25
            canvas.drawText("Total Budget: ${currency.symbol}${String.format("%.2f", totalBudget)}", MARGIN.toFloat(), yPosition, textPaint)
            yPosition += 20
            canvas.drawText("Total Estimated: ${currency.symbol}${String.format("%.2f", totalEstimated)}", MARGIN.toFloat(), yPosition, textPaint)
            yPosition += 20
            canvas.drawText("Total Actual: ${currency.symbol}${String.format("%.2f", totalActual)}", MARGIN.toFloat(), yPosition, textPaint)
            yPosition += 20
            canvas.drawText("Remaining: ${currency.symbol}${String.format("%.2f", totalBudget - totalActual)}", MARGIN.toFloat(), yPosition, textPaint)
            yPosition += 40
            
            // Items
            canvas.drawText("Budget Items", MARGIN.toFloat(), yPosition, headerPaint)
            yPosition += 25
            
            items.forEach { item ->
                if (yPosition > PAGE_HEIGHT - MARGIN) {
                    document.finishPage(page)
                    val newPage = document.startPage(pageInfo)
                    yPosition = MARGIN + 30f
                }
                
                canvas.drawText("${item.name} (${item.category})", MARGIN.toFloat(), yPosition, textPaint)
                yPosition += 18
                canvas.drawText("  Estimated: ${currency.symbol}${String.format("%.2f", item.estimatedCost)}", MARGIN.toFloat() + 20, yPosition, textPaint)
                yPosition += 18
                canvas.drawText("  Actual: ${currency.symbol}${String.format("%.2f", item.actualCost)}", MARGIN.toFloat() + 20, yPosition, textPaint)
                yPosition += 25
            }
            
            document.finishPage(page)
            
            val fileName = "Wedzy_Budget_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            
            FileOutputStream(file).use { outputStream ->
                document.writeTo(outputStream)
            }
            
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun exportGuestListToPdf(
        context: Context,
        guests: List<Guest>
    ): File? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }
            
            val headerPaint = Paint().apply {
                textSize = 16f
                isFakeBoldText = true
            }
            
            val textPaint = Paint().apply {
                textSize = 12f
            }
            
            var yPosition = MARGIN + 30f
            
            canvas.drawText("Wedding Guest List", MARGIN.toFloat(), yPosition, titlePaint)
            yPosition += 40
            
            canvas.drawText("Total Guests: ${guests.size}", MARGIN.toFloat(), yPosition, headerPaint)
            yPosition += 30
            
            guests.forEach { guest ->
                if (yPosition > PAGE_HEIGHT - MARGIN) {
                    document.finishPage(page)
                    val newPage = document.startPage(pageInfo)
                    yPosition = MARGIN + 30f
                }
                
                canvas.drawText("${guest.firstName} ${guest.lastName}", MARGIN.toFloat(), yPosition, textPaint)
                yPosition += 18
                canvas.drawText("  RSVP: ${guest.rsvpStatus} | Side: ${guest.side}", MARGIN.toFloat() + 20, yPosition, textPaint)
                yPosition += 18
                if (guest.email.isNotEmpty()) {
                    canvas.drawText("  Email: ${guest.email}", MARGIN.toFloat() + 20, yPosition, textPaint)
                    yPosition += 18
                }
                yPosition += 10
            }
            
            document.finishPage(page)
            
            val fileName = "Wedzy_GuestList_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            
            FileOutputStream(file).use { outputStream ->
                document.writeTo(outputStream)
            }
            
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun exportTasksToPdf(
        context: Context,
        tasks: List<Task>
    ): File? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }
            
            val headerPaint = Paint().apply {
                textSize = 16f
                isFakeBoldText = true
            }
            
            val textPaint = Paint().apply {
                textSize = 12f
            }
            
            var yPosition = MARGIN + 30f
            
            canvas.drawText("Wedding Task Checklist", MARGIN.toFloat(), yPosition, titlePaint)
            yPosition += 40
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            
            tasks.sortedBy { it.dueDate }.forEach { task ->
                if (yPosition > PAGE_HEIGHT - MARGIN) {
                    document.finishPage(page)
                    val newPage = document.startPage(pageInfo)
                    yPosition = MARGIN + 30f
                }
                
                val checkbox = if (task.status.name == "COMPLETED") "☑" else "☐"
                canvas.drawText("$checkbox ${task.title}", MARGIN.toFloat(), yPosition, textPaint)
                yPosition += 18
                
                val dueDate = task.dueDate?.let { dateFormat.format(Date(it)) } ?: "No due date"
                canvas.drawText("  Due: $dueDate | Priority: ${task.priority}", MARGIN.toFloat() + 20, yPosition, textPaint)
                yPosition += 18
                
                if (task.description.isNotEmpty()) {
                    canvas.drawText("  ${task.description}", MARGIN.toFloat() + 20, yPosition, textPaint)
                    yPosition += 18
                }
                yPosition += 10
            }
            
            document.finishPage(page)
            
            val fileName = "Wedzy_Tasks_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            
            FileOutputStream(file).use { outputStream ->
                document.writeTo(outputStream)
            }
            
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
