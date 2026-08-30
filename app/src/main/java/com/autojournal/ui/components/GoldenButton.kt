package com.autojournal.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.autojournal.ui.theme.ThemeManager

@Composable
fun GoldenButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = ThemeManager.accentColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = ThemeManager.accentColor.copy(alpha = 0.15f),
            contentColor = ThemeManager.accentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = ThemeManager.accentColor.copy(alpha = 0.3f)
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun GoldenOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ThemeManager.accentColor,
            disabledContentColor = ThemeManager.accentColor.copy(alpha = 0.3f)
        )
    ) {
        Text(text = text)
    }
}

@Composable
fun GoldenOutlinedButtonWithBorder(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = 1.dp,
                color = ThemeManager.accentColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = ThemeManager.accentColor,
            disabledContentColor = ThemeManager.accentColor.copy(alpha = 0.3f)
        )
    ) {
        Text(text = text)
    }
}