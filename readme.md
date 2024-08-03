# Cosmic Puzzle Gradle
A Gradle plugin aiming to make the life of Cosmic Reach with PuzzleLoader modding easier

This project was based off of [Cosmic Loom](https://codeberg.org/CRModders/cosmic-loom) (Thanks CoolGI) and was designed to have the tools you need Built-In.

Using this plugin in your projects
```groovy
apply plugin: 'java'
apply plugin: 'CRPuzzleGradle'

buildscript() {
	repositories {
		maven {
			url 'https://jitpack.io'
		}
	}
	dependencies {
		classpath 'com.github.PuzzleLoader:CRPuzzleGradle:{version}'
	}
}
```

