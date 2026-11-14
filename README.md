# Khabar — News Application

An Android news app (XML layouts) with a splash screen, Firebase login/signup,
and a news landing page showing top headlines by country.

## Features

- Animated splash screen, then an auth check that routes to login or the news
  feed
- Sign up / log in with Firebase Auth (Google Sign-In supported)
- Top headlines list, fetched per country from a news API
- News list UI with images (Glide)

## Tech stack

- Kotlin, XML layouts with ViewBinding
- Hilt — dependency injection
- Retrofit + Gson/Moshi, OkHttp — networking
- Firebase Auth + Firestore, Google Sign-In
- Navigation Component, Lifecycle ViewModel, coroutines
- Glide, Coil — image loading
- Min SDK 29, target SDK 36

## Project structure

```
app/src/main/java/com/example/khabar/
├── Application.kt         # Hilt application class
├── MainActivity.kt        # Splash screen + auth routing
├── data/
│   ├── api/
│   │   └── ApiServices.kt # Top-headlines endpoint
│   ├── datasource/
│   ├── model/             # News response models
│   └── repository/        # Auth + news repositories
├── di/                    # Hilt modules
├── ui/
│   ├── NewsScreens/
│   │   ├── NewsLandingPage.kt
│   │   ├── newsAdapter.kt
│   │   └── viewmodel/
│   ├── auth/              # AuthActivity, SignupActivity, AuthViewModel
│   ├── components/
│   └── theme/
└── utils/
```

## How to build and run

1. Open the project in Android Studio.
2. Let Gradle sync, then run the `app` configuration on an emulator or a
   physical device (Android 10 / API 29 or higher).
3. Firebase features (login) need the project's `google-services.json`
   configured, and the app needs an internet connection to fetch headlines.

## Notes

- A learning project built to practise Hilt, Retrofit, Firebase Auth, and
  MVVM in a multi-screen Android app.
