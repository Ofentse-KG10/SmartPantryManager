package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class PantryListActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private ListView listPantryIngredients;
    private TextView tvPantryCount;
    private TextView tvPantryEmpty;

    private final ArrayList<Ingredient> ingredients =
            new ArrayList<>();

    private IngredientAdapter adapter;

    // Temporary cleanup flag.
    private boolean duplicatesMerged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_list);

        databaseHelper = new PantryDatabaseHelper(this);

        // TEMPORARY: Merge existing duplicate pantry entries.
        databaseHelper.mergeDuplicateIngredients();
        duplicatesMerged = true;

        listPantryIngredients =
                findViewById(R.id.listPantryIngredients);
        tvPantryCount = findViewById(R.id.tvPantryCount);
        tvPantryEmpty = findViewById(R.id.tvPantryEmpty);

        adapter = new IngredientAdapter();
        listPantryIngredients.setAdapter(adapter);

        Button btnAdd = findViewById(
                R.id.btnPantryAddIngredient);

        btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(
                    PantryListActivity.this,
                    AddIngredientActivity.class));
        });

        Button btnHome = findViewById(R.id.btnPantryHome);
        btnHome.setOnClickListener(v -> finish());

        listPantryIngredients.setOnItemClickListener(
                (parent, view, position, id) ->
                        showIngredientOptions(ingredients.get(position))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        ingredients.clear();

        try (Cursor cursor = databaseHelper.getAllIngredients()) {
            while (cursor.moveToNext()) {
                ingredients.add(new Ingredient(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
                ));
            }
        }

        adapter.notifyDataSetChanged();

        tvPantryCount.setText(getResources().getQuantityString(
                R.plurals.pantry_count,
                ingredients.size(),
                ingredients.size()
        ));

        boolean isEmpty = ingredients.isEmpty();
        tvPantryEmpty.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE);
        listPantryIngredients.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showIngredientOptions(Ingredient ingredient) {
        String[] options = {
                "View details",
                "Edit ingredient",
                "Delete ingredient"
        };

        new AlertDialog.Builder(this)
                .setTitle(ingredient.name)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showIngredientDetails(ingredient);
                    } else if (which == 1) {
                        editIngredient(ingredient);
                    } else {
                        confirmDelete(ingredient);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showIngredientDetails(Ingredient ingredient) {
        String expiry = ingredient.expiryDate == null
                || ingredient.expiryDate.trim().isEmpty()
                ? "Not set"
                : ingredient.expiryDate;

        String message =
                "Quantity: " + formatQuantity(ingredient.quantity)
                        + " " + ingredient.unit
                        + "\nExpiry date: " + expiry;

        new AlertDialog.Builder(this)
                .setTitle(ingredient.name)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void editIngredient(Ingredient ingredient) {
        Intent intent = new Intent(
                this, AddIngredientActivity.class);

        intent.putExtra("ingredient_id", ingredient.id);
        startActivity(intent);
    }

    private void confirmDelete(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient?")
                .setMessage("Delete " + ingredient.name
                        + " from your pantry?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    int deleted =
                            databaseHelper.deleteIngredient(ingredient.id);

                    if (deleted > 0) {
                        Toast.makeText(
                                this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                this,
                                "Could not delete ingredient",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    loadIngredients();
                })
                .show();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.format(Locale.getDefault(), "%.0f", quantity);
        }
        return String.format(Locale.getDefault(), "%s", quantity);
    }

    private static class Ingredient {
        final long id;
        final String name;
        final double quantity;
        final String unit;
        final String expiryDate;

        Ingredient(long id, String name, double quantity,
                   String unit, String expiryDate) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
            this.expiryDate = expiryDate;
        }
    }

    private class IngredientAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ingredients.size();
        }

        @Override
        public Object getItem(int position) {
            return ingredients.get(position);
        }

        @Override
        public long getItemId(int position) {
            return ingredients.get(position).id;
        }

        @Override
        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                row = LayoutInflater.from(PantryListActivity.this)
                        .inflate(
                                R.layout.item_pantry_ingredient,
                                parent,
                                false
                        );
            }

            Ingredient ingredient = ingredients.get(position);

            TextView name = row.findViewById(R.id.tvIngredientName);
            TextView quantity =
                    row.findViewById(R.id.tvIngredientQuantity);
            TextView expiry = row.findViewById(R.id.tvIngredientExpiry);

            name.setText(ingredient.name);
            quantity.setText(
                    formatQuantity(ingredient.quantity)
                            + " " + ingredient.unit
            );

            if (ingredient.expiryDate == null
                    || ingredient.expiryDate.trim().isEmpty()) {
                expiry.setVisibility(View.GONE);
            } else {
                expiry.setVisibility(View.VISIBLE);
                expiry.setText("Expires: " + ingredient.expiryDate);
            }

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
