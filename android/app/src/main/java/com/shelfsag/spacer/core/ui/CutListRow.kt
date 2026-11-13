package com.shelfsag.spacer.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shelfsag.spacer.core.theme.Accent
import com.shelfsag.spacer.core.theme.Outline
import com.shelfsag.spacer.core.theme.RatingInvisible
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.theme.TextPrimary
import com.shelfsag.spacer.core.theme.TextSecondary

@Composable
fun CutListRow(
    name: String,
    detail: String?,
    quantity: String,
    bought: Boolean,
    toggleDescription: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .semantics { stateDescription = toggleDescription },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onToggle, modifier = Modifier.size(48.dp)) {
                Box24(bought = bought)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (bought) TextMuted else TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (detail != null) {
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = quantity,
                style = MaterialTheme.typography.labelMedium,
                color = Accent,
                maxLines = 1
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Outline)
        )
    }
}

@Composable
private fun Box24(bought: Boolean) {
    if (bought) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = RatingInvisible,
            modifier = Modifier.size(24.dp)
        )
    } else {
        Spacer(
            modifier = Modifier
                .size(20.dp)
                .border(1.5.dp, Outline, RoundedCornerShape(4.dp))
                .background(Color.Transparent, RoundedCornerShape(4.dp))
        )
    }
}
