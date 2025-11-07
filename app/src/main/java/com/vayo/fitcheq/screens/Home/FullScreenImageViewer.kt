package com.vayo.fitcheq.screens.Home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun FullScreenImageViewer(
    images: List<String>,
    startIndex: Int = 0,
    onClose: () -> Unit
){
    val pagerState = rememberPagerState(
        initialPage = startIndex,
        pageCount = { images.size }
    )
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding()
    ) {
        // Main zoomable image pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.85f)
        ) { page ->
            ZoomableImage(url = images[page])
        }

        // Thumbnails row
        LazyRow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            itemsIndexed(images) { index, url ->
                AsyncImage(
                    model = url,
                    contentDescription = "Thumbnail $index",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(
                            width = if (index == pagerState.currentPage) 2.dp else 0.dp,
                            color = if (index == pagerState.currentPage) Color.White else Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                )
            }
        }

        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .padding(top = 10.dp, start = 14.dp)
                .background(
                    color = Color.White.copy(alpha = 0.4f),
                    shape = CircleShape
                )
                .size(32.dp)
                .align(Alignment.TopStart),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Black.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ZoomableImage(url: String) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    val newOffset = offset + pan * scale
                    val maxX = (size.width * (scale - 1)) / 2
                    val maxY = (size.height * (scale - 1)) / 2
                    offset = Offset(
                        newOffset.x.coerceIn(-maxX, maxX),
                        newOffset.y.coerceIn(-maxY, maxY)
                    )
                }
            }
    )
}

