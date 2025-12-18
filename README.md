# 🎵 Alaap - Modern Android Music Player


A feature-rich music player for Android that seamlessly blends local and WhatsApp audio playback with online music discovery, built with Kotlin and Jetpack Compose.

## ✨ Features

### 🎧 Multi-Source Audio Playback
- Local storage songs
- WhatsApp audio integration
- Downloaded/recorded audio files
- Background playback with notification controls

### 🔍 Smart Music Discovery
- Last.fm API integration for:
  - Track metadata (album art, artist info)
  - Similar track recommendations
  - Dynamic music search

### 🔒 User Management
- Firebase authentication (Email/Google)
- Cloud backup/restore for playlists
- Sync across devices

### 🎛️ Enhanced Playback
- ExoPlayer with equalizer support
- Playlist management
- Room Database caching for:
  - Songs metadata
  - User moods/playlists
  - Recently played tracks

## 🛠️ Tech Stack

| Category          | Technologies Used                          |
|-------------------|-------------------------------------------|
| Language          | Kotlin                                    |
| UI Framework      | Jetpack Compose + Material 3              |
| Audio Engine      | ExoPlayer + Android MediaSession          |
| Local Database    | Room + DataStore                          |
| Networking        | Retrofit + Kotlin Coroutines              |
| DI                | Dagger Hilt                               |
| Image Loading     | Coil                                      |
| Authentication    | Firebase Auth                             |
| Cloud Sync        | Firebase Firestore                        |
| Music Metadata    | Last.fm API                               |

## 📸 Screenshots

<p align="center"><img src="https://github.com/user-attachments/assets/f0693caf-1a53-4be4-9c19-8ec4b86c68a7" width="250"/><img src="https://github.com/user-attachments/assets/39ed9142-7d78-4bd1-bd31-5bf108a88b2e" width="250"/>
<img src="https://github.com/user-attachments/assets/e4ea4bbd-9d16-40e4-b756-85bbe844cf22" width="250"/><img src="https://github.com/user-attachments/assets/63a5ed3d-4371-4582-93c1-f94e4601ce2b" width="250"/>
<img src="https://github.com/user-attachments/assets/ea8bebd0-9d56-446e-8e8d-b6c935632650" width="250"/><img src="https://github.com/user-attachments/assets/e04caec6-7793-4a18-ad38-4f3b8997a0a4" width="250"/>
<img src="https://github.com/user-attachments/assets/4965d81c-8660-49f8-b97e-645e7e4628d9" width="250"/></p>
  







## 🚀 Getting Started

### Prerequisites
- Android Studio Flamingo or later
- Firebase project setup
- Last.fm API key

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/alaap.git
