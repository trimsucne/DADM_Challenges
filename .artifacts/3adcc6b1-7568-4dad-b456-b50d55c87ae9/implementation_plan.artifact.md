# Implementation Plan - Reto 2 (Incremented from Reto 1)

This plan covers the duplication of `reto1` into `reto2`, package renaming, and the implementation of Menus and Dialogs (Reto 4 requirements) adapted to modern Jetpack Compose and Material 3.

## User Review Required

> [!IMPORTANT]
> - **Package Renaming**: All occurrences of `com.example.reto1` will be updated to `com.example.reto2`.
> - **Compose Adaptation**: Traditional XML menus and `DialogFragment`s will be replaced with Compose `DropdownMenu` and `AlertDialog` for a modern, lifecycle-aware implementation.

## Proposed Changes

### 1. Project Duplication and Renaming (Cleanup)
Rename directories and update package strings across the project.

#### [MODIFY] [build.gradle.kts](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto2/app/build.gradle.kts)
- Update `namespace` and `applicationId` to `com.example.reto2`.

#### [MODIFY] All Kotlin Files
- Update package declarations and imports to use `com.example.reto2`.
- Rename `Reto1Theme` to `Reto2Theme`.

---

### 2. Logic Layer (Difficulty Support)
Add AI difficulty levels to the game logic.

#### [MODIFY] [TicTacToeGame.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto2/app/src/main/java/com/example/reto2/logic/TicTacToeGame.kt)
- Add `Difficulty` enum: `EASY`, `MEDIUM`, `HARD`.
- Update `getComputerMove` to accept difficulty:
    - `EASY`: Purely random.
    - `MEDIUM`: Blocks human win or random.
    - `HARD`: Best strategy (current logic).

---

### 3. UI Layer (Menus and Dialogs)
Implement the "Reto 4" requirements using Compose.

#### [MODIFY] [TicTacToeState.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto2/app/src/main/java/com/example/reto2/ui/TicTacToeState.kt)
- Add state for:
    - `showAboutDialog: Boolean`
    - `showDifficultyDialog: Boolean`
    - `showQuitDialog: Boolean`
    - `difficulty: Difficulty`

#### [MODIFY] [TicTacToeViewModel.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto2/app/src/main/java/com/example/reto2/ui/TicTacToeViewModel.kt)
- Add functions to toggle dialogs.
- Update `triggerComputerMove` to use the selected difficulty.

#### [MODIFY] [TicTacToeScreen.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto2/app/src/main/java/com/example/reto2/ui/TicTacToeScreen.kt)
- Add an actions icon (3-dots menu) to `TopAppBar`.
- Menu items: **New Game**, **AI Difficulty**, **About**, **Quit**.
- Implement `AlertDialog` components for:
    - **About**: Shows developer info.
    - **Difficulty**: Radio group to select AI level.
    - **Quit**: Confirmation before exiting.

## Verification Plan

### Manual Verification
- Verify that the app starts with the new package name.
- Open the menu and verify all items:
    - **New Game** resets the board.
    - **AI Difficulty** opens a dialog and changing it affects AI behavior.
    - **About** shows developer info.
    - **Quit** asks for confirmation and closes the app on "Yes".
