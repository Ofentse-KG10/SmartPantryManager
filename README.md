# Smart Pantry Manager

## 1. Project Overview

Smart Pantry Manager is an Android mobile application developed for the Mobile App Development 700 assignment. The purpose of the application is to help users organise ingredients stored in their pantry and find recipes they can prepare using the ingredients available to them.

The application allows users to manage pantry ingredients, record quantities and units, store expiration dates, and receive recipe suggestions based on ingredient availability.

## 2. Main Features

- **Home Screen:** Displays a welcome message, a summary of saved pantry ingredients, and navigation options.
- **Add Ingredient:** Allows users to add ingredients by entering the ingredient name, quantity, unit, and optional expiration date.
- **View My Pantry:** Displays saved ingredients, quantities, units, and available expiration dates.
- **Edit Ingredients:** Allows users to update the information of existing ingredients.
- **Delete Ingredients:** Allows users to remove ingredients from their pantry.
- **Recipe Suggestions:** Displays recipes that can be prepared using the ingredients and quantities available in the pantry.
- **Strict Recipe Matching:** Checks whether all required ingredients and quantities are available before suggesting a recipe.
- **Local Database:** Uses SQLite to store pantry ingredients, recipes, recipe requirements, and settings.

## 3. Technologies Used

- Java for Android application development.
- Android Studio for development and testing.
- XML for designing the user interface layouts.
- SQLite for local database storage.
- Git and GitHub for source-code version control and project hosting.

## 4. Database Design

The application uses a local SQLite database named `smart_pantry.db`.

The database contains four tables:

1. **ingredients:** Stores ingredient IDs, names, quantities, units, and optional expiration dates.
2. **recipes:** Stores recipe IDs, names, and preparation instructions.
3. **recipe_ingredients:** Stores the ingredients and quantities required for each recipe.
4. **settings:** Stores application settings as key-value pairs.

The database helper class, `PantryDatabaseHelper`, creates and manages the database tables. It also supports adding, retrieving, updating, and deleting ingredients, as well as managing recipes and their required ingredients.

## 5. Recipe Matching

The application checks the ingredients available in the pantry against the requirements of each recipe. A recipe is suggested only when every required ingredient is available in a sufficient quantity and the ingredient units match after supported unit normalisation.

If an ingredient is missing or its available quantity is insufficient, the recipe is excluded from the suggestions. This helps users identify recipes they can prepare with their existing pantry ingredients.

## 6. Project Structure

The project contains the following important files and directories:

- `app/src/main/java/` — Contains the Java source files for the application.
- `app/src/main/res/layout/` — Contains XML layout files for the user interface.
- `app/src/main/res/values/` — Contains application resources such as strings, colours, and themes.
- `app/src/main/AndroidManifest.xml` — Contains the Android application configuration.
- Gradle configuration files — Define the Android project build configuration.
- `README.md` — Provides an overview of the application and instructions for using the project.

The exact files and package structure may vary depending on the final project configuration.

## 7. How to Run the Application

1. Install Android Studio on a computer.
2. Open Android Studio.
3. Select **Open** and choose the `SmartPantryManager` project folder.
4. Allow Gradle to synchronise and complete any required project setup.
5. Start an Android emulator or connect a compatible Android device with USB debugging enabled.
6. Select the application run configuration.
7. Click **Run** to build and launch the application.
8. Use the Home screen to navigate to the ingredient management and recipe suggestion features.

The SQLite database is created and managed by the application when it runs.

## 8. Testing

The application was tested to check that its main screens and functions operate correctly. Testing covered pantry ingredient management, database storage and retrieval, and recipe suggestions based on available ingredients and quantities.

The tests also considered missing ingredients and insufficient quantities to confirm that recipes are not suggested when their requirements are not met.

## 9. GitHub Repository

The source code for Smart Pantry Manager is available on GitHub:

https://github.com/Ofentse-KG10/SmartPantryManager

The repository provides access to the project source code and its version-control history.

## 10. Conclusion

Smart Pantry Manager provides a simple way for users to organise pantry ingredients and identify suitable recipes. By combining an Android user interface with SQLite database storage and strict recipe matching, the application supports ingredient management and recipe suggestions in one mobile application.
