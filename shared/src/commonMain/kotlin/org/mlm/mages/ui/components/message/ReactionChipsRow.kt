package org.mlm.mages.ui.components.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.mlm.mages.matrix.ReactionSummary

enum class ReactionChipStyle {
    Timeline,
    ThreadRoot,
}

@Composable
fun ReactionChipsRow(
    chips: List<ReactionSummary>,
    modifier: Modifier = Modifier,
    style: ReactionChipStyle = ReactionChipStyle.Timeline,
    maxVisible: Int? = null,
    onClick: ((String) -> Unit)? = null,
) {
    if (chips.isEmpty()) return

    val visible = maxVisible?.let { chips.take(it) } ?: chips

    FlowRow(
        modifier = modifier.padding(top = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        visible.forEach { chip ->
            val colors = when (style) {
                ReactionChipStyle.Timeline -> InputChipDefaults.inputChipColors()
                ReactionChipStyle.ThreadRoot -> InputChipDefaults.inputChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            InputChip(
                selected = chip.mine,
                onClick = { onClick?.invoke(chip.key) },
                label = { Text("${chip.key} ${chip.count}") },
                colors = colors,
            )
        }
    }
}
