# Walkthrough - Reto 2 Implementation (with Custom Icons)

I have successfully implemented Reto 2, including the requested custom icons for the menu and the app.

## Changes Made

### 1. Project Duplication and Renaming
- Duplicated `reto1` directory to `reto2`.
- Updated `namespace` and `applicationId` to `com.example.reto2`.
- Global search and replace for all package references and theme names.

### 2. AI Difficulty Support
- Added `Difficulty` enum (Easy, Medium, Hard) to `TicTacToeGame.kt`.
- Implemented varied strategic logic for each difficulty level.

### 3. Options Menu and Dialogs
- Added an **Options Menu** (3-dots) to the `TopAppBar`.
- Implemented **New Game**, **AI Difficulty**, **About**, and **Quit** actions.
- Used Compose `AlertDialog` for all interactive prompts.

### 4. Custom Icons (Extra Challenge)
- **Menu Icons**: Created 4 custom vector drawables (`ic_menu_new_game.xml`, etc.) and integrated them into the dropdown menu using `leadingIcon`.
- **App Icon**: Created a custom `ic_launcher_foreground.xml` with a Tic-Tac-Toe grid design and updated the adaptive icon configuration.

## Verification Results
- Verified Kotlin logic via static analysis.
- Verified resource references in `TicTacToeScreen.kt`.
- Adaptive app icon is correctly configured in `mipmap-anydpi-v26`.

> [!TIP]
> The custom icons use Material Design paths to ensure they look professional and consistent with the Android system UI.
