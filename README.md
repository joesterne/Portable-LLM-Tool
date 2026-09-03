# LLM Controller Dashboard

A modern, native Android application designed as an LLM Controller and Prompt Manager. Built with Kotlin, Jetpack Compose, and Material 3, it offers a secure, offline-first approach to tracking, visualizing, and managing interactions with Large Language Models (like Gemini).

## Features

* **LLM Dashboard & Terminal:** Directly interact with LLM APIs in a terminal-themed UI (`TerminalDarker`, `NeonBlue`, `NeonPurple`).
* **Prompt Manager:** Save, edit, and organize your most frequently used prompts locally via Room Database. One-tap to load a saved prompt into the execution terminal.
* **Activity Logs & Offline History:** All requests (commands and responses) are logged locally using Room, allowing you to browse past sessions safely offline.
* **Visual Analytics:** View real-time statistics of your prompt history with native charts (powered by Vico):
  * **Success/Error Rate:** Track the health and success metrics of your model interactions.
  * **Token Usage Trends:** Visualize estimated token consumption over time to optimize request sizes.
* **Native CSV Export:** Export your complete interaction history as a CSV file using Android's native FileProvider and Share Sheet (email, save locally, or share to other devices).
* **Secure Architecture:** Adheres to Android best practices, running heavy I/O operations (like database interactions and file exports) safely on background Coroutine dispatchers (`Dispatchers.IO`) to guarantee smooth UI performance.

## Tech Stack

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (Material 3)
* **Local Database:** Room Database & Flow
* **Networking:** Retrofit & OkHttp
* **Visualizations:** Vico Charting Library
* **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines

## Setup & Configuration

1. Clone or import the repository.
2. In AI Studio Build, configure your secrets by setting the `GEMINI_API_KEY` (and Firebase details if applicable) in the environment settings.
3. Build and run using the provided Gradle toolchain.

## Design Philosophy

The application is styled as a hacker/terminal dashboard to emphasize its utility as an admin controller for AI models. It prioritizes data privacy by keeping prompt libraries and history offline-first in SQLite until explicitly exported.
