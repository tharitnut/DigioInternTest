# XO Game (Kotlin Jetpack Compose)

Tic-Tac-Toe project with a customizable board size (**from 3×3 and larger**)  
Game history is stored with **Room Database** and also can watch replays.

---

## 🚀 Features
- Choose board size: 3×3, 4×4, 5×5 ... (set from Home screen)
- **2-player mode**
- Save **Game Session** + **Moves** into Room
- **Replay screen** to review each move step by step
- UI built with **Jetpack Compose (Material 3)** + **Navigation**

---

## ⚙️ Setup

### Requirements
- **Android Studio Ladybug+** (or newer)
- **JDK 17**
- **Android SDK Platform 36**
- Emulator/Device running **Android 8.0 (API 26)+**

### Steps

    git clone https://github.com/tharitnut/DigioInternTest.git xo-game
    cd xo-game

Open in Android Studio → Sync Gradle → Run on Emulator/Device.

---

## ▶️ Run

- Select board size in HomeScreen
- Play game in GameScreen (Use Back button to go back to HomeScreen)
- Watch replay in ReplayScreen

---

## 📂 Project Structure
- `ui/` → UI screens (**HomeScreen**, **GameScreen**, **ReplayScreen**)
- `viewmodel/` → State & logic for each screen (**HomeViewModel**, **GameViewModel**, **ReplayViewModel**)
- `data/` → Database (Room **entities**, **dao**, **db**, **repo**)
- `logic/` → Game algorithms (**GameEngine** checks win condition 3 or 4-in-a-row)
- `MainActivity.kt` → App entry point + Navigation


---

## 🧠 Logic & Algorithm
- Win condition
  - Board 3×3 → 3-in-a-row
  - Board larger than 3×3 → 4-in-a-row (can be adjusted in `GameViewModel`)
- Algorithm (Checks for winning lines in 4 directions):
  - Horizontal
  - Vertical
  - Diagonal down `\`
  - Diagonal up `/`
- For each cell:
  - If cell has X or O, check next K-1 cells in each direction
  - If all match → declare winner
  - If no winner and board is full → Draw