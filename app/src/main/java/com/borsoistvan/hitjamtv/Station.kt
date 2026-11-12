package com.borsosistvan.hitjamtv

// Ez az adatmodell pontosan illeszkedik a stations.json felépítéséhez
data class Station(
    val id: String,
    val name: String,
    val genre: String,
    val logo: String,
    val payload: String // Ez maga az .m3u8 stream link
)

// Tartalék (offline) lista, ha a GitHub nem elérhető
object DefaultStations {
    val list = listOf(
        Station("m1", "M1", "Hírek és közélet", "📺", "https://c201-node61-cdn.connectmedia.hu/110301/e475b5226d338c8b2eb3b9d0e6bdd778/6af3619f/index.m3u8"),
        Station("m2", "M2", "Gyerek és ifjúsági", "🎈", "https://c402-node62-cdn.connectmedia.hu/110302/10cdfae4bad5ae9225e08db12b9f6a56/6af361ef/index.m3u8"),
        Station("m5", "M5", "Kultúra és oktatás", "🎭", "https://c402-node61-cdn.connectmedia.hu/110305/baab752ce1dabe13b3bc0fb9f2ad5ce1/6af3623d/index.m3u8")
    )
}
