package com.marvel.recruiter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.marvel.recruiter.ui.theme.Borders
import com.marvel.recruiter.ui.theme.EaseStandard
import com.marvel.recruiter.ui.theme.Motion
import com.marvel.recruiter.ui.theme.Sizes
import com.marvel.recruiter.ui.theme.Space
import com.marvel.recruiter.ui.theme.motionTween
import com.marvel.recruiter.ui.theme.rememberReducedMotion
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * Ação principal. Com [enabled] false, [supportingText] explica o motivo (anunciado pelo TalkBack).
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    supportingText: String? = null,
) {
    val colors = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val reduced = rememberReducedMotion()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = motionTween(Motion.PRESS, reduced, EaseStandard),
        label = "pressScale",
    )
    Column(modifier.fillMaxWidth()) {
        Button(
            onClick = onClick,
            enabled = enabled && !loading,
            interactionSource = interaction,
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
                disabledContainerColor = colors.outline.copy(alpha = 0.3f),
                disabledContentColor = colors.onSurfaceVariant,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Sizes.buttonHeight)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .semantics { if (supportingText != null && !enabled) stateDescription = supportingText },
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = Borders.thick,
                    color = colors.onPrimary,
                )
                Spacer(Modifier.width(Space.s2))
            }
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                modifier = Modifier.padding(top = Space.s1),
            )
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(Borders.medium, colors.secondary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.secondary,
            disabledContentColor = colors.onSurfaceVariant,
        ),
        modifier = modifier.fillMaxWidth().heightIn(min = Sizes.buttonHeight),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = Sizes.touchMin),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}
