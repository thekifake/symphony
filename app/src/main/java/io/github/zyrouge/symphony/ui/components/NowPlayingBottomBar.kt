package io.github.zyrouge.symphony.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.zyrouge.symphony.services.radio.PlaybackPosition
import io.github.zyrouge.symphony.ui.helpers.Routes
import io.github.zyrouge.symphony.ui.helpers.ViewContext
import io.github.zyrouge.symphony.ui.helpers.navigate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingBottomBar(context: ViewContext) {
    var currentPlayingSong by remember {
        mutableStateOf(context.symphony.radio.queue.currentPlayingSong)
    }
    var isPlaying by remember { mutableStateOf(context.symphony.radio.isPlaying) }

    EventerEffect(context.symphony.radio.onUpdate) {
        currentPlayingSong = context.symphony.radio.queue.currentPlayingSong
        isPlaying = context.symphony.radio.isPlaying
    }

    AnimatedVisibility(
        visible = currentPlayingSong != null,
        enter = slideIn {
            IntOffset(0, it.height / 2)
        } + fadeIn(),
        exit = slideOut {
            IntOffset(0, it.height / 2)
        } + fadeOut()
    ) {
        currentPlayingSong?.let { song ->
            Column {
                Box(
                    modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                ) {
                    var duration by remember {
                        mutableStateOf(
                            context.symphony.radio.currentPlaybackPosition ?: PlaybackPosition.zero
                        )
                    }
                    EventerEffect(context.symphony.radio.onPlaybackPositionUpdate) {
                        duration = it
                    }
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(0.3f))
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxWidth(duration.toRatio())
                            .fillMaxHeight()
                    )
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RectangleShape,
                    onClick = {
                        context.navController.navigate(Routes.NowPlaying)
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(12.dp, 8.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    song.getArtwork(context.symphony, 150)
                                        .asImageBitmap(),
                                    null,
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(15.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(song.title, style = MaterialTheme.typography.bodyMedium)
                                    song.artistName?.let { artistName ->
                                        Text(
                                            artistName,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(15.dp))
                                IconButton(
                                    onClick = { context.symphony.radio.shorty.playPause() }
                                ) {
                                    Icon(
                                        if (!isPlaying) Icons.Default.PlayArrow
                                        else Icons.Default.Pause,
                                        null
                                    )
                                }
                                IconButton(
                                    onClick = { context.symphony.radio.shorty.skip() }
                                ) {
                                    Icon(Icons.Default.SkipNext, null)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
