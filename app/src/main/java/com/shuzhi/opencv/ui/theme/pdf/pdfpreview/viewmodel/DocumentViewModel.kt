package com.shuzhi.opencv.ui.theme.pdf.pdfpreview.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.di.IoDispatcher
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.DocumentItem
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// presentation/viewmodel/DocumentViewModel.kt
@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val repository: DocumentRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _documents = MutableStateFlow<List<DocumentItem>>(emptyList())
    val documents: StateFlow<List<DocumentItem>> = _documents.asStateFlow()

    private val _selectedDocument = MutableStateFlow<DocumentItem?>(null)
    val selectedDocument: StateFlow<DocumentItem?> = _selectedDocument.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        viewModelScope.launch(ioDispatcher) {
            _loadingState.value = true
            _documents.value = repository.loadLocalDocuments()
            Log.d("DocumentViewModel", "Documents loaded: ${documents.value.size}")
            _loadingState.value = false
        }
    }

    fun selectDocument(document: DocumentItem) {
        _selectedDocument.value = document
    }

    fun clearSelection() {
        _selectedDocument.value = null
    }

    fun shareDocument(context: Context, document: DocumentItem) {
        Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, document.uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            },
            "分享文档"
        ).also { context.startActivity(it) }
    }

    fun openDocument(context: Context, document: DocumentItem) {
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(document.uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            )
        } catch (e: ActivityNotFoundException) {
//            context.startActivity(
//                Intent(context, PdfViewerActivity::class.java).apply {
//                    putExtra("PDF_URI", document.uri.toString())
//                }
//            )
        }
    }
}