# Modern Tic-Tac-Toe Implementation Plan (Reto 3)

Adapt the "Reto 3" Tic-Tac-Toe requirement into a modern Android app using **Jetpack Compose**, **MVVM architecture**, and **Material 3**.

## User Review Required

> [!IMPORTANT]
> The implementation will focus on a clean, reactive UI and robust game logic. I'll include three difficulty levels for the AI (Easy, Medium, Hard/Minimax).

- **Difficulty Levels**:
    - **Easy**: Computer moves randomly.
    - **Medium**: Computer tries to win or block the human player.
    - **Hard**: Computer uses the Minimax algorithm for an unbeatable game.
- **State Management**: Using `ViewModel` with `MutableStateFlow` to handle game state across configuration changes (like orientation).

## Proposed Changes

### [Domain/Logic Layer]

Summary: Pure Kotlin logic for the game mechanics, win detection, and AI moves.

#### [NEW] [TicTacToeGame.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto1/app/src/main/java/com/example/reto1/logic/TicTacToeGame.kt)
- Define constants for board markers (X, O, Empty).
- Logic to check for a winner or a tie.
- AI logic for different difficulty levels.

### [UI/ViewModel Layer]

Summary: UI State and ViewModel to manage game flow and user interactions.

#### [NEW] [TicTacToeViewModel.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto1/app/src/main/java/com/example/reto1/ui/TicTacToeViewModel.kt)
- Expose `TicTacToeState` as a Flow.
- Handle human move events.
- Trigger AI moves with a small delay for better UX.
- Manage scores.

#### [NEW] [TicTacToeState.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto1/app/src/main/java/com/example/reto1/ui/TicTacToeState.kt)
- Data class to hold the board, current player, game status (Winner/Tie), and scores.

### [UI Components Layer]

Summary: Compose-based UI components for the game.

#### [NEW] [TicTacToeScreen.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto1/app/src/main/java/com/example/reto1/ui/TicTacToeScreen.kt)
- Main screen layout.
- `BoardComponent`: Renders the 3x3 grid.
- `ScoreBoard`: Displays scores and status messages.
- `GameMenu`: Options for Difficulty and Restart.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/secoe/Documents/Projects/MobileApps/DADM_Challenges/reto1/app/src/main/java/com/example/reto1/MainActivity.kt)
- Update to host the `TicTacToeScreen`.

## Verification Plan

### Automated Tests
- Unit tests for `TicTacToeGame` logic:
    - `checkWin()` for rows, columns, diagonals.
    - `getComputerMove()` for different difficulty levels.

### Manual Verification
- Deploy to an Android emulator/device.
- Verify game flow: Human move -> AI move -> Win/Tie detection.
- Verify score tracking.
- Test orientation changes to ensure state persistence via ViewModel.
