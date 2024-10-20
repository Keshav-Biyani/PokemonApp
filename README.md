# Pokémon App

## Overview

This Android app, built using Jetpack Compose, showcases a list of Pokémon and their details. The application supports both portrait and landscape orientations and follows the MVVM architecture pattern.

## Features

- **List of Pokémon**: Displays a grid of Pokémon names and images.
- **Detail Screen**: Shows detailed information for a selected Pokémon.
- **Orientation Support**: Adaptable UI for both portrait and landscape modes.
- **MVVM Architecture**: Utilizes Model-View-ViewModel architecture for separation of concerns.

## Architecture

### MVVM (Model-View-ViewModel)

- **Model**: Manages the data layer, including network operations and data storage.
- **ViewModel**: Acts as a bridge between the Model and the View, holding UI-related data and business logic.
- **View**: Composed using Jetpack Compose, presenting data to the user and handling user interactions.

## Data Endpoints

- **Pokémon List**: [https://pokeapi.co/api/v2/pokemon](https://pokeapi.co/api/v2/pokemon)
- **Pokémon Details**: [https://pokeapi.co/api/v2/pokemon/{id}](https://pokeapi.co/api/v2/pokemon/{id})

## Screenshots and Videos

- **App Overview**: Shows the main screen with a list of Pokémon.
  ![IMG-20240807-WA0010](https://github.com/user-attachments/assets/6f9c2a03-351e-48e7-94c0-d24a90405b8f)
- **Pokémon Detail Screen**: Demonstrates the detail view of a selected Pokémon.
  ![IMG-20240807-WA0004](https://github.com/user-attachments/assets/c3f24554-370e-4f96-acfe-5efaeb5e6968)

- **Vedio**:

https://github.com/user-attachments/assets/2221bf6e-60c7-44cc-91a3-1561f9ad02d4


  
- **Landscape Mode**: View of the app in landscape orientation.
  ![IMG-20240807-WA0005](https://github.com/user-attachments/assets/9223a01f-c926-4b44-a1b1-6cb8ba811993)
![IMG-20240807-WA0009](https://github.com/user-attachments/assets/98aa4ca0-fe2e-4db3-84aa-c7299b869ab9)
![IMG-20240807-WA0007](https://github.com/user-attachments/assets/33bf25d6-b2ff-4f04-bd81-6119614b40c8)


## Setup

1. **Clone the Repository**

   ```sh
   git clone https://github.com/your-repo/pokemon-app.git](https://github.com/Keshav-Biyani/PokemonApp.git
   ```

2. **Open in Android Studio**

   - Import the project in Android Studio.
   - Sync Gradle.

3. **Run the App**

   - Connect an Android device or start an emulator.
   - Build and run the app from Android Studio.

## Dependencies

- Jetpack Compose
- Retrofit
- Coroutines
- Glide / Coil for image loading



## Contact

For any questions or feedback, please reach out to [keshavbiyani23@gmail.com](mailto:keshavbiyani23@gmail.com).


