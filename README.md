# Weather MVI

A sample Android weather app using MVI architecture, Coroutines/Flow, Hilt, Room, and Retrofit.

## Purpose

This project exists to practice and reinforce core Android architecture like MVI and library concepts.

**What the app does:**
1. User selects a city from a predefined list of 10 cities.
2. App requests weather for that city.
3. Shows a loading state.
4. Shows weather data on success.
5. Shows an error state on failure, with a retry action.
6. Maintains a small recent-searches list.
7. Caches results locally: checks cache first (fresh → use cached; stale/missing → hit network and cache the result); falls back to stale cache on network failure if available.
8. Includes a fake/forced-state repository for testing — able to force Success, Error, and Slow-response scenarios independent of the real network. (Empty was deliberately skipped: it has no natural mapping onto this app's `Result<Weather>` contract, unlike a list-based feature.)

**Architectural focus:**
- **Single, unified, immutable `StateFlow<WeatherUiState>`** exposed by the ViewModel — intentional, to feel the tradeoff MVI has vs. MVVM's multiple independent observable properties. All UI-relevant state (loading, weather, error, recent searches) lives inside one state hierarchy, updated atomically, rather than several independent fields that can drift out of sync.
- **Sealed `WeatherIntent`** class representing all possible user actions — the View dispatches intents, the ViewModel interprets them and produces new state, rather than the View calling named ViewModel methods directly.
- **One-time events** (e.g. a Snackbar on API failure) modeled separately from persistent state via a `Channel<T>` → `Flow` (consume-once semantics built in), collected once by the View — the Coroutines/Flow equivalent of MVVM's `Event<T>` + `LiveData` wrapper. Persistent error *state* (retry UI) is kept separate from the transient *event* (Snackbar).

This app has a sibling MVVM project exploring the same domain with RxJava, `LiveData`, and multiple independent observable properties — XML/ViewBinding, manual Dagger, RxJava, and MVVM are intentionally out of scope here.

## Tech Stack

| Concern | Choice |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (not XML/ViewBinding) |
| Architecture | MVI |
| Async / Reactive | Kotlin Coroutines + Flow (`suspend`, `Flow`, `StateFlow`, `Channel`) |
| State exposure | `StateFlow<WeatherUiState>` (single source of truth), collected via `collectAsStateWithLifecycle()` |
| Dependency Injection | Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`) |
| Networking | Retrofit, with suspend-function API methods (no RxJava adapter) |
| Weather API | [Open-Meteo](https://api.open-meteo.com/v1/forecast) — free, no API key required |
| City input | Predefined list of 10 cities (name + lat/lon), matching the MVVM app's list; no free-text search or geocoding |
| Local cache | Room, with suspend/Flow-typed DAO methods |
| Navigation | Jetpack Navigation for Compose (`NavHost`/`NavController`) — Main + Settings screens |
| App settings | Preferences DataStore, wrapped by a `SettingsRepository` (same abstraction pattern as `WeatherRepository` over Room/Retrofit) — stores °C/°F toggle |
| Testing | JUnit + MockK; Turbine for `StateFlow`/`Flow`/`Channel` assertions; Room in-memory DB for DAO integration tests |

## Project Structure

```
com.dmariani.weathermvi
│
├── data
│   ├── local
│   │   ├── WeatherDao.kt
│   │   ├── WeatherEntity.kt
│   │   └── WeatherDatabase.kt
│   │
│   ├── remote
│   │   ├── WeatherApi.kt
│   │   └── WeatherResponse.kt
│   │
│   ├── repository
│   │   ├── WeatherRepositoryImpl.kt
│   │   └── SettingsRepositoryImpl.kt   // wraps Preferences DataStore
│   │
│   └── Mappers.kt
│
├── domain
│   ├── model
│   │   ├── Weather.kt
│   │   ├── City.kt
│   │   └── Cities.kt
│   │
│   └── repository
│       ├── WeatherRepository.kt
│       └── SettingsRepository.kt
│
├── di
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   ├── SettingsModule.kt          // provides DataStore<Preferences>
│   └── RepositoryModule.kt        // @Binds for both repositories
│   // no manual AppComponent/ViewModelModule/ViewModelFactory/ViewModelKey —
│   // Hilt's @HiltViewModel + @AndroidEntryPoint generate this wiring
│
├── ui
│   ├── main
│   │   ├── MainActivity.kt        // @AndroidEntryPoint, hosts NavHost (Main/Settings routes)
│   │   ├── MainScreen.kt          // MainScreen, MainContent, CityPicker, Loader,
│   │   │                          // WeatherView, ErrorView + @Preview functions
│   │   ├── WeatherViewModel.kt    // @HiltViewModel — combines WeatherRepository +
│   │   │                          // SettingsRepository for °C/°F display conversion
│   │   ├── WeatherUiState.kt      // WeatherUiState + sealed WeatherContentState
│   │   └── WeatherIntent.kt       // sealed class of user actions
│   │
│   ├── settings
│   │   ├── SettingsScreen.kt      // SettingsScreen, SettingsContent, TemperatureUnitToggle
│   │   ├── SettingsViewModel.kt   // @HiltViewModel
│   │   ├── SettingsUiState.kt
│   │   └── SettingsIntent.kt
│   │
│   └── theme/                     // Compose theming (Color.kt, Type.kt, Theme.kt)
│
├── util
│   └── TemperatureConverter.kt    // celsiusToFahrenheit — pure, unit-tested
│
└── WeatherApp.kt                  // @HiltAndroidApp
```

**Test source set** (`src/test`, mirrors the structure above):
```
com.dmariani.weathermvi
│
├── data
│   ├── MappersTest.kt
│   ├── WeatherCodeMappingTest.kt      // parameterized, full WMO bucket coverage
│   └── repository
│       ├── FakeWeatherRepository.kt   // forcedWeather / forcedError / delayMs
│       └── FakeSettingsRepository.kt
│
├── util
│   └── TemperatureConverterTest.kt
│
└── ui
    ├── main
    │   └── WeatherViewModelTest.kt    // StandardTestDispatcher, Turbine, fakes
    └── settings
        └── SettingsViewModelTest.kt
```

## Build Sequence

1. Gradle setup — dependencies added per-layer, as each step actually needs them (Kotlin support via AGP built-in Kotlin, core-ktx, lifecycle-runtime-ktx, Coroutines core/android as the baseline; Retrofit/Room/Hilt/Compose added later at their respective steps)
2. Domain models + repository interface
3. Remote layer (`WeatherApi`, `WeatherResponse`)
4. Local layer (Room: `WeatherEntity`, `WeatherDao`, `WeatherDatabase`)
5. Mappers + `WeatherRepositoryImpl` (cache orchestration)
6. Hilt wiring (`WeatherMviApp`, `NetworkModule`, `DatabaseModule`, `RepositoryModule`)
7. UI state & intent design (`WeatherUiState`, `WeatherIntent`)
8. `WeatherViewModel` (`StateFlow`, `Channel`→`Flow` one-time events)
9. UI (`MainScreen`, `MainActivity`, Compose theming)
10. Settings screen — Jetpack Compose Navigation, Preferences DataStore, `SettingsRepository` (abstracting over DataStore, same pattern as `WeatherRepository` over Room/Retrofit), `SettingsUiState`/`SettingsViewModel`; `WeatherViewModel` combines weather state with the unit preference for °C/°F display conversion
11. Fake/forced-state repository
12. Tests, layer by layer (JUnit, MockK, Turbine, Room in-memory DB) — covering Settings/DataStore as well as Weather