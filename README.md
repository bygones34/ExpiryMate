# ExpiryMate

**Track expiration dates. Get reminded. Waste less.**

ExpiryMate is a local-first Android app for tracking expiration dates of everyday products such as food, medicine, cosmetics, supplements, and household items.

It helps you see what is expiring soon, receive reminders before items expire, and keep a simple history of what you used or discarded.

---

## Features

- Track products and expiration dates
- See items that are expiring soon
- Receive local expiration reminders
- Choose reminder timing per item
- Mark items as **Used** or **Discarded**
- View item history
- Search and filter active items by category
- Edit or delete existing items
- Light, Dark, and System theme support
- Material You dynamic colors on supported Android versions
- Category-specific item icons
- First-launch welcome experience
- English and Turkish localization
- Privacy Policy accessible directly from Settings

---

## Screenshots

| Home | Items | Add Item |
|---|---|---|
| ![Home](docs/screenshots/01_home.png) | ![Items](docs/screenshots/02_items.png) | ![Add Item](docs/screenshots/03_add_item.png) |

| Item Details | History | Settings |
|---|---|---|
| ![Item Details](docs/screenshots/04_item_detail.png) | ![History](docs/screenshots/05_history.png) | ![Settings](docs/screenshots/06_settings.png) |

> Screenshot paths can be adjusted depending on how store assets are organized in the repository.

---

## How It Works

1. Add an item with its expiration date.
2. Choose a category and reminder timing.
3. ExpiryMate schedules a local reminder.
4. Check the Home screen to see what needs attention.
5. Mark the item as Used or Discarded when you're done with it.

No account or cloud service is required.

---

## Categories

ExpiryMate currently supports:

- Food
- Medicine
- Cosmetics
- Supplements
- Household
- Other

Each category is represented by its own visual icon throughout the app.

---

## Reminders

ExpiryMate uses Android local notifications and WorkManager to schedule expiration reminders.

The reminder time is calculated from the item's expiration date and selected reminder offset.

Examples:

- Same day
- 1 day before
- 3 days before
- 7 days before

Notifications are revalidated before delivery so deleted, completed, or ineligible items are not notified unnecessarily.

---

## Privacy

ExpiryMate is designed as a **local-first** application.

### Current V1 behavior

- No account required
- No backend
- No cloud sync
- No analytics
- No advertising SDKs
- No tracking
- No Firebase
- No developer-controlled data transmission

Item data and settings are stored locally on the device.

Android system backup may back up application data depending on the user's device and Google backup settings.

Privacy Policy:

**https://bygones34.github.io/ExpiryMate/privacy-policy.html**

---

## Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **Navigation Compose**
- **Room**
- **KSP**
- **DataStore Preferences**
- **WorkManager**
- **Flow / StateFlow**
- **Android Notifications**
- Manual dependency injection through `AppContainer`

### Android configuration

- `minSdk`: 26
- `targetSdk`: 37
- `compileSdk`: 37

---

## Architecture

ExpiryMate follows a simple layered Android architecture:

```text
UI / Compose
     ↓
ViewModels
     ↓
Repository Layer
     ↓
Room / DataStore / WorkManager
```

The project intentionally avoids unnecessary abstraction for the current scope.

There is currently no:

- Hilt
- Backend API
- Authentication layer
- Firebase
- Analytics SDK
- Ad SDK
- Cloud database

---

## Data & Persistence

### Room

Room stores expiration-tracking data such as:

- Product name
- Category
- Expiration date
- Reminder timing
- Notes
- Item status
- Completion metadata

Supported item states:

```text
ACTIVE
USED
DISCARDED
```

### DataStore

Preferences DataStore is used for app-level settings such as:

- Expiration reminder preference
- Default reminder timing
- Theme mode
- Dynamic color preference
- First-launch welcome state

---

## Localization

ExpiryMate currently supports:

- English
- Turkish

The app automatically follows the Android system locale.

There is no in-app language selector.

Unsupported locales currently fall back to English.

---

## Themes

ExpiryMate supports:

- System Default
- Light
- Dark

Dynamic Material You colors are available on supported Android versions.

The splash screen also provides dedicated light and dark background treatment.

---

## Build

Clone the repository:

```bash
git clone <repository-url>
cd ExpiryMate
```

Build the debug APK:

### Windows / PowerShell

```powershell
.\gradlew :app:assembleDebug
```

### macOS / Linux

```bash
./gradlew :app:assembleDebug
```

The debug APK is generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Run unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

---

## Project Status

ExpiryMate is currently being prepared for its first Google Play release.

Completed release-readiness work includes:

- Release build verification
- Room schema export
- Final launcher icon
- Dedicated notification small icon
- Light / dark splash treatment
- Privacy Policy
- Play Store listing draft
- Store screenshots
- Feature graphic
- English / Turkish localization
- Real-device notification testing

Remaining release work includes:

- Google Play Console setup
- Data Safety form
- Play App Signing
- Signed Android App Bundle
- Internal Testing
- Pre-launch report review
- Production release preparation

---

## Product Principles

ExpiryMate currently follows a few intentionally simple product decisions:

- Local-first
- No account requirement
- No cloud dependency
- Minimal permissions
- No ads or analytics in V1
- Simple reminder workflow
- Focused feature set

---

## Planned / Postponed Features

Potential future additions include:

- Barcode scanning
- OCR / camera support
- Export / import
- Cloud sync
- Family sharing
- Additional languages

These are intentionally outside the scope of the first release.

---

## Contributing

ExpiryMate is currently under active development.

If you find a bug or have a feature suggestion, feel free to open an issue.

---

## License

A license has not yet been specified.
