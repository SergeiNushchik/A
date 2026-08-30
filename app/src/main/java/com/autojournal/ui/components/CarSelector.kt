package com.autojournal.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.autojournal.data.model.Car

@Composable
fun CarSelector(
    cars: List<Car>,
    selectedCar: Car?,
    onCarSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cars.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        cars.forEach { car ->
            FilterChip(
                selected = car.id == selectedCar?.id,
                onClick = { onCarSelected(car.id) },
                label = {
                    Text(
                        text = if (car.brand.isNotBlank()) {
                            "${car.brand} ${car.model}".take(15)
                        } else {
                            car.plate.ifEmpty { "Авто" }
                        }
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}