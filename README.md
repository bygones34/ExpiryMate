<div align="center">

<img src="docs/store-assets/expirymate_play_store_icon_512.png" width="120" alt="ExpiryMate App Icon">

# ExpiryMate

### Track expiration dates. Get reminded. Waste less.

A local-first Android app for keeping track of expiration dates across food, medicine, cosmetics, supplements, household products, and more.

<br>

![Android](https://img.shields.io/badge/Android-26%2B-3DDC84?logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)
![Room](https://img.shields.io/badge/Persistence-Room-1976D2)
![WorkManager](https://img.shields.io/badge/Reminders-WorkManager-0F9D58)
![Languages](https://img.shields.io/badge/Languages-English%20%7C%20Türkçe-blue)
![Privacy](https://img.shields.io/badge/Privacy-Local--First-success)

<br>

<img src="docs/store-assets/feature_graphic_1024x500.png" width="100%" alt="ExpiryMate Feature Graphic">

</div>

---

## About ExpiryMate

**ExpiryMate** helps you keep track of products before they expire.

Add an item, choose its expiration date and reminder timing, and let ExpiryMate keep the important dates organized for you.

No account. No cloud dependency. No ads. Your item data stays local to your device.

### Highlights

- 📅 Track expiration dates
- 🔔 Receive local reminders before items expire
- ⏳ Quickly see what is expiring soon
- 🗂️ Organize items by category
- ✅ Mark items as Used or Discarded
- 🕘 Review item history
- 🔎 Search and filter your active items
- 🌗 Light, Dark, and System themes
- 🎨 Material You dynamic colors on supported devices
- 🇬🇧 English and 🇹🇷 Turkish localization

---

## Screenshots

<div align="center">

| Home | Items | Add Item |
|:---:|:---:|:---:|
| <img src="docs/store-assets/screenshots/01_home.png" width="250"> | <img src="docs/store-assets/screenshots/02_items.png" width="250"> | <img src="docs/store-assets/screenshots/03_add_item.png" width="250"> |

| Item Details | History | Settings |
|:---:|:---:|:---:|
| <img src="docs/store-assets/screenshots/04_item_detail.png" width="250"> | <img src="docs/store-assets/screenshots/05_history.png" width="250"> | <img src="docs/store-assets/screenshots/06_settings.png" width="250"> |

</div>

---

## How It Works

1. **Add an item** with its expiration date.
2. **Choose a category** and reminder timing.
3. ExpiryMate schedules a **local notification**.
4. Check the Home screen to see what's expiring next.
5. Mark the item as **Used** or **Discarded** when you're done.

---

## Built With

| Area | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Local Database | Room |
| Preferences | DataStore |
| Background Work | WorkManager |
| Reactive State | Flow / StateFlow |
| Dependency Injection | Manual `AppContainer` |
| Notifications | Android Notifications |
| Code Generation | KSP |

### Android

```text
minSdk     26
targetSdk  37
compileSdk 37
```

---

## Architecture

ExpiryMate keeps its architecture intentionally straightforward:

```text
┌──────────────────────┐
│   Jetpack Compose    │
│         UI           │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│      ViewModels      │
│   Flow / StateFlow   │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   Repository Layer   │
└──────┬────────┬──────┘
       │        │
       ▼        ▼
    Room     DataStore
       │
       └──────────────► WorkManager / Notifications
```

The current V1 intentionally does **not** use:

- Hilt
- Firebase
- Backend APIs
- Authentication
- Analytics
- Advertising SDKs
- Cloud databases

---

## Privacy

ExpiryMate is designed as a **local-first** application.

Current V1 behavior:

- No account required
- No backend
- No cloud sync
- No analytics
- No ads
- No tracking
- No Firebase
- No developer-controlled data transmission

Item data and app preferences are stored locally on the device.

Android system backup may back up application data depending on device and Google backup settings.

### Privacy Policy

[Read the ExpiryMate Privacy Policy](https://bygones34.github.io/ExpiryMate/privacy-policy.html)

---

## Localization

ExpiryMate currently supports:

- 🇬🇧 English
- 🇹🇷 Turkish

The app automatically follows the Android system language.

Unsupported locales fall back to English.

---

## Build Locally

Clone the repository:

```bash
git clone <repository-url>
cd ExpiryMate
```

### Windows / PowerShell

```powershell
.\gradlew :app:assembleDebug
```

### macOS / Linux

```bash
./gradlew :app:assembleDebug
```

The generated debug APK can be found at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Run the unit tests:

```powershell
.\gradlew :app:testDebugUnitTest
```

---

## Project Status

ExpiryMate is currently being prepared for its **first Google Play release**.

### Completed

- ✅ Core expiration tracking flow
- ✅ Room persistence
- ✅ Local reminder notifications
- ✅ Item Detail / Edit / Delete
- ✅ Used / Discarded history
- ✅ Light / Dark / System themes
- ✅ Dynamic Colors
- ✅ Dedicated notification small icon
- ✅ Light / dark splash treatment
- ✅ Category-specific icons
- ✅ First-launch welcome experience
- ✅ English / Turkish localization
- ✅ Privacy Policy
- ✅ Play Store screenshots
- ✅ Feature graphic
- ✅ Real-device notification verification

### Release Work Remaining

- ⏳ Google Play Console setup
- ⏳ Data Safety declaration
- ⏳ Play App Signing
- ⏳ Signed Android App Bundle
- ⏳ Internal Testing
- ⏳ Pre-launch report
- ⏳ Production release

---

## Future Ideas

Features intentionally outside the first-release scope include:

- Barcode scanning
- OCR / camera support
- Export / import
- Cloud sync
- Accounts / family sharing
- Additional languages

---

## Contributing

ExpiryMate is currently under active development.

Bug reports and feature suggestions are welcome through GitHub Issues.

---

## License

A license has not yet been specified.