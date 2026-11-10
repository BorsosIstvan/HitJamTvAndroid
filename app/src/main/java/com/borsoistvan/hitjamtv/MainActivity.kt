package com.borsosistvan.hitjamtv

import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
class MainActivity : ComponentActivity() {
    
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var statusTextView: TextView
    private lateinit var buttonRow: LinearLayout
    
    private var isFullScreen = false
    private var currentFocusedUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Videomotor initialiseren
        exoPlayer = ExoPlayer.Builder(this).build()

        // Hoofdindeling (Verticaal)
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#121212"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Status tekst bovenin
        statusTextView = TextView(this).apply {
            text = "HitJam TV - Válassz csatornát!"
            setTextColor(Color.parseColor("#FF4500"))
            textSize = 24f
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 20)
        }
        mainLayout.addView(statusTextView)

        // 2. Videovenster (PlayerView) in 16:9 verhouding
        playerView = PlayerView(this).apply {
            player = exoPlayer
            useController = false // Geen balken zichtbaar tijdens het zappen in de lijst
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f // Vult de ruimte boven de knoppen op
            )
        }
        mainLayout.addView(playerView)

        // Horizontale rij voor de 3 tv-knoppen
        buttonRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(20, 30, 20, 30)
            }
        }

        // De streams voor M1, M2 en M5 via ConnectMedia omleidingen
        val channels = listOf(
            Pair("M1", "https://c201-node61-cdn.connectmedia.hu/110301/e475b5226d338c8b2eb3b9d0e6bdd778/6af3619f/index.m3u8"),
            Pair("M2", "https://c402-node62-cdn.connectmedia.hu/110302/10cdfae4bad5ae9225e08db12b9f6a56/6af361ef/index.m3u8"),
            Pair("M5", "https://c402-node61-cdn.connectmedia.hu/110305/baab752ce1dabe13b3bc0fb9f2ad5ce1/6af3623d/index.m3u8")
        )

        channels.forEachIndexed { index, channel ->
            val channelButton = Button(this).apply {
                text = channel.first
                textSize = 20f
                isFocusable = true
                
                layoutParams = LinearLayout.LayoutParams(0, 150, 1f).apply {
                    setMargins(15, 0, 15, 0)
                }

                // Luisteren naar de afstandsbediening focus (Gecorrigeerd naar hasFocus)
                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        // Oranje neon-stijl wanneer geselecteerd met afstandsbediening
                        setBackgroundColor(Color.parseColor("#FF4500"))
                        setTextColor(Color.WHITE)
                        
                        // Start de live stream preview direct zodra je op de knop gaat staan
                        if (currentFocusedUrl != channel.second) {
                            currentFocusedUrl = channel.second
                            playStream(channel.second, channel.first)
                        }
                    } else {
                        // Normale grijze knop als de afstandsbediening er weer vanaf gaat
                        setBackgroundColor(Color.parseColor("#1E1E1E"))
                        setTextColor(Color.GRAY)
                    }
                }

                // Wanneer je op Enter drukt gaat de video naar Full-screen
                setOnClickListener {
                    toggleFullScreen(true)
                }
                
                if (index == 0) {
                    requestFocus()
                }
            }
            buttonRow.addView(channelButton)
        }

        mainLayout.addView(buttonRow)
        setContentView(mainLayout)
    }

    private fun playStream(url: String, channelName: String) {
        statusTextView.text = "Élő előnézet: $channelName"
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    // Wisselen tussen volledig scherm en mini-lijst modus
    private fun toggleFullScreen(goFullscreen: Boolean) {
        isFullScreen = goFullscreen
        if (goFullscreen) {
            statusTextView.visibility = View.GONE
            buttonRow.visibility = View.GONE
            
            playerView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            playerView.useController = true // Toon play/pause balken op volledig scherm
        } else {
            statusTextView.visibility = View.VISIBLE
            buttonRow.visibility = View.VISIBLE
            
            playerView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            playerView.useController = false
            
            // Zet de afstandsbediening focus direct terug op de zenderlijst
            buttonRow.getChildAt(0).requestFocus()
        }
    }

    // Luisteren naar de 'Terug' (Back) knop van de tv-afstandsbediening
    override fun onBackPressed() {
        if (isFullScreen) {
            // Als de video groot is, brengt de Terug-knop je weer naar de zenderlijst
            toggleFullScreen(false)
        } else {
            // Als je al in de lijst stond, sluit de app netjes af
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
