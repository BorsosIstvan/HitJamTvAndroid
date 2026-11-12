package com.borsosistvan.hitjamtv

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                HitJamTvApp()
            }
        }
    }
}

@Composable
fun HitJamTvApp() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var stations by remember { mutableStateOf(DefaultStations.list) }
    var activeStation by remember { mutableStateOf(stations.first()) }
    var isFullScreen by remember { mutableStateOf(false) }
    var dataSourceText by remember { mutableStateOf("Helyi lista") }

    LaunchedEffect(Unit) {
        val onlineList = StationRepository.fetchOnlineStations()
        if (onlineList != null) {
            stations = onlineList
            activeStation = onlineList.first()
            dataSourceText = "Online lista (GitHub)"
        }
    }

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    // Home gomb megnyomásakor azonnal leállítja a lejátszót
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    exoPlayer.stop()
                    exoPlayer.clearMediaItems()
                }
                Lifecycle.Event.ON_START -> {
                    exoPlayer.setMediaItem(MediaItem.fromUri(activeStation.payload))
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    LaunchedEffect(activeStation) {
        delay(350)
        exoPlayer.setMediaItem(MediaItem.fromUri(activeStation.payload))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    BackHandler(enabled = isFullScreen) {
        isFullScreen = false
    }

    if (isFullScreen) {
        FullScreenPlayer(exoPlayer = exoPlayer)
    } else {
        val focusRequesters = remember(stations) { List(stations.size) { FocusRequester() } }

        LaunchedEffect(stations, isFullScreen) {
            delay(150)
            val activeIndex = stations.indexOfFirst { it.id == activeStation.id }.coerceAtLeast(0)
            try {
                focusRequesters.getOrNull(activeIndex)?.requestFocus()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HitJam TV • ${activeStation.name}",
                    color = Color(0xFFFF4500),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(text = dataSourceText, color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(stations, key = { _, station -> station.id }) { index, station ->
                    StationCard(
                        station = station,
                        exoPlayer = exoPlayer,
                        focusRequester = focusRequesters[index],
                        onFocused = { activeStation = station },
                        onClick = {
                            activeStation = station
                            isFullScreen = true
                        }
                    )
                }
            }
        }
    }
}
