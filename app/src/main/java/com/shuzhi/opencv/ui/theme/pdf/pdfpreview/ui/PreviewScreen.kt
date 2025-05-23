package com.shuzhi.opencv.ui.theme.pdf.pdfpreview.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.model.DocumentItem
import com.shuzhi.opencv.ui.theme.pdf.pdfpreview.viewmodel.DocumentViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// presentation/screen/PreviewScreen.kt
@Composable
fun PreviewScreen(
    index: Int,
    modifier: Modifier = Modifier,
    viewModel: DocumentViewModel = hiltViewModel(),
) {


    LaunchedEffect(Unit) {
        viewModel.loadDocuments(isRemote = index == 1)
    }
    val documents by viewModel.documents.collectAsState()
    val selectedDocument by viewModel.selectedDocument.collectAsState()
    val isLoading by viewModel.loadingState.collectAsState()
    val context = LocalContext.current


    Box(modifier = modifier.fillMaxSize()) {
        DocumentGrid(
            index = index,
            documents = documents,
            onDocumentSelected = viewModel::selectDocument,
            isLoading = isLoading
        )

        selectedDocument?.let { doc ->
            ActionBottomSheet(
                document = doc,
                onOpen = { viewModel.openDocument(context, doc) },
                onShare = { viewModel.shareDocument(context, doc) },
                onDismiss = viewModel::clearSelection
            )
        }
    }
}

@Composable
private fun DocumentGrid(
    index: Int,
    documents: List<DocumentItem>,
    onDocumentSelected: (DocumentItem) -> Unit,
    isLoading: Boolean
) {
    Scaffold(
        topBar = {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    if (index==0)"本地PDF预览" else "云端PDF预览",
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }, content = { padding ->
            if (isLoading) {
                CircularProgressIndicator(Modifier.fillMaxSize())
                return@Scaffold
            }
            if (documents.isEmpty()) {
                Spacer(Modifier.height(36.dp))
                Text(
                    text = "没有可预览的PDF文件，请先添加文件",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            LazyVerticalStaggeredGrid(
                modifier = Modifier.padding(padding),
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(documents) { doc ->
                    DocumentCard(
                        document = doc,
                        onCardClick = { onDocumentSelected(doc) }
                    )
                }
            }
        })

}

@Composable
private fun DocumentCard(
    document: DocumentItem,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncThumbnail(bitmap = document.thumbnail, imageUrl = document.thumbnailUri)

            Text(
                text = document.fileName,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
                    .format(document.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun AsyncThumbnail(bitmap: Bitmap?, imageUrl: String? = null) {
    if (bitmap != null || imageUrl != null) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionBottomSheet(
    document: DocumentItem,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = document.fileName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            ActionButton(
                icon = Icons.Default.Done,
                text = "打开文件",
                onClick = onOpen
            )
            ActionButton(
                icon = Icons.Default.Share,
                text = "分享文档",
                onClick = onShare
            )
//            ActionButton(
//                icon = Icons.Default.Edit,
//                text = "编辑信息",
//                onClick = { /* TODO */ }
//            )

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("关闭")
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}