package net.mullvad.mullvadvpn.lib.theme

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun Animatable(initialValue: ColorScheme): AnimatableColorScheme =
    AnimatableColorScheme(
        primary = Animatable(initialValue.primary),
        onPrimary = Animatable(initialValue.onPrimary),
        primaryContainer = Animatable(initialValue.primaryContainer),
        onPrimaryContainer = Animatable(initialValue.onPrimaryContainer),
        inversePrimary = Animatable(initialValue.inversePrimary),
        secondary = Animatable(initialValue.secondary),
        onSecondary = Animatable(initialValue.onSecondary),
        secondaryContainer = Animatable(initialValue.secondaryContainer),
        onSecondaryContainer = Animatable(initialValue.onSecondaryContainer),
        tertiary = Animatable(initialValue.tertiary),
        onTertiary = Animatable(initialValue.onTertiary),
        tertiaryContainer = Animatable(initialValue.tertiaryContainer),
        onTertiaryContainer = Animatable(initialValue.onTertiaryContainer),
        background = Animatable(initialValue.background),
        onBackground = Animatable(initialValue.onBackground),
        surface = Animatable(initialValue.surface),
        onSurface = Animatable(initialValue.onSurface),
        surfaceVariant = Animatable(initialValue.surfaceVariant),
        onSurfaceVariant = Animatable(initialValue.onSurfaceVariant),
        surfaceTint = Animatable(initialValue.surfaceTint),
        inverseSurface = Animatable(initialValue.inverseSurface),
        inverseOnSurface = Animatable(initialValue.inverseOnSurface),
        error = Animatable(initialValue.error),
        onError = Animatable(initialValue.onError),
        errorContainer = Animatable(initialValue.errorContainer),
        onErrorContainer = Animatable(initialValue.onErrorContainer),
        outline = Animatable(initialValue.outline),
        outlineVariant = Animatable(initialValue.outlineVariant),
        scrim = Animatable(initialValue.scrim)
    )

