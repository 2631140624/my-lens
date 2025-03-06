package com.shuzhi.opencv.ui.theme.mlkit.scanner

import android.app.Activity
import android.widget.Toast
import androidx.activity.ComponentActivity

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.launch

private class DocumentScannerImpl(
    private val context: ComponentActivity,
    private val scannerLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    private val onFailure: (Throwable) -> Unit
) : DocumentScanner {

    override fun scan() {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setResultFormats(
                GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                GmsDocumentScannerOptions.RESULT_FORMAT_PDF
            )
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)

        scanner.getStartScanIntent(context)
            .addOnSuccessListener { intentSender ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener(onFailure)
    }

}
@Composable
fun rememberDocumentScanner(
    onSuccess: (ScanResult) -> Unit
): DocumentScanner {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current as ComponentActivity

    val scannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            GmsDocumentScanningResult.fromActivityResultIntent(result.data)?.apply {
                onSuccess(
                    ScanResult(
                        imageUris = pages?.let { pages ->
                            pages.map { it.imageUri }
                        } ?: emptyList(),
                        pdfUri = pdf?.uri
                    )
                )
            }
        }
    }

    return remember(context, scannerLauncher) {
        DocumentScannerImpl(
            context = context,
            scannerLauncher = scannerLauncher,
            onFailure = {
                scope.launch {
                    Toast.makeText(context,"${it.message}",Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}