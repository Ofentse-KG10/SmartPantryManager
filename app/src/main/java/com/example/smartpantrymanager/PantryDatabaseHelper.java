
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL CHECK(quantity > 0), " +
                "unit TEXT NOT NULL, expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity REAL NOT NULL CHECK(required_quantity > 0), " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE settings (" +
                "setting_key TEXT PRIMARY KEY, " +
                "setting_value TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add database migration steps here when the schema changes.
    }

    public long addIngredient(String name, double quantity,
                              String unit, String expiryDate) {
        if (name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || !Double.isFinite(quantity) || quantity <= 0) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiry_date", expiryDate);

        return getWritableDatabase().insert("ingredients", null, values);
    }

    public Cursor getAllIngredients() {
        return getReadableDatabase().query(
                "ingredients",
                new String[]{"id", "name", "quantity", "unit", "expiry_date"},
                null, null, null, null, "name COLLATE NOCASE ASC");
    }

    public Cursor getIngredientById(long id) {
        return getReadableDatabase().query(
                "ingredients",
                new String[]{"id", "name", "quantity", "unit", "expiry_date"},
                "id = ?", new String[]{String.valueOf(id)},
                null, null, null);
    }

    public int updateIngredient(long id, String name, double quantity,
                                String unit, String expiryDate) {
        if (name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || !Double.isFinite(quantity) || quantity <= 0) {
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());
        values.put("expiry_date", expiryDate);

        return getWritableDatabase().update(
                "ingredients", values, "id = ?",
                new String[]{String.valueOf(id)});
    }

    public int deleteIngredient(long id) {
        return getWritableDatabase().delete(
                "ingredients", "id = ?",
                new String[]{String.valueOf(id)});
    }

    public long addRecipe(String name, String instructions) {
        if (name == null || name.trim().isEmpty()
                || instructions == null || instructions.trim().isEmpty()) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("instructions", instructions.trim());

        return getWritableDatabase().insert("recipes", null, values);
    }

    public long addRecipeIngredient(long recipeId, String ingredientName,
                                    double requiredQuantity, String unit) {
        if (recipeId <= 0 || ingredientName == null
                || ingredientName.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || !Double.isFinite(requiredQuantity)
                || requiredQuantity <= 0) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();

        Cursor recipeCursor = db.query(
                "recipes", new String[]{"id"}, "id = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null);

        boolean recipeExists;
        try {
            recipeExists = recipeCursor.moveToFirst();
        } finally {
            recipeCursor.close();
        }

        if (!recipeExists) {
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName.trim());
        values.put("required_quantity", requiredQuantity);
        values.put("unit", unit.trim());

        return db.insert("recipe_ingredients", null, values);
    }

    public Cursor getAllRecipes() {
        return getReadableDatabase().query(
                "recipes", new String[]{"id", "name", "instructions"},
                null, null, null, null, "name COLLATE NOCASE ASC");
    }

    public Cursor getRecipeIngredients(long recipeId) {
        return getReadableDatabase().query(
                "recipe_ingredients",
                new String[]{"id", "recipe_id", "ingredient_name",
                        "required_quantity", "unit"},
                "recipe_id = ?", new String[]{String.valueOf(recipeId)},
                null, null, "ingredient_name COLLATE NOCASE ASC");
    }

    public boolean saveSetting(String key, String value) {
        if (key == null || key.trim().isEmpty() || value == null) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("setting_key", key.trim());
        values.put("setting_value", value);

        return getWritableDatabase().insertWithOnConflict(
                "settings", null, values,
                SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public String getSetting(String key, String defaultValue) {
        Cursor cursor = getReadableDatabase().query(
                "settings", new String[]{"setting_value"},
                "setting_key = ?", new String[]{key},
                null, null, null);

        try {
            return cursor.moveToFirst() ? cursor.getString(0) : defaultValue;
        } finally {
            cursor.close();
        }
    }
}
