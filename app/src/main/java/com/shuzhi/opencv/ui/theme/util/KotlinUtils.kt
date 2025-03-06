package com.shuzhi.opencv.ui.theme.util

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <T, R> T.runSuspendCatching(block: T.() -> R): Result<R> {
    currentCoroutineContext().ensureActive()

    return try {
        Result.success(block())
    } catch (e: Throwable) {
        currentCoroutineContext().ensureActive()
        Result.failure(e)
    }
}
inline fun <reified T> T?.notNullAnd(
    predicate: (T) -> Boolean
): Boolean = if (this != null) predicate(this)
else false

fun CharSequence.isBase64() = isNotEmpty() && BASE64_PATTERN.matches(this)

fun CharSequence.trimToBase64() = toString().filter { !it.isWhitespace() }.substringAfter(",")

private val BASE64_PATTERN = Regex(
    "^(?=(.{4})*\$)[A-Za-z0-9+/]*={0,2}\$"
)

inline fun <reified T, reified R> T.cast(): R = this as R

inline fun <reified T, reified R> T.safeCast(): R? = this as? R


inline operator fun CharSequence.times(
    count: Int
): CharSequence = repeat(count.coerceAtLeast(0))