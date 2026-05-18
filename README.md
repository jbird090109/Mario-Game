# Super Mario World (Java)

A Super Mario World-style platformer with SNES-resolution gameplay, Yoshi's Island 1 (World 1-1) level layout, SMW physics, Goombas, Koopas, question blocks, and power-ups. Uses built-in procedural SMW-style graphics (no external assets required).

## Project Structure
```
Mario-Game/
├── src/                   # Java source files
├── bin/                   # Compiled Java files
├── assets/                # Images, sounds, and other assets
├── README.md              # This file
└── .gitignore             # Git ignore file
```

## Getting Started

### Prerequisites
- Java JDK 11 or higher (JDK 14 is installed on this PC at `C:\Program Files\Java\jdk-14.0.1`)

### Easy way (Windows)
Double-click **`compile.bat`**, then **`run.bat`**.

### Manual compile (if `javac` is not in PATH)
Use the full path to your JDK:
```bat
"C:\Program Files\Java\jdk-14.0.1\bin\javac.exe" -d bin src\*.java
"C:\Program Files\Java\jdk-14.0.1\bin\java.exe" -cp bin Main
```

### Add Java to PATH (optional)
1. Press Win, search **Environment Variables**
2. Under **System variables**, edit **Path** → **New**
3. Add: `C:\Program Files\Java\jdk-14.0.1\bin`
4. Open a **new** terminal, then `javac` and `java` will work everywhere

### Controls
- **Arrow keys / A D**: Move
- **Z / Shift**: Run (build speed meter for sprint)
- **Space**: Jump (hold for higher jumps)
- **P**: Pause | **R**: Restart

## License
MIT License
