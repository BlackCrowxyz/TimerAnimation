# Analog Clock & Timer

<!-- <h1>Screenshots</h1>

<div style="display:flex; gap:20px; align-items:flex-start;">
  <div style="text-align:center;">
    <p>Main activity:</p>
    <img src="https://github.com/BlackCrowxyz/TimerAnimation/blob/main/Screenshots/1.png" style="width:120px;" />
  </div>

  <div style="text-align:center;">
    <p>Clock activity:</p>
    <img src="https://github.com/BlackCrowxyz/TimerAnimation/blob/main/Screenshots/2.png" style="width:120px;" />
  </div>

  <div style="text-align:center;">
    <p>Timer activity:</p>
    <img src="https://github.com/BlackCrowxyz/TimerAnimation/blob/main/Screenshots/3.png" style="width:120px;" />
  </div>
</div> -->

## Screenshots

| Main activity | Clock activity | Timer activity |
|--------------|----------------|----------------|
| ![](https://github.com/BlackCrowxyz/TimerAnimation/blob/main/Screenshots/1.png) | ![](https://github.com/BlackCrowxyz/TimerAnimation/blob/main/Screenshots/2.png) | ![](https://github.com/BlackCrowxyz/TimerAnimation/blob/main/Screenshots/3.png) |


## Overview
This Android app showcases a custom SurfaceView-driven analog clock alongside an interactive countdown timer. A launcher menu routes to either experience and exposes a settings screen where users can personalize clock hand and marker colors. Preferences persist between sessions, demonstrating shared preference storage and retrieval.

## Features
- **Analog clock activity**
  - Custom `SurfaceView` drawing hour numbers, tick marks, and four animated hands (hour, minute, second, millisecond) in a square face
  - Dedicated rendering thread for smooth 60 fps updates and proper lifecycle cleanup
  - Colors driven by user preferences with instant updates
- **Timer activity**
  - NumberPickers for minutes/seconds, start/pause/reset controls, and sub-second display
  - Timer runs via a shared manager so countdowns continue even after leaving the screen; completion fires a toast “alarm”
- **Navigation & settings**
  - Main menu with Material buttons to open each activity
  - Toolbar menu entry into the preferences screen
  - ListPreference-based color pickers stored via `SharedPreferences`

## Project Structure
- `MainActivity` – menu/navigation hub with toolbar menu for preferences
- `ClockActivity` – hosts `AnalogClockSurfaceView` responsible for rendering
- `TimerActivity` – UI/controller for the countdown timer
- `SettingsActivity` – wraps a `PreferenceFragmentCompat`
- `ColorPreferences` – utility for parsing and validating stored color values
- Resources for layouts and preference XML live under `app/src/main/res`

## Technical Notes
- **Minimum SDK:** 28
- **Target/Compile SDK:** 34
- **Tested AVD:** Pixel 6 (Android 14 / API 34)

## Known Limitations
- The app focuses on in-app visuals.
- Preferences offer curated color presets rather than free-form pickers.

## How to Run
1. Open the project in the latest stable Android Studio.
2. Sync Gradle; ensure the Pixel 6 API 34 emulator (or equivalent physical device) is available.
3. Build & run via `MainActivity`.
4. Use the home screen buttons to open the clock or timer. Access preferences from the toolbar menu to customize colors.