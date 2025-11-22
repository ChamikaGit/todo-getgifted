# Todo App - Android

A simple and clean Todo application built with Android using MVVM architecture. This app allows users to view and manage their todo items with offline support.

## Architecture

This project follows the **MVVM (Model-View-ViewModel)** architecture pattern, which provides a clean separation of concerns and makes the code more maintainable and testable.

## Project Structure

```
app/src/main/java/com/getgifted/todoapp/
├── data/
│   ├── local/          # Room database and DAOs
│   ├── model/          # Data models
│   ├── remote/         # API services
│   ├── repository/     # Repository pattern implementation
│   └── utils/          # Utility classes
├── di/                 # Dependency injection modules
└── ui/
    ├── adapter/        # RecyclerView adapters
    ├── fragments/      # UI fragments
    └── viewmodel/      # ViewModels
```

## Features

- ✅ View list of todos
- ✅ View todo details
- ✅ Offline support with local caching
- ✅ Pull to refresh
- ✅ Pagination support
- ✅ Splash screen
- ✅ Dark mode support

## Libraries Used

### Core Android
- **Kotlin** - Programming language
- **ViewBinding** - View access without findViewById

### Architecture Components
- **ViewModel** - Manage UI-related data lifecycle
- **LiveData** - Observable data holder
- **Room Database** - Local data persistence
- **Navigation Component** - Fragment navigation

### Dependency Injection
- **Hilt** - Dependency injection framework

### Networking
- **Retrofit** - REST API client
- **OkHttp** - HTTP client
- **Gson** - JSON serialization/deserialization

### Async Operations
- **Kotlin Coroutines** - Asynchronous programming
- **Flow** - Reactive data streams

### UI Components
- **RecyclerView** - List display
- **SwipeRefreshLayout** - Pull to refresh
- **Material Design Components** - UI components

## API

The app fetches todo data from: `https://jsonplaceholder.typicode.com/todos`

## Build

```bash
./gradlew build
```
