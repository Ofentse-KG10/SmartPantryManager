Smart Pantry Manager
1. Project Overview

Smart Pantry Manager is an Android application I developed for my Mobile App Development 700 practical assignment. The main idea behind the app is to help users keep track of ingredients they have at home and find recipes they can make with those ingredients.

The app has a home screen where users can navigate to the ingredient form, view their pantry and open the suggested recipes screen.

2. Project Objectives

The objectives of this project are to:

Develop an Android application using Java and XML.
Allow users to add and manage ingredients in their pantry.
Store ingredient information in a local database.
Display saved ingredients in a pantry list.
Suggest recipes based on available pantry ingredients.
Use Android Activities and Intents to navigate between screens.
Design an interface that is simple and easy to use.
Use Git and GitHub to keep track of project changes.
3. Technologies Used

The following tools and technologies were used to develop the application:

Java: Used to write the application logic.
XML: Used to design the application's screens.
Android Studio: Used to develop and run the application.
SQLite: Used for local data storage through the database helper.
Gradle: Used to build the Android application.
Git: Used to track changes to the source code.
GitHub: Used to store the project repository online.
4. Application Features
   4.1 Home Dashboard

The home screen is the main point of navigation in the application. It contains a welcome message, a pantry section, an Add Ingredient button, a View My Pantry button and a Recipe Suggestions section.

4.2 Add Ingredients

The ingredient screen allows users to enter information about ingredients they want to store in their pantry. This includes the ingredient name, quantity, unit and an optional expiry date.

The application includes input validation to help prevent incorrect ingredient information from being saved.

4.3 View My Pantry

The pantry screen displays ingredients saved in the application. Each ingredient can be viewed with its stored information, depending on the fields supported by the application.

4.4 Suggested Recipes

The suggested recipes screen displays recipes that can be made using ingredients in the pantry.

During development, I tested the recipe suggestions screen with eggs and tomatoes, and the application displayed a Tomato Eggs recipe. This feature is intended to help users decide what they can cook with the ingredients they already have.

4.5 Local Database

The application uses PantryDatabaseHelper.java to handle database operations. SQLite is used to store application data locally on the device.

This allows the application to retrieve saved information without requiring a separate online database server.

5. Main Application Components

The project contains the following main Java classes:

File	Purpose
MainActivity.java	Displays the home dashboard and handles navigation.
AddIngredientActivity.java	Handles the ingredient entry screen.
PantryListActivity.java	Displays the saved pantry ingredients.
SuggestedRecipesActivity.java	Displays recipe suggestions.
PantryDatabaseHelper.java	Manages SQLite database creation and operations.

The main XML layout files are:

File	Purpose
activity_main.xml	Home dashboard layout.
activity_add_ingredient.xml	Add ingredient screen layout.
activity_pantry_list.xml	Pantry list screen layout.
activity_suggested_recipes.xml	Suggested recipes screen layout.
item_pantry_ingredient.xml	Layout for displaying a pantry ingredient.
6. Database Implementation

SQLite is used to store the application's data locally. The database operations are managed by the PantryDatabaseHelper class.

The database supports storing and retrieving pantry information. Recipe information and ingredient matching are also used by the recipe suggestion feature.

The exact table names, columns and database relationships are defined in the database helper class.

7. Testing

During development, I built the application and checked that the project compiled successfully. I also tested the suggested recipes screen using eggs and tomatoes, which resulted in a Tomato Eggs suggestion.

The following additional checks should be completed on the final version of the application:

Adding an ingredient and confirming that it is saved.
Viewing saved ingredients in the pantry.
Checking that data remains available after restarting the application.
Testing input validation.
Checking recipe suggestions with different pantry ingredients.
Confirming that navigation between screens works correctly.
Testing editing and deleting ingredients if those operations are implemented.

The final testing results should be recorded after testing the application on an emulator or Android device.

8. User Interface

The application uses XML layouts to create its screens. I used a consistent colour scheme with green and peach colours to give the app a simple kitchen-related appearance.

The interface is divided into separate screens so that users can navigate between adding ingredients, viewing their pantry and finding recipe suggestions.

Screenshots of the running application should be included in the assignment report to show the main features.

9. Version Control

I used Git to track changes made during development and GitHub to host the project.

The commit history shows the progress of the application, including the initial project setup, database development, ingredient handling, dashboard updates and recipe suggestion screens.

The repository contains the Android source code, XML layouts and resource files used by the application.

GitHub repository: [Insert your actual GitHub repository URL here]

10. How to Run the Application
    Install Android Studio.
    Clone the repository from GitHub or download the project.
    Open the project in Android Studio.
    Allow Gradle to synchronise.
    Run the application using an Android emulator or a compatible Android device.
    Open the dashboard and navigate through the available screens.

The project should build successfully before running the application.

11. Limitations and Future Improvements

The application can be improved by adding more recipes and providing detailed cooking instructions for each recipe.

Other possible improvements include making recipe matching more advanced, improving the way ingredient quantities are compared and adding further features to help users manage expiry dates and reduce food waste.

These improvements could make the application more useful in everyday kitchen management.

12. Conclusion

Developing Smart Pantry Manager helped me practise using Java, XML layouts, Android Activities, Intents and SQLite in an Android application.

The project combines ingredient management with recipe suggestions in one application. It also gave me practical experience with database operations, interface design and using Git and GitHub to track development.

13. Student Information
    Student name: Ofentse Kgopotse
    Module: Mobile App Development 700
    Assessment: Practical Assignment
    Submission date: 23 October 2026