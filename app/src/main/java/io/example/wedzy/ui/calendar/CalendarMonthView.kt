package io.example.wedzy.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.example.wedzy.data.model.WeddingEvent
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarMonthView(
    events: List<WeddingEvent>,
    selectedDate: Long?,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    
    Column(modifier = modifier) {
        // Month header with navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, -1)
                }
            }) {
                Text("←", style = MaterialTheme.typography.headlineMedium)
            }
            
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, 1)
                }
            }) {
                Text("→", style = MaterialTheme.typography.headlineMedium)
            }
        }
        
        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        val daysInMonth = getDaysInMonth(currentMonth)
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(daysInMonth) { dayData ->
                CalendarDayCell(
                    dayData = dayData,
                    events = events,
                    isSelected = selectedDate?.let { isSameDay(it, dayData.dateMillis) } ?: false,
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    dayData: DayData,
    events: List<WeddingEvent>,
    isSelected: Boolean,
    onDateSelected: (Long) -> Unit
) {
    val hasEvents = events.any { event ->
        isSameDay(event.startDateTime, dayData.dateMillis)
    }
    
    val isToday = isSameDay(System.currentTimeMillis(), dayData.dateMillis)
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .clickable(enabled = dayData.isCurrentMonth) {
                onDateSelected(dayData.dateMillis)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayData.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = when {
                    !dayData.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
            )
            
            if (hasEvents && dayData.isCurrentMonth) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}

private data class DayData(
    val dayOfMonth: Int,
    val dateMillis: Long,
    val isCurrentMonth: Boolean
)

private fun getDaysInMonth(calendar: Calendar): List<DayData> {
    val days = mutableListOf<DayData>()
    val cal = calendar.clone() as Calendar
    
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // Add days from previous month
    cal.add(Calendar.MONTH, -1)
    val daysInPrevMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    for (i in (daysInPrevMonth - firstDayOfWeek + 2)..daysInPrevMonth) {
        cal.set(Calendar.DAY_OF_MONTH, i)
        days.add(DayData(i, cal.timeInMillis, false))
    }
    
    // Add days of current month
    cal.add(Calendar.MONTH, 1)
    for (i in 1..daysInMonth) {
        cal.set(Calendar.DAY_OF_MONTH, i)
        days.add(DayData(i, cal.timeInMillis, true))
    }
    
    // Add days from next month to complete the grid
    cal.add(Calendar.MONTH, 1)
    val remainingDays = 42 - days.size // 6 rows * 7 days
    for (i in 1..remainingDays) {
        cal.set(Calendar.DAY_OF_MONTH, i)
        days.add(DayData(i, cal.timeInMillis, false))
    }
    
    return days
}

private fun isSameDay(millis1: Long, millis2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
    
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}
