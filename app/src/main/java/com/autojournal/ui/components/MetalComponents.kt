package com.autojournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autojournal.ui.theme.MetalChromeTheme

// ===== МЕТАЛЛИЧЕСКИЙ ФОН =====
@Composable
fun MetalBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MetalChromeTheme.AnodizedBlack,
                        MetalChromeTheme.FactoryDark,
                        MetalChromeTheme.MetalGrey,
                        MetalChromeTheme.FactoryDark
                    )
                )
            )
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.02f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.02f),
                        Color.Transparent,
                        Color.White.copy(alpha = 0.01f),
                        Color.Transparent
                    ),
                    startX = 0.0f,
                    endX = 1.0f
                )
            )
    ) {
        content()
    }
}

// ===== МЕТАЛЛИЧЕСКАЯ КАРТОЧКА =====
@Composable
fun MetalCard(
    modifier: Modifier = Modifier,
    elevation: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MetalChromeTheme.MetalGrey
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MetalChromeTheme.Steel.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MetalChromeTheme.MetalGrey,
                            MetalChromeTheme.WorkshopFloor
                        )
                    )
                )
                .padding(16.dp)
        ) {
            content()
        }
    }
}

// ===== ХРОМИРОВАННАЯ КНОПКА =====
@Composable
fun ChromeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = MetalChromeTheme.Chrome.copy(alpha = if (enabled) 0.5f else 0.2f),
                shape = RoundedCornerShape(6.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) MetalChromeTheme.DarkSteel else MetalChromeTheme.MetalGrey,
            contentColor = MetalChromeTheme.MetalText,
            disabledContainerColor = MetalChromeTheme.WorkshopFloor,
            disabledContentColor = MetalChromeTheme.DarkMetalText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = if (enabled) 0.3f else 0.05f),
                                Color.Transparent
                            )
                        )
                    )
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                content()
            }
        }
    }
}

// ===== МЕТАЛЛИЧЕСКОЕ ПОЛЕ ВВОДА =====
@Composable
fun MetalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        singleLine = singleLine,
        label = label?.let { { Text(it, color = MetalChromeTheme.DarkMetalText) } },
        placeholder = placeholder?.let { { Text(it, color = MetalChromeTheme.DarkMetalText.copy(alpha = 0.5f)) } },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MetalChromeTheme.Chrome,
            unfocusedBorderColor = MetalChromeTheme.DarkMetalText.copy(alpha = 0.3f),
            focusedLabelColor = MetalChromeTheme.Chrome,
            unfocusedLabelColor = MetalChromeTheme.DarkMetalText,
            cursorColor = MetalChromeTheme.Chrome,
            focusedTextColor = MetalChromeTheme.MetalText,
            unfocusedTextColor = MetalChromeTheme.MetalText,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorBorderColor = MetalChromeTheme.StatusRed
        ),
        textStyle = TextStyle(
            color = MetalChromeTheme.MetalText,
            fontSize = 14.sp
        )
    )
}

// ===== ХРОМИРОВАННАЯ МАЛЕНЬКАЯ КНОПКА =====
@Composable
fun ChromeSmallButton(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = MetalChromeTheme.Chrome.copy(alpha = if (enabled) 0.4f else 0.15f),
                shape = RoundedCornerShape(4.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) MetalChromeTheme.DarkSteel else MetalChromeTheme.MetalGrey,
            contentColor = MetalChromeTheme.MetalText,
            disabledContainerColor = MetalChromeTheme.WorkshopFloor,
            disabledContentColor = MetalChromeTheme.DarkMetalText
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp,
            maxLines = 1
        )
    }
}

// ===== ИНДИКАТОР-БОЛТ =====
@Composable
fun BoltIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = if (isActive) MetalChromeTheme.Chrome else MetalChromeTheme.DarkSteel,
                shape = CircleShape
            )
            .border(
                width = 2.dp,
                color = if (isActive) MetalChromeTheme.Steel else MetalChromeTheme.FactoryDark,
                shape = CircleShape
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(2.dp)
                .align(Alignment.Center)
                .background(
                    color = if (isActive) MetalChromeTheme.Titanium else MetalChromeTheme.AnodizedBlack
                )
        )
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight(0.6f)
                .align(Alignment.Center)
                .background(
                    color = if (isActive) MetalChromeTheme.Titanium else MetalChromeTheme.AnodizedBlack
                )
        )
    }
}

// ===== МЕТАЛЛИЧЕСКИЙ ЧИП =====
@Composable
fun MetalChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MetalChromeTheme.DarkSteel
            else
                MetalChromeTheme.WorkshopFloor
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected)
                MetalChromeTheme.Chrome
            else
                MetalChromeTheme.Steel.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = if (isSelected) MetalChromeTheme.MetalText else MetalChromeTheme.DarkMetalText,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ===== ИНДУСТРИАЛЬНЫЙ ПАРАМЕТР ТЕЛЕМЕТРИИ =====
@Composable
fun IndustrialTelemetryItem(
    icon: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(60.dp)
    ) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MetalChromeTheme.MetalText,
            maxLines = 1
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = MetalChromeTheme.DarkMetalText,
            maxLines = 1,
            letterSpacing = 0.5.sp
        )
    }
}

// ===== МЕТАЛЛИЧЕСКИЙ ПЕРЕКЛЮЧАТЕЛЬ =====
@Composable
fun MetalSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(48.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                color = if (checked)
                    MetalChromeTheme.Chrome.copy(alpha = 0.3f)
                else
                    MetalChromeTheme.DarkSteel.copy(alpha = 0.5f)
            )
            .border(
                width = 1.dp,
                color = if (checked)
                    MetalChromeTheme.Chrome.copy(alpha = 0.5f)
                else
                    MetalChromeTheme.DarkMetalText.copy(alpha = 0.3f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .padding(3.dp)
                .clip(CircleShape)
                .background(
                    color = if (checked)
                        MetalChromeTheme.Chrome
                    else
                        MetalChromeTheme.DarkMetalText
                )
                .border(
                    width = 1.dp,
                    color = if (checked)
                        Color.White.copy(alpha = 0.3f)
                    else
                        MetalChromeTheme.FactoryDark,
                    shape = CircleShape
                )
        )
    }
}

// ===== МЕТАЛЛИЧЕСКАЯ СТАТУСНАЯ ПАНЕЛЬ =====
@Composable
fun MetalStatusPanel(
    label: String,
    value: String,
    statusColor: Color = MetalChromeTheme.StatusGreen,
    modifier: Modifier = Modifier
) {
    MetalCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = MetalChromeTheme.DarkMetalText,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Text(
                    text = value,
                    color = MetalChromeTheme.MetalText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}