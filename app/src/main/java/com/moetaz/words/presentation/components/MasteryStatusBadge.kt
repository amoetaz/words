package com.moetaz.words.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moetaz.words.domain.model.WordMasteryStatus

@Composable
fun MasteryStatusBadge(
    status: WordMasteryStatus,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (status) {
        WordMasteryStatus.LEARNING -> MaterialTheme.colorScheme.tertiaryContainer
        WordMasteryStatus.REVIEWING -> MaterialTheme.colorScheme.secondaryContainer
        WordMasteryStatus.MASTERED -> MaterialTheme.colorScheme.primaryContainer
    }

    val badgeTextColor = when (status) {
        WordMasteryStatus.LEARNING -> MaterialTheme.colorScheme.onTertiaryContainer
        WordMasteryStatus.REVIEWING -> MaterialTheme.colorScheme.onSecondaryContainer
        WordMasteryStatus.MASTERED -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Surface(
        modifier = modifier,
        color = badgeColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.name,
            color = badgeTextColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
