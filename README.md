
SpaceyInvaders: A Space Invaders Game
Created by Aaron Liao 90811748 and Yen Chun Liang 23647471 for EECS 40 Spring 2016

Compilation Instructions
- Import project to Android Studio
- Run on emulator or Android device with USB Debugging enabled

How to Play
- Upon launch, you will be greeted with a simple main menu. Your high score is shown here.
- Press the play button at the bottom of the screen to start the game.
- When in game, you will see a horizontal line just under the "Score" and "Level" indicators.
  - This line has multiple purposes:
    - When any of the Kevin Spaceys on screen touches this line, you lose.
    - Tap above this line to fire a bullet. (You can have up to 5 bullets on screen at once and can only shoot while stationary)
    - Tap below this line and on the left half of the screen to move your ship to the left. You can hold to continue moving.
    - Tap below this line and on the right half of the screen to move your ship to the right. You can hold to continue moving.
- Your goal is to shoot the oncoming invaders. (Kevin Spaceys)
  - The entire block of enemies moves side to side and down upon hitting the edges.
  - After killing all the enemies, bonus points will be awarded, the next level will begin and the enemies will advance more quickly.
  - Each enemy killed will net you points equal to the current level.
  - Occasionally, a UFO will move across at the top of the screen. Hitting it will net you points equal to 20 times the current level.

Game Features
- Scoring system and high score saving
- UFO as target
- Game over / Play again screen
- Pause screen (To be implemented)
- Multi-touch ship movement handling
- 5 concurrent bullets handling
