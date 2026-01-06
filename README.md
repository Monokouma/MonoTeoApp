# MonoTeo 🌦️

<p align="center">
  <img src="/composeApp/src/commonMain/composeResources/drawable/app_logo.png" width="120" alt="MonoTeo Icon"/>
</p>

<p align="center">
  <strong>La météo prend vie.</strong><br>
  Application météo cross-platform avec des fonds animés immersifs.
</p>

<p align="center">
  <a href="https://apps.apple.com/app/monoteo">
    <img src="https://img.shields.io/badge/App%20Store-Disponible-blue?style=flat&logo=apple" alt="App Store"/>
  </a>
  <a href="https://play.google.com/store/apps/details?id=com.despaircorp.monoteo">
    <img src="https://img.shields.io/badge/Play%20Store-Bientôt-green?style=flat&logo=googleplay" alt="Play Store"/>
  </a>
  <img src="https://img.shields.io/badge/Kotlin-2.3.0-7F52FF?style=flat&logo=kotlin" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Platform-Android%20%7C%20iOS-lightgrey?style=flat" alt="Platform"/>
</p>

---

## ✨ Fonctionnalités

- 🌍 **Météo en temps réel** basée sur votre position
- 🎨 **17 fonds animés** uniques créés avec Canvas
- 🌧️ Pluie, neige, orage, brouillard, tornade, et bien plus
- 🧭 **Mise à jour automatique** quand vous vous déplacez de 5km
- 🔒 **Respect de la vie privée** - aucune donnée stockée
- 📱 **100% Kotlin Multiplatform** - un seul code pour Android et iOS

---

## 🎬 Animations météo

| Condition | Animation |
|-----------|-----------|
| ☀️ Ensoleillé | Rayons de soleil, lens flares, particules flottantes |
| ☁️ Nuageux | Nuages multicouches avec dérive réaliste |
| 🌧️ Pluie | Gouttes, splashs, reflets |
| 🌨️ Neige | Flocons avec accumulation, vent |
| ⛈️ Orage | Éclairs procéduraux, pluie intense |
| 🌫️ Brouillard | Bancs de brume, gouttelettes |
| 🌪️ Tornade | Vortex, débris volants, éclairs |
| 🏜️ Tempête de sable | Particules, rafales de vent |
| Et plus... | Brume, fumée, cendres, grésil |

---

## 🏗️ Architecture
```
MonoTeoApp/
├── composeApp/
│   ├── commonMain/          # Code partagé
│   │   ├── data/            # Repositories, Services, DTOs
│   │   ├── domain/          # Use Cases, Entities
│   │   ├── ui/              # Compose UI, ViewModels
│   │   │   ├── background/  # 17 fonds animés Canvas
│   │   │   ├── weather/     # Écran météo
│   │   │   └── theme/       # Thème Material3
│   │   └── di/              # Modules Koin
│   ├── androidMain/         # Implémentations Android
│   └── iosMain/             # Implémentations iOS
└── iosApp/                  # Entry point iOS
```

**Clean Architecture** avec séparation stricte des couches :
- **Data** → Repositories, API calls, mappers
- **Domain** → Use cases, business logic
- **UI** → Compose screens, ViewModels

---

## 🛠️ Stack technique

| Catégorie | Technologie |
|-----------|-------------|
| **Framework** | Kotlin Multiplatform 2.3.0 |
| **UI** | Compose Multiplatform 1.9.3 |
| **DI** | Koin 4.1.1 |
| **Network** | Ktor 3.3.3 |
| **Async** | Coroutines + Flow |
| **Serialization** | Kotlinx Serialization |
| **Location** | FusedLocationProvider (Android) / CLLocationManager (iOS) |
| **Permissions** | Moko Permissions |
| **Images** | Coil 3 |

---

## 📊 Coverage
```bash
./gradlew koverHtmlReport
```

Rapport disponible dans `build/reports/kover/html/index.html`

---

## 📄 Licence
```
MIT License

Copyright (c) 2025 Flac Inc

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software...
```

---

## 🔗 Liens

- [Backend API](https://github.com/Monokouma/MonoTeo)

---

<p align="center">
  Made with ❤️ and Kotlin by <a href="https://github.com/Monokouma">Monokouma</a>
</p>
