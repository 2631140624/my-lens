/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.shuzhi.opencv.ui.theme.util.provider

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import coil.imageLoader
import com.shuzhi.opencv.ui.theme.util.LocalToastHostState
import com.shuzhi.opencv.ui.theme.util.ToastHostState
import com.shuzhi.opencv.ui.theme.util.rememberToastHostState

/**
 * Composition locals for the application.
 */
@Composable
fun CompositionLocals(
    toastHostState: ToastHostState = rememberToastHostState(),
    content: @Composable () -> Unit
) {

    val context = LocalContext.current

    val screenSize = rememberScreenSize()

    val values = remember(
        toastHostState,
        context,
        screenSize
    ) {
        derivedStateOf {
            listOfNotNull(
                LocalToastHostState provides toastHostState,
                LocalImageLoader provides context.imageLoader,
                LocalScreenSize provides screenSize
            ).toTypedArray()
        }
    }

    CompositionLocalProvider(
        *values.value,
        content = content
    )
}

private infix fun <T : Any> ProvidableCompositionLocal<T>.providesOrNull(
    value: T?
): ProvidedValue<T>? = if (value != null) provides(value) else null