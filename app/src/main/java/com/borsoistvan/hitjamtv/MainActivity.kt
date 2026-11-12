package com.borsosistvan.hitjamtv

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

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

@OptIn(UnstableApi::class)
@Composable
fun HitJamTvApp() {
    val context = LocalContext.current

    // Alapból a Station.kt-ben lévő tartalék listával indulunk, így sosem üres a képernyő
    var stations by remember { mutableStateOf(DefaultStations.list) }
    var activeStation by remember { mutableStateOf(stations.first()) }
    var isFullScreen by remember { mutableStateOf(false) }
    var dataSourceText by remember { mutableStateOf("Helyi lista") }

    // Háttérben megpróbáljuk letölteni a friss listát a GitHubról
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                // IDE ÍRD A SAJÁT GITHUB PAGES JSON LINKEDET!
                val url = URL("https://borsosistvan.github.io/hitjam-tv-app/stations.json")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 4000
                connection.readTimeout = 4000

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                    val listType = object : TypeToken<List<Station>>() {}.type
                    val onlineStations: List<Station> = Gson().fromJson(jsonText, listType)

                    if (onlineStations.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            stations = onlineStations
                            activeStation = onlineStations.first()
                            dataSourceText = "Online lista (GitHub)"
                        }
                    }
                }
            } catch (e: Exception) {
                // Ha nincs net vagy hibás a link, csendben marad a DefaultStations.list!
                e.printStackTrace()
            }
        }
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(activeStation) {
        val mediaItem = MediaItem.fromUri(activeStation.payload)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    BackHandler(enabled = isFullScreen) {
        isFullScreen = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isFullScreen) Color.Black else Color(0xFF121212))
            .padding(if (isFullScreen) 0.dp else 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isFullScreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Élő adás: ${activeStation.name}",
                    color = Color(0xFFFF4500),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dataSourceText,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Box(
            modifier = if (isFullScreen) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .weight(1f)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
            }
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                update = { playerView ->
                    playerView.useController = isFullScreen
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (!isFullScreen) {
            Spacer(modifier = Modifier.height(24.dp))

            val firstButtonFocus = remember { FocusRequester() }
            LaunchedEffect(stations) {
                delay(200)
                try {
                    firstButtonFocus.requestFocus()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // LazyRow: Vízszintesen görgethető gombsor, így akár 50 csatorna is elfér!
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(stations) { index, station ->
                    var isFocused by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .width(220.dp) // Fix szélesség a görgethetőség miatt
                            .height(100.dp)
                            .then(if (index == 0) Modifier.focusRequester(firstButtonFocus) else Modifier)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isFocused) Color(0xFFFF4500) else Color(0xFF1E1E1E))
                            .border(
                                BorderStroke(3.dp, if (isFocused) Color(0xFFFF8C00) else Color(0xFF333333)),
                                RoundedCornerShape(20.dp)
                            )
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                if (focusState.isFocused) {
                                    activeStation = station
                                }
                            }
                            .focusable()
                            .clickable {
                                activeStation = station
                                isFullScreen = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${station.logo} ${station.name}",
                                color = if (isFocused) Color.White else Color.Gray,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = station.genre,
                                color = if (isFocused) Color.White.copy(alpha = 0.8f) else Color.DarkGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
