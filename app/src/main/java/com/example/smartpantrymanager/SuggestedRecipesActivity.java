package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private ListView listRecipes;
    private TextView tvEmptyRecipes;
    private RecipeAdapter adapter;

    private final ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new PantryDatabaseHelper(this);

        listRecipes = findViewById(R.id.listSuggestedRecipes);
        tvEmptyRecipes = findViewById(R.id.tvEmptyRecipes);

        adapter = new RecipeAdapter();
        listRecipes.setAdapter(adapter);

        Button btnBack = findViewById(R.id.btnRecipesBack);
        btnBack.setOnClickListener(v -> finish());

        listRecipes.setOnItemClickListener((parent, view, position, id) -> {
            Recipe recipe = recipes.get(position);

            String message = recipe.instructions;
            try (Cursor cursor =
                         databaseHelper.getRecipeIngredients(recipe.id)) {

                StringBuilder details = new StringBuilder();
                details.append(recipe.instructions)
                        .append("\n\nIngredients:\n");

                while (cursor.moveToNext()) {
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow("ingredient_name"));

                    double quantity = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("required_quantity"));

                    String unit = cursor.getString(
                            cursor.getColumnIndexOrThrow("unit"));

                    details.append("\u2022 ")
                            .append(name)
                            .append(" — ")
                            .append(quantity == Math.floor(quantity)
                                    ? String.valueOf((long) quantity)
                                    : String.valueOf(quantity))
                            .append(" ")
                            .append(unit)
                            .append("\n");
                }

                message = details.toString();
            }

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(recipe.name)
                    .setMessage(message)
                    .setPositiveButton("Close", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {
        recipes.clear();

        try (Cursor cursor = databaseHelper.getSuggestedRecipes()) {
            while (cursor.moveToNext()) {
                recipes.add(new Recipe(
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("instructions"))
                ));
            }
        }

        adapter.notifyDataSetChanged();

        boolean isEmpty = recipes.isEmpty();

        tvEmptyRecipes.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE);

        listRecipes.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE);
    }

    private static class Recipe {
        final long id;
        final String name;
        final String instructions;

        Recipe(long id, String name, String instructions) {
            this.id = id;
            this.name = name;
            this.instructions = instructions;
        }
    }

    private class RecipeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return recipes.size();
        }

        @Override
        public Object getItem(int position) {
            return recipes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return recipes.get(position).id;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {

            View row = convertView;

            if (row == null) {
                row = LayoutInflater.from(
                        SuggestedRecipesActivity.this).inflate(
                        android.R.layout.simple_list_item_2,
                        parent,
                        false);
            }

            Recipe recipe = recipes.get(position);

            TextView title = row.findViewById(android.R.id.text1);
            TextView subtitle = row.findViewById(android.R.id.text2);

            title.setText(recipe.name);
            subtitle.setText(recipe.instructions);

            return row;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
            databaseHelper = null;
        }
    }
}