class AnimatableColorScheme(
    val primary: Animatable<Color, AnimationVector4D>,
    val onPrimary: Animatable<Color, AnimationVector4D>,
    val primaryContainer: Animatable<Color, AnimationVector4D>,
    val onPrimaryContainer: Animatable<Color, AnimationVector4D>,
    val inversePrimary: Animatable<Color, AnimationVector4D>,
    val secondary: Animatable<Color, AnimationVector4D>,
    val onSecondary: Animatable<Color, AnimationVector4D>,
    val secondaryContainer: Animatable<Color, AnimationVector4D>,
    val onSecondaryContainer: Animatable<Color, AnimationVector4D>,
    val tertiary: Animatable<Color, AnimationVector4D>,
    val onTertiary: Animatable<Color, AnimationVector4D>,
    val tertiaryContainer: Animatable<Color, AnimationVector4D>,
    val onTertiaryContainer: Animatable<Color, AnimationVector4D>,
    val background: Animatable<Color, AnimationVector4D>,
    val onBackground: Animatable<Color, AnimationVector4D>,
    val surface: Animatable<Color, AnimationVector4D>,
    val onSurface: Animatable<Color, AnimationVector4D>,
    val surfaceVariant: Animatable<Color, AnimationVector4D>,
    val onSurfaceVariant: Animatable<Color, AnimationVector4D>,
    val surfaceTint: Animatable<Color, AnimationVector4D>,
    val inverseSurface: Animatable<Color, AnimationVector4D>,
    val inverseOnSurface: Animatable<Color, AnimationVector4D>,
    val error: Animatable<Color, AnimationVector4D>,
    val onError: Animatable<Color, AnimationVector4D>,
    val errorContainer: Animatable<Color, AnimationVector4D>,
    val onErrorContainer: Animatable<Color, AnimationVector4D>,
    val outline: Animatable<Color, AnimationVector4D>,
    val outlineVariant: Animatable<Color, AnimationVector4D>,
    val scrim: Animatable<Color, AnimationVector4D>,
) {
    val value =
        ColorScheme(
            primary = primary.value,
            onPrimary = onPrimary.value,
            primaryContainer = primaryContainer.value,
            onPrimaryContainer = onPrimaryContainer.value,
            inversePrimary = inversePrimary.value,
            secondary = secondary.value,
            onSecondary = onSecondary.value,
            secondaryContainer = secondaryContainer.value,
            onSecondaryContainer = onSecondaryContainer.value,
            tertiary = tertiary.value,
            onTertiary = onTertiary.value,
            tertiaryContainer = tertiaryContainer.value,
            onTertiaryContainer = onTertiaryContainer.value,
            background = background.value,
            onBackground = onBackground.value,
            surface = surface.value,
            onSurface = onSurface.value,
            surfaceVariant = surfaceVariant.value,
            onSurfaceVariant = onSurfaceVariant.value,
            surfaceTint = surfaceTint.value,
            inverseSurface = inverseSurface.value,
            inverseOnSurface = inverseOnSurface.value,
            error = error.value,
            onError = onError.value,
            errorContainer = errorContainer.value,
            onErrorContainer = onErrorContainer.value,
            outline = outline.value,
            outlineVariant = outlineVariant.value,
            scrim = scrim.value
        )

    suspend fun animateTo(otherColorScheme: ColorScheme) {
        primary.animateTo(otherColorScheme.primary)
        onPrimary.animateTo(otherColorScheme.onPrimary)
        primaryContainer.animateTo(otherColorScheme.primaryContainer)
        onPrimaryContainer.animateTo(otherColorScheme.onPrimaryContainer)
        inversePrimary.animateTo(otherColorScheme.inversePrimary)
        secondary.animateTo(otherColorScheme.secondary)
        onSecondary.animateTo(otherColorScheme.onSecondary)
        secondaryContainer.animateTo(otherColorScheme.secondaryContainer)
        onSecondaryContainer.animateTo(otherColorScheme.onSecondaryContainer)
        tertiary.animateTo(otherColorScheme.tertiary)
        onTertiary.animateTo(otherColorScheme.onTertiary)
        tertiaryContainer.animateTo(otherColorScheme.tertiaryContainer)
        onTertiaryContainer.animateTo(otherColorScheme.onTertiaryContainer)
        background.animateTo(otherColorScheme.background)
        onBackground.animateTo(otherColorScheme.onBackground)
        surface.animateTo(otherColorScheme.surface)
        onSurface.animateTo(otherColorScheme.onSurface)
        surfaceVariant.animateTo(otherColorScheme.surfaceVariant)
        onSurfaceVariant.animateTo(otherColorScheme.onSurfaceVariant)
        surfaceTint.animateTo(otherColorScheme.surfaceTint)
        inverseSurface.animateTo(otherColorScheme.inverseSurface)
        inverseOnSurface.animateTo(otherColorScheme.inverseOnSurface)
        error.animateTo(otherColorScheme.error)
        onError.animateTo(otherColorScheme.onError)
        errorContainer.animateTo(otherColorScheme.errorContainer)
        onErrorContainer.animateTo(otherColorScheme.onErrorContainer)
        outline.animateTo(otherColorScheme.outline)
        outlineVariant.animateTo(otherColorScheme.outlineVariant)
        scrim.animateTo(otherColorScheme.scrim)
    }
}

private val animationSpec: AnimationSpec<Color> = tween(durationMillis = 1500)

@Composable
private fun animateColor(targetValue: Color, finishedListener: ((Color) -> Unit)? = null) =
    animateColorAsState(targetValue = targetValue, animationSpec = animationSpec).value

@Composable
fun ColorScheme.switch(): ColorScheme =
    this.copy(
        primary = animateColor(primary),
        onPrimary = animateColor(onPrimary),
        primaryContainer = animateColor(primaryContainer),
        onPrimaryContainer = animateColor(onPrimaryContainer),
        inversePrimary = animateColor(inversePrimary),
        secondary = animateColor(secondary),
        onSecondary = animateColor(onSecondary),
        secondaryContainer = animateColor(secondaryContainer),
        onSecondaryContainer = animateColor(onSecondaryContainer),
        tertiary = animateColor(tertiary),
        onTertiary = animateColor(onTertiary),
        tertiaryContainer = animateColor(tertiaryContainer),
        onTertiaryContainer = animateColor(onTertiaryContainer),
        background = animateColor(background),
        onBackground = animateColor(onBackground),
        surface = animateColor(surface),
        onSurface = animateColor(onSurface),
        surfaceVariant = animateColor(surfaceVariant),
        onSurfaceVariant = animateColor(onSurfaceVariant),
        surfaceTint = animateColor(surfaceTint),
        inverseSurface = animateColor(inverseSurface),
        inverseOnSurface = animateColor(inverseOnSurface),
        error = animateColor(error),
        onError = animateColor(onError),
        errorContainer = animateColor(errorContainer),
        onErrorContainer = animateColor(onErrorContainer),
        outline = animateColor(outline),
        outlineVariant = animateColor(outlineVariant),
        scrim = animateColor(scrim)
    )
