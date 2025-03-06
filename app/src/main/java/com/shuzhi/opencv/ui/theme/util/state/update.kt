package com.shuzhi.opencv.ui.theme.util.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

 fun <T> MutableState<T>.update(
    transform: (T) -> T
): T = run {
    transform(this.value).also {
        this.value = it
    }
}

fun <T> MutableState<T>.update(
    onValueChanged: () -> Unit,
    transform: (T) -> T
): T = run {
    transform(this.value).also {
        if (this.value != it) onValueChanged()
        this.value = it
    }
}

 fun <T> MutableState<T>.updateIf(
    predicate: (T) -> Boolean,
    transform: (T) -> T
): MutableState<T> = apply {
    if (predicate(this.value)) {
        this.value = transform(this.value)
    }
}

@Composable
fun <T> derivedValueOf(
    vararg keys: Any?,
    calculation: () -> T
): T = remember(keys) {
    derivedStateOf(calculation)
}.value
