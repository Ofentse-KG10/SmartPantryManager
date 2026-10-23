
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private TextView tvPantryDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (view, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        databaseHelper = new PantryDatabaseHelper(this);

        tvPantryDescription = findViewById(
                R.id.tvPantryDescription
        );

        // Add Ingredient button
        Button btnAddIngredient = findViewById(
                R.id.btnAddIngredient
        );

        btnAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );
            startActivity(intent);
        });

        // View My Pantry button
        Button btnViewPantry = findViewById(
                R.id.btnViewPantry
        );

        btnViewPantry.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryListActivity.class
            );
            startActivity(intent);
        });

        // Recipe Suggestions card
        View recipeSuggestions = findViewById(
                R.id.recipeSuggestions
        );

        recipeSuggestions.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPantrySummary();
    }

    private void loadPantrySummary() {
        if (databaseHelper == null
                || tvPantryDescription == null) {
            return;
        }

        StringBuilder summary = new StringBuilder();
        int count = 0;

        try (Cursor cursor = databaseHelper.getAllIngredients()) {

            while (cursor.moveToNext()) {
                count++;

                if (count <= 5) {
                    String name = cursor.getString(
                            cursor.getColumnIndexOrThrow("name")
                    );

                    double quantity = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("quantity")
                    );

                    String unit = cursor.getString(
                            cursor.getColumnIndexOrThrow("unit")
                    );

                    if (summary.length() > 0) {
                        summary.append("\n");
                    }

                    String formattedQuantity;

                    if (quantity == Math.floor(quantity)) {
                        formattedQuantity = String.format(
                                Locale.getDefault(),
                                "%.0f",
                                quantity
                        );
                    } else {
                        formattedQuantity = String.format(
                                Locale.getDefault(),
                                "%s",
                                quantity
                        );
                    }

                    summary.append("• ")
                            .append(name)
                            .append(" — ")
                            .append(formattedQuantity)
                            .append(" ")
                            .append(unit);
                }
            }
        }

        if (count == 0) {
            tvPantryDescription.setText(
                    R.string.pantry_description
            );
        } else {
            String countText = getResources().getQuantityString(
                    R.plurals.pantry_count,
                    count,
                    count
            );

            if (count > 5) {
                summary.append("\n")
                        .append(getString(
                                R.string.pantry_more_items,
                                count - 5
                        ));
            }

            tvPantryDescription.setText(
                    getString(
                            R.string.pantry_summary_format,
                            countText,
                            summary.toString()
                    )
            );
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
