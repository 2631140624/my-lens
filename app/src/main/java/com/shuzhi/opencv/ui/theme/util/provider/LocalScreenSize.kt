package com.shuzhi.opencv.ui.theme.util.provider

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.shuzhi.opencv.ui.theme.util.FullscreenPopup

val LocalScreenSize = compositionLocalOf<ScreenSize> { error("ScreenSize not present") }

data class ScreenSize internal constructor(
    val width: Dp,
    val height: Dp,
    val widthPx: Int,
    val heightPx: Int
)

private fun Density.ScreenSize(
    width: Dp,
    height: Dp,
) = ScreenSize(
    width = width,
    height = height,
    widthPx = width.roundToPx(),
    heightPx = height.roundToPx()
)

@Composable
fun rememberScreenSize(): ScreenSize {
    val configuration = LocalConfiguration.current

    var constraints by remember(configuration) {
        mutableStateOf<Constraints?>(null)
    }

    if (constraints == null) {
        FullscreenPopup {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                SideEffect {
                    constraints = this.constraints
                }
            }
        }
    }

    val density = LocalDensity.current

    return remember(constraints, configuration, density) {
        derivedStateOf {
            with(density) {
                ScreenSize(
                    width = constraints?.maxWidth?.toDp() ?: configuration.screenWidthDp.dp,
                    height = constraints?.maxHeight?.toDp() ?: configuration.screenHeightDp.dp,
                )
            }
        }
    }.value
}

@Composable
fun rememberCurrentLifecycleEvent(): Lifecycle.Event =
    LocalLifecycleOwner.current.lifecycle.observeAsState().value

@Composable
fun Lifecycle.observeAsState(): State<Lifecycle.Event> {
    val state = remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    DisposableEffect(this) {
        val observer = LifecycleEventObserver { _, event ->
            state.value = event
        }
        this@observeAsState.addObserver(observer)
        onDispose {
            this@observeAsState.removeObserver(observer)
        }
    }
    return state
}