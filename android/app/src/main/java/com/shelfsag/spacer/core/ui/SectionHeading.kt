package com.shelfsag.spacer.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.shelfsag.spacer.core.theme.Primary
import com.shelfsag.spacer.core.theme.TextMuted
import com.shelfsag.spacer.core.theme.TextPrimary
import com.shelfsag.spacer.core.theme.TextSecondary

@Composable
fun SectionHeading(
    eyebrow: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.displayLarge,
    ruleWidth: Int = 56
) {
    Column(modifier = modifier) {
        Text(
            text = eyebrow,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = titleStyle,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(
            modifier = Modifier
                .width(ruleWidth.dp)
                .height(3.dp)
                .background(Primary, RoundedCornerShape(2.dp))
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}
