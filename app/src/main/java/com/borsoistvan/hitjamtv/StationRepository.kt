package com.borsosistvan.hitjamtv

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object StationRepository {
    suspend fun fetchOnlineStations(): List<Station>? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://borsosistvan.github.io/hitjam-tv-app/stations.json")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 4000
            connection.readTimeout = 4000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                val listType = object : TypeToken<List<Station>>() {}.type
                val onlineStations: List<Station> = Gson().fromJson(jsonText, listType)
                if (onlineStations.isNotEmpty()) {
                    return@withContext onlineStations
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}
