# HitJam TV - Natieve Android TV Applicatie

HitJam TV is een 100% natieve Android TV / Google TV applicatie gebouwd met Kotlin en Jetpack Compose. De app is specifiek ontworpen voor gebruik op TV-sticks (zoals de Chromecast met Google TV) en maakt gebruik van de krachtige Media3 ExoPlayer-videomotor voor het vloeiend afspelen van live `.m3u8` (HLS) streams met hardwareversnelling.

## 🚀 Kenmerken
- **Afstandsbediening-vriendelijk (D-pad):** Naadloze navigatie met de pijltjestoetsen van je tv-afstandsbediening.
- **Live Preview:** Zenders starten direct in een klein venster zodra je erop gaat staan (fokus).
- **Full-screen Modus:** Druk op *Enter* om de stream volledig scherm te openen.
- **Terug-knop Integratie:** Druk op *Back* op je afstandsbediening om het volledige scherm te sluiten en terug te keren naar de zenderlijst.
- **Oranje Neon Stijl:** Een strakke, donkere interface met oplichtende oranje randen bij selectie.
- **Lichtgewicht Build:** Ontwikkeld in VS Code en compileerbaar via de opdrachtregel zonder zware IDE (Android Studio) vereisten.

---

## 📂 Projectstructuur & Schone Code

Deze repository bevat uitsluitend de pure broncode en configuratiebestanden. Alle door Gradle gegenereerde cache- en tijdelijke build-bestanden worden strikt gefilterd via `.gitignore`. 

De mappenstructuur ziet er als volgt uit:

```text
HitJamTV-Native/
├── build.gradle.kts            # Globale Gradle plug-in configuratie
├── settings.gradle.kts           # Project- en module-inclusie
├── gradle.properties             # RAM- en AndroidX-instellingen
├── .gitignore                    # Filters voor schone Git commits
└── app/
    ├── build.gradle.kts          # Module-specifieke dependencies (ExoPlayer, GSON)
    └── src/
        └── main/
            ├── AndroidManifest.xml   # TV-instellingen en internetrechten
            └── java/
                └── com/
                    └── borsosistvan/
                        └── hitjamtv/
                            ├── MainActivity.kt    # TV UI-logica en Full-screen navigatie
                            ├── Station.kt         # Datamodel voor de zenders
                            └── TvVideoPlayer.kt   # ExoPlayer videomotor integratie
```

---

## 🛠️ Vereisten & Installatiegids (Vanaf Nul in VS Code)

Volg deze stappen nauwkeurig om je computer in te richten voor het compileren van de APK.

### Stap 1: Java JDK 17 Installeren
De Kotlin compiler heeft Java 17 nodig. Download en installeer de **Java JDK 17** (bijvoorbeeld van Oracle of Eclipse Temurin).

### Stap 2: Android SDK Command-Line Tools downloaden
Omdat we geen Android Studio gebruiken, hebben we de losse command-line tools van Google nodig:
1. Download de **Command-line tools** onderaan de officiële Android Studio pagina (zoek naar *Command line tools only*).
2. Maak een map aan op je hardeschijf: `C:\Android\sdk\`.
3. Pak de zip uit zodat de `bin` en `lib` mappen exact hier staan: `C:\Android\sdk\cmdline-tools\bin\`.

### Stap 3: Gradle 8.7 Handmatig Installeren
Download en configureer de specifieke Gradle builder:
1. Download **Gradle 8.7 (Binary-only)** van de officiële Gradle website (`gradle-8.7-bin.zip`).
2. Maak een map aan: `C:\Gradle\`.
3. Pak de zip hierin uit, zodat de executable hier staat: `C:\Gradle\gradle-8.7\bin\gradle.bat`.

### Stap 4: Windows Omgevingsvariabelen (System Path) Instellen
Dit zorgt ervoor dat je computer de Android SDK, Java en Gradle overal herkent:
1. Open de Windows Start-knop, typ **Környezeti változók** (Omgevingsvariabelen) en open het menu.
2. Voeg onder **Rendszerváltozók** (Systeemvariabelen) de volgende twee nieuwe variabelen toe via de knop *Új* (Nieuw):
   - **Naam:** `ANDROID_HOME` | **Waarde:** `C:\Android\sdk`
   - **Naam:** `JAVA_HOME` | **Waarde:** `C:\Program Files\Java\jdk-17` *(Pas aan naar jouw exacte Java-map)*
3. Zoek in dezelfde lijst naar de variabele **`Path`** (vagy `PATH`), klik op *Szerkesztés* (Bewerken), voeg een nieuwe regel toe en plak dit pad erin:
   - `C:\Gradle\gradle-8.7\bin`
4. Klik op **OK** in alle vensters en **start VS Code opnieuw op** om de wijzigingen te activeren.

### Stap 5: Google Licenties Accepteren (Verplicht!)
Voordat je mag bouwen, moet je akkoord gaan met de voorwaarden van Google. Open een Windows Opdrachtprompt (`cmd`) en voer dit commando uit:

```cmd
c:\Android\sdk\cmdline-tools\bin\sdkmanager.bat --licenses --sdk_root=c:\Android\sdk
```
*Druk bij elke vraag op de letter **`y`** en daarna op **Enter** totdat de melding "All SDK package licenses accepted" verschijnt.*

---

## 🏗️ Hoe bouw je de APK (Klonen & Compileren)

Wanneer de computer is ingericht volgens de bovenstaande stappen, kun je de app met één druk op de knop compileren:

1. Kloon deze repository naar je computer:
   ```powershell
   git clone https://github.com
   cd HitJamTvAndroid
   ```
2. Initialiseer de lokale Gradle Wrapper:
   ```powershell
   gradle wrapper --gradle-version 8.7 --distribution-type bin
   ```
3. Start de compilatie van de Android TV app:
   ```powershell
   .\gradlew.bat clean assembleDebug
   ```

Na een succesvolle build (`BUILD SUCCESSFUL`), vind je de kant-en-klare TV app op deze locatie:
📁 **`app/build/outputs/apk/debug/app-debug.apk`**

---

## 📺 Installatie op de Google TV Stick

Aangezien je geen kabels nodig hebt, kun je de app distribueren via je eigen **HitJamStore** (GitHub Pages HTML-pagina). Open de browser op je Google TV (bijvoorbeeld *TV Bro*), surf naar je store, download de `hitjamtv.apk` en start de installatie!

