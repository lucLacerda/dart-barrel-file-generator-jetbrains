# Dart Barrel Generator for JetBrains IDEs

Plugin for JetBrains IDEs (IntelliJ IDEA, WebStorm, Android Studio) that generates barrel files for Dart and Flutter projects.

## What are barrel files?

In Dart (and other languages), a barrel file is a file that re-exports several other files from a directory, allowing you to import multiple files through a single import. This helps organize and simplify your code.

## Features

- Generate barrel file for the selected folder
- Generate barrel files recursively for the folder and its subfolders
- Generate barrel file including all files from subfolders

## Installation

1. Open your JetBrains IDE (IntelliJ IDEA, WebStorm, Android Studio)
2. Go to `Settings/Preferences > Plugins > Marketplace`
3. Search for "Dart Barrel Generator"
4. Click "Install"
5. Restart the IDE when prompted

## How to use
1. Right-click on a folder in the Project Explorer
2. Select "Dart Barrel Generator" from the context menu
3. Choose one of the options:
   - **Generate Barrel for This Folder**: Creates a barrel file only for the selected folder
   - **Generate Barrel Recursive**: Creates barrel files for the selected folder and all its subfolders
   - **Generate Barrel with Subfolders**: Creates a barrel file including all files from subfolders

## Settings
Access the plugin settings in `Settings/Preferences > Tools > Dart Barrel Generator`.

### Available options:

- **Default barrel file name**: Sets a custom name for barrel files (by default, uses the folder name)
- **Prompt for file name**: Asks for the barrel file name before creating it
- **Skip empty folders**: Does not create barrel files for folders without Dart files
- **Exclude .freezed.dart files**: Does not include .freezed.dart files in barrel files
- **Exclude .g.dart files (generated)**: Does not include .g.dart files in barrel files
- **Add folder name at the beginning**: Adds the folder name at the beginning of exports
- **Add folder name at the end**: Adds the folder name at the end of exports
- **Use package: format for exports in the lib folder**: Uses the package: format for exports in the lib folder

## Development

This plugin was developed in Kotlin for JetBrains IDEs, based on the functionality of the VSCode plugin "Dart Barrel File Generator".

### Development requirements:

- IntelliJ IDEA (Community or Ultimate)
- Plugin DevKit installed
- JDK 17 or higher
- Gradle

### Building:

```bash
./gradlew build
```

### Running in development mode:

```bash
./gradlew runIde
```

## Credits

Based on the VSCode plugin [Dart Barrel File Generator](https://github.com/mikededo/dart-barrel-file-generator) by Miquel de Domingo i Giralt.
