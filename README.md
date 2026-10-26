Smart Pantry Manager

1. Project Description
Smart Pantry Manager is a Java Android application developed for the Mobile App Development 700 practical assignment.
The purpose of this application is to help users manage the ingredients they have at home and discover recipes they can prepare using the ingredients already available in their pantry. The application aims to help users reduce food waste by making better use of leftover ingredients.
Users can save ingredients with their names, quantities, units and optional expiry dates. They can view, add, edit and delete pantry items. The application also includes a recipe suggestion feature that checks the ingredients and quantities available in the pantry before recommending a recipe.

2. Main Features
- Add ingredients to the pantry.
- View saved ingredients in a pantry list.
- Edit existing ingredients.
- Delete ingredients that are no longer needed.
- Save ingredient names, quantities, units and optional expiry dates.
- Store information locally using an SQLite database.
- Display suggested recipes based on the ingredients available in the pantry.
- Apply strict recipe matching so that recipes requiring missing ingredients or insufficient quantities are excluded.
- Display a message when no recipes match the current pantry.
- View recipe ingredients and preparation instructions.
- Manage the expiry-soon alert preference through the Settings screen.
- Display an expiry reminder for ingredients approaching their expiry dates.

3. Technologies Used
The application was developed using the following technologies:
- Java for application logic.
- Android Studio as the development environment.
- XML for Android screen layouts.
- SQLite for local data storage.
- SQLiteOpenHelper for database creation and management.
- Android Activities and Intents for screen navigation.
- ListView and a custom BaseAdapter for displaying pantry ingredients.
- Git and GitHub for version control.

4. Database Choice
I chose SQLite because Smart Pantry Manager stores structured information such as ingredients, recipes, recipe ingredients and application settings.
SQLite stores the information locally on the device. This makes it suitable for an application that does not require a separate online server or cloud database to manage its main features.
The database helper used in my project is called PantryDatabaseHelper. The database is named smart_pantry.db.
The database contains tables for ingredients, recipes, recipe ingredients and settings. The database helper manages the creation of these tables and provides methods for working with the saved information. The recipe collection is seeded when the database is first created. 


5. How to Open and Run the Application
- Download or clone this repository to your computer.
- Open Android Studio.
- Select Open.
- Choose the SmartPantryManager project folder.
- Allow Android Studio to complete the Gradle synchronisation process.
- If Android Studio requests a required SDK component, follow the installation instructions.
- Start an Android emulator or connect a compatible Android device.
- Select the application run configuration.
- Press Run to build and launch the application.
The project should be opened using the main project folder containing the app folder and project-level Gradle files.

6. Project Structure
The Android application source code is stored under the app/src/main directory.
Important classes in the project include:
- MainActivity.java: Displays the home dashboard and provides navigation to other screens.
- PantryDatabaseHelper.java: Manages the SQLite database, tables and database operations.
- PantryListActivity.java: Displays the ingredients saved in the pantry.
- AddIngredientActivity.java: Provides the form used to add or edit an ingredient.
- SuggestedRecipesActivity.java: Displays recipes that match the current pantry.
- SettingsActivity.java: Provides the application's settings.

7. Testing
During development, I tested the pantry management functions and the strict recipe-matching behavior.
The strict-matching tests included:
-Checking that a recipe appears when all required ingredients and quantities are available.
-Checking that a recipe disappears when a required ingredient is missing.
-Checking that a recipe disappears when the available quantity is insufficient.
The pantry management tests included creating, viewing, updating and deleting ingredients. I also checked that saved pantry information remained available after closing and reopening the application.

8. GitHub Repository
Repository link:
https://github.com/Ofentse-KG10/SmartPantryManager 
The repository is intended to show the source code and development history of Smart Pantry Manager. 

9. Student Information
Student Name: Ofentse Kgopotse 
Student Number: 402412861
Module: Mobile App Development 700


