
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 3;

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
        createTables(db);
        seedRecipes(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "quantity REAL NOT NULL CHECK(quantity > 0)," +
                "unit TEXT NOT NULL," +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE," +
                "instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "recipe_id INTEGER NOT NULL," +
                "ingredient_name TEXT NOT NULL," +
                "required_quantity REAL NOT NULL CHECK(required_quantity > 0)," +
                "unit TEXT NOT NULL," +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");

        db.execSQL("CREATE TABLE settings (" +
                "setting_key TEXT PRIMARY KEY," +
                "setting_value TEXT NOT NULL)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS recipes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL UNIQUE," +
                    "instructions TEXT NOT NULL)");

            db.execSQL("CREATE TABLE IF NOT EXISTS recipe_ingredients (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "recipe_id INTEGER NOT NULL," +
                    "ingredient_name TEXT NOT NULL," +
                    "required_quantity REAL NOT NULL CHECK(required_quantity > 0)," +
                    "unit TEXT NOT NULL," +
                    "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");

            seedRecipes(db);
        }

        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS settings (" +
                    "setting_key TEXT PRIMARY KEY," +
                    "setting_value TEXT NOT NULL)");
        }
    }


    // PANTRY CRUD


    public long addIngredient(String name, double quantity,
                              String unit, String expiryDate) {

        if (name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || !Double.isFinite(quantity) || quantity <= 0) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();

        String cleanName = name.trim();
        String cleanUnit = unit.trim();

        db.beginTransaction();

        try {
            try (Cursor cursor = db.query(
                    "ingredients",
                    new String[]{"id", "quantity", "expiry_date"},
                    "name = ? COLLATE NOCASE AND unit = ? COLLATE NOCASE",
                    new String[]{cleanName, cleanUnit},
                    null,
                    null,
                    "id ASC")) {

                if (cursor.moveToFirst()) {
                    long id = cursor.getLong(0);
                    double oldQuantity = cursor.getDouble(1);
                    String oldExpiry = cursor.getString(2);

                    ContentValues values = new ContentValues();
                    values.put("quantity", oldQuantity + quantity);

                    values.put(
                            "expiry_date",
                            expiryDate == null || expiryDate.trim().isEmpty()
                                    ? oldExpiry
                                    : expiryDate.trim()
                    );

                    int updated = db.update(
                            "ingredients",
                            values,
                            "id = ?",
                            new String[]{String.valueOf(id)}
                    );

                    if (updated > 0) {
                        db.setTransactionSuccessful();
                        return id;
                    }

                    return -1;
                }
            }

            ContentValues values = new ContentValues();
            values.put("name", cleanName);
            values.put("quantity", quantity);
            values.put("unit", cleanUnit);
            values.put(
                    "expiry_date",
                    expiryDate == null || expiryDate.trim().isEmpty()
                            ? null
                            : expiryDate.trim()
            );

            long id = db.insert("ingredients", null, values);

            if (id != -1) {
                db.setTransactionSuccessful();
            }

            return id;

        } finally {
            db.endTransaction();
        }
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

    // RECIPE METHODS

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

        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName.trim());
        values.put("required_quantity", requiredQuantity);
        values.put("unit", unit.trim());

        return getWritableDatabase().insert(
                "recipe_ingredients", null, values);
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

    // SETTINGS

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
        if (key == null) return defaultValue;

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

    // SEED 20 RECIPES ON FIRST RUN

    private void seedRecipes(SQLiteDatabase db) {
        String[][] data = {
                {"Scrambled Eggs", "Whisk eggs and cook gently with butter.",
                        "eggs|2|piece", "butter|10|g"},
                {"Tomato Pasta", "Boil pasta and cook with tomatoes and onion.",
                        "pasta|100|g", "tomato|2|piece", "onion|1|piece"},
                {"Banana Pancakes", "Mash banana, mix with eggs and flour, then fry.",
                        "banana|1|piece", "eggs|1|piece", "flour|50|g"},
                {"Cheese Omelette", "Cook beaten eggs with cheese in a pan.",
                        "eggs|2|piece", "cheese|30|g"},
                {"Garlic Fried Rice", "Fry garlic and rice together.",
                        "rice|150|g", "garlic|2|clove", "oil|10|ml"},
                {"Mashed Potatoes", "Boil potatoes and mash with butter.",
                        "potato|200|g", "butter|15|g"},
                {"Apple Oatmeal", "Cook oats in milk and add apple.",
                        "oats|50|g", "milk|200|ml", "apple|1|piece"},
                {"Cheese Toast", "Toast bread with cheese.",
                        "bread|2|slice", "cheese|30|g"},
                {"Tomato Eggs", "Cook tomatoes and stir in beaten eggs.",
                        "tomato|2|piece", "eggs|2|piece"},
                {"Carrot Soup", "Boil carrots and onion until soft.",
                        "carrot|150|g", "onion|1|piece", "water|300|ml"},
                {"Peanut Butter Toast", "Toast bread and spread peanut butter.",
                        "bread|2|slice", "peanut butter|20|g"},
                {"Garden Salad", "Chop and mix the vegetables.",
                        "lettuce|50|g", "tomato|1|piece", "cucumber|1|piece"},
                {"Chicken Rice", "Cook chicken thoroughly and serve with rice.",
                        "chicken|150|g", "rice|100|g"},
                {"Tuna Sandwich", "Mix tuna and mayonnaise; serve on bread.",
                        "tuna|80|g", "bread|2|slice", "mayonnaise|15|g"},
                {"Spinach Omelette", "Cook spinach with beaten eggs.",
                        "spinach|40|g", "eggs|2|piece"},
                {"Bean Salad", "Mix beans with chopped onion and tomato.",
                        "beans|100|g", "onion|1|piece", "tomato|1|piece"},
                {"Garlic Bread", "Spread garlic butter on bread and toast.",
                        "bread|2|slice", "garlic|1|clove", "butter|10|g"},
                {"Yogurt Banana Bowl", "Slice banana and serve with yogurt.",
                        "banana|1|piece", "yogurt|150|g"},
                {"Cucumber Yogurt Salad", "Mix chopped cucumber with yogurt.",
                        "cucumber|1|piece", "yogurt|100|g"},
                {"Flatbread", "Mix flour and water, roll flat and cook.",
                        "flour|100|g", "water|60|ml"}
        };

        for (String[] recipe : data) {
            ContentValues rv = new ContentValues();
            rv.put("name", recipe[0]);
            rv.put("instructions", recipe[1]);

            long id = db.insertWithOnConflict(
                    "recipes", null, rv, SQLiteDatabase.CONFLICT_IGNORE);

            if (id == -1) {
                Cursor c = db.query("recipes", new String[]{"id"},
                        "name = ?", new String[]{recipe[0]},
                        null, null, null);
                try {
                    if (c.moveToFirst()) id = c.getLong(0);
                } finally {
                    c.close();
                }
            }

            if (id <= 0) continue;

            Cursor existing = db.query("recipe_ingredients",
                    new String[]{"id"}, "recipe_id = ?",
                    new String[]{String.valueOf(id)}, null, null, null);

            boolean hasIngredients;
            try {
                hasIngredients = existing.moveToFirst();
            } finally {
                existing.close();
            }

            if (hasIngredients) continue;

            for (int i = 2; i < recipe.length; i++) {
                String[] p = recipe[i].split("\\|");
                ContentValues iv = new ContentValues();
                iv.put("recipe_id", id);
                iv.put("ingredient_name", p[0]);
                iv.put("required_quantity", Double.parseDouble(p[1]));
                iv.put("unit", p[2]);
                db.insert("recipe_ingredients", null, iv);
            }
        }
    }

    // STRICT MATCHING: EVERY REQUIRED INGREDIENT AND QUANTITY MUST EXIST

    public boolean canMakeRecipe(long recipeId) {
        Cursor required = getRecipeIngredients(recipeId);
        Map<String, Double> pantry = new HashMap<>();

        Cursor items = getAllIngredients();
        try {
            while (items.moveToNext()) {
                String name = normalizeName(items.getString(
                        items.getColumnIndexOrThrow("name")));
                String unit = normalizeUnit(items.getString(
                        items.getColumnIndexOrThrow("unit")));
                double quantity = items.getDouble(
                        items.getColumnIndexOrThrow("quantity"));

                String key = name + "|" + unit;
                pantry.put(key, pantry.getOrDefault(key, 0.0) + quantity);
            }
        } finally {
            items.close();
        }

        try {
            if (!required.moveToFirst()) return false;

            do {
                String name = normalizeName(required.getString(
                        required.getColumnIndexOrThrow("ingredient_name")));
                String unit = normalizeUnit(required.getString(
                        required.getColumnIndexOrThrow("unit")));
                double needed = required.getDouble(
                        required.getColumnIndexOrThrow("required_quantity"));

                if (pantry.getOrDefault(name + "|" + unit, 0.0) < needed) {
                    return false;
                }
            } while (required.moveToNext());

            return true;
        } finally {
            required.close();
        }
    }

    public Cursor getSuggestedRecipes() {
        ArrayList<Long> ids = new ArrayList<>();
        Cursor all = getAllRecipes();

        try {
            while (all.moveToNext()) {
                long id = all.getLong(all.getColumnIndexOrThrow("id"));
                if (canMakeRecipe(id)) ids.add(id);
            }
        } finally {
            all.close();
        }

        if (ids.isEmpty()) {
            return getReadableDatabase().rawQuery(
                    "SELECT id, name, instructions FROM recipes WHERE 1=0",
                    null);
        }

        StringBuilder where = new StringBuilder("id IN (");
        String[] args = new String[ids.size()];

        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) where.append(",");
            where.append("?");
            args[i] = String.valueOf(ids.get(i));
        }
        where.append(")");

        return getReadableDatabase().query(
                "recipes", new String[]{"id", "name", "instructions"},
                where.toString(), args, null, null, "name COLLATE NOCASE ASC");
    }

    private String normalizeName(String value) {
        String s = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);

        if (s.endsWith("ies") && s.length() > 4) {
            return s.substring(0, s.length() - 3) + "y";
        }
        if (s.endsWith("oes") && s.length() > 4) {
            return s.substring(0, s.length() - 2);
        }
        if (s.endsWith("s") && !s.endsWith("ss") && s.length() > 3) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }


    private String normalizeUnit(String value) {
        String s = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);

        switch (s) {
            case "pcs":
            case "pc":
            case "pieces":
                return "piece";
            case "grams":
                return "g";
            case "millilitres":
            case "milliliters":
                return "ml";
            case "slices":
                return "slice";
            case "cloves":
                return "clove";
            default:
                return s;
        }
    }

    public void mergeDuplicateIngredients() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            Cursor cursor = db.rawQuery(
                    "SELECT MIN(id), " +
                            "LOWER(TRIM(name)), " +
                            "LOWER(TRIM(unit)), " +
                            "SUM(quantity), " +
                            "MAX(expiry_date) " +
                            "FROM ingredients " +
                            "GROUP BY LOWER(TRIM(name)), LOWER(TRIM(unit))",
                    null
            );

            try {
                while (cursor.moveToNext()) {
                    long keepId = cursor.getLong(0);
                    String name = cursor.getString(1);
                    String unit = cursor.getString(2);
                    double totalQuantity = cursor.getDouble(3);
                    String expiryDate = cursor.getString(4);

                    ContentValues values = new ContentValues();
                    values.put("quantity", totalQuantity);
                    values.put("expiry_date", expiryDate);

                    db.update(
                            "ingredients",
                            values,
                            "id = ?",
                            new String[]{String.valueOf(keepId)}
                    );

                    db.delete(
                            "ingredients",
                            "LOWER(TRIM(name)) = ? AND " +
                                    "LOWER(TRIM(unit)) = ? AND id != ?",
                            new String[]{
                                    name,
                                    unit,
                                    String.valueOf(keepId)
                            }
                    );
                }
            } finally {
                cursor.close();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

