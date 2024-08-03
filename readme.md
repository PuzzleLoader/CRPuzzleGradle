# Cosmic Puzzle Gradle
A Gradle plugin aiming to make the life of Cosmic Reach with PuzzleLoader modding easier

This project was based off of [Cosmic Loom](https://codeberg.org/CRModders/cosmic-loom) (Thanks CoolGI) and was designed to have the tools you need Built-In.

Using this plugin in your projects

in your settings.gradle do
```groovy
buildscript {
    repositories {
        maven {
            name "JitPack"
            url "https://jitpack.io"
        }
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath "com.github.johnrengelman:shadow:8.1.1"
        classpath "com.github.PuzzleLoader:CRPuzzleGradle:$puzzle_gradle_version"
    }
}
```

at the top of your build.gradle do
```groovy
plugins {
    id "java"
    id "cr_puzzle_gradle"
    id "com.github.johnrengelman.shadow"
}
```
