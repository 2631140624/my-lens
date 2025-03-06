package com.shuzhi.opencv.ui.theme.mlkit.scanner

import android.net.Uri
import androidx.camera.video.Quality
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shuzhi.opencv.ui.theme.util.runSuspendCatching
import com.shuzhi.opencv.ui.theme.util.state.update
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class  DocumentScannerViewmodel@Inject constructor(): ViewModel() {
    private val _uris = mutableStateOf<List<Uri>>(emptyList())
    val uris by _uris

    private val _pdfUris = mutableStateOf<List<Uri>>(emptyList())

    private suspend fun getPdfUri(): Uri? =
        if (_pdfUris.value.size > 1 || _pdfUris.value.isEmpty()) {
            createPdfUri()
        } else _pdfUris.value.firstOrNull()

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    private val _done: MutableState<Int> = mutableIntStateOf(0)
    val done by _done

    private val _left: MutableState<Int> = mutableIntStateOf(-1)
    val left by _left

    fun parseScanResult(scanResult: ScanResult) {
        if (scanResult.imageUris.isNotEmpty()) {
            _uris.update { scanResult.imageUris }
        }
        if (scanResult.pdfUri != null) {
            _pdfUris.update { listOfNotNull(scanResult.pdfUri) }
        }

    }


    fun saveBitmaps(
        oneTimeSaveLocationUri: String?,
        onComplete: (List<SaveResult>) -> Unit
    ) {

    }

    fun savePdfTo(
        uri: Uri,
        onResult: (SaveResult) -> Unit
    ) {

    }

    private suspend fun createPdfUri(): Uri? {

        return null
    }

    fun generatePdfFilename(): String {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date()) + "_${Random(Random.nextInt()).hashCode().toString().take(4)}"
        return "PDF_$timeStamp.pdf"
    }

    fun sharePdf(
        onComplete: () -> Unit
    ) {

    }


    fun cancelSaving() {
    }

    fun removeImageUri(uri: Uri) {

    }

    fun addScanResult(scanResult: ScanResult) {

    }

    fun shareBitmaps(onComplete: () -> Unit) {

    }


    fun shareUri(uri: Uri) {

    }
}

