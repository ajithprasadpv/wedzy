package io.example.wedzy.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Premium rounded shapes for a softer, more elegant look
val WedzyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Custom shape tokens for specific components
val ButtonShape = RoundedCornerShape(12.dp)
val CardShape = RoundedCornerShape(20.dp)
val ChipShape = RoundedCornerShape(50)
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val DialogShape = RoundedCornerShape(28.dp)
val ImageShape = RoundedCornerShape(16.dp)
val AvatarShape = RoundedCornerShape(50)
