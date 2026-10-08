package com.marvel.recruiter.ui.theme

import android.provider.Settings
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

object Motion {
    const val PRESS = 120
    const val STATE = 200
    const val ENTER = 320
    const val FILL = 600
    const val PACK_SHAKE = 900
    const val FLIP = 500
    const val DICE = 1400
    const val BURST = 450
    const val ROUND_STEP = 280
    const val ROUND_LUNGE = 90
    const val ROUND_ENTRY = 350
    const val ROUND_IMPACT = 170
    const val ROUND_HOLD = 300L
}

val EaseStandard: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
val EaseDecelerate: Easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
val EaseAccelerate: Easing = CubicBezierEasing(0.4f, 0f, 1f, 1f)
val EaseDice: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

/** Tween que vira instantâneo com reduced motion (design.md 2.6). */
fun <T> motionTween(ms: Int, reduced: Boolean, easing: Easing = EaseStandard): TweenSpec<T> =
    tween(durationMillis = if (reduced) 0 else ms, easing = easing)

/** `true` quando o usuário desligou animações do sistema (escala de animação = 0). */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f,
            ) == 0f
        } catch (e: Exception) {
            false
        }
    }
}
