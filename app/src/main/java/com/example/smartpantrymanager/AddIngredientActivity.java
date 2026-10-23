package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etExpiryDate;
    private Spinner spinnerUnit;

    private PantryDatabaseHelper databaseHelper;

    private long ingredientId = -1;
    private boolean editMode = false;

    private final ArrayList<String> units = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        databaseHelper = new PantryDatabaseHelper(this);

        etIngredientName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etQuantity);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        spinnerUnit = findViewById(R.id.spinnerUnit);

        // Standard units supported by the application.
        units.add("piece");
        units.add("g");
        units.add("kg");
        units.add("ml");
        units.add("l");
        units.add("slice");
        units.add("clove");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(adapter);

        Button btnSave = findViewById(R.id.btnSaveIngredient);
        Button btnCancel = findViewById(R.id.btnCancelIngredient);

        // Read the ingredient ID passed by PantryListActivity.
        ingredientId = getIntent().getLongExtra(
                "ingredient_id", -1
        );

        editMode = ingredientId > 0;

        if (editMode) {
            btnSave.setText("Update Ingredient");
            loadIngredientForEditing();
        }

        btnSave.setOnClickListener(v -> saveIngredient());

        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * Loads the selected ingredient and populates the form.
     */
    private void loadIngredientForEditing() {

        try (Cursor cursor =
                     databaseHelper.getIngredientById(ingredientId)) {

            if (!cursor.moveToFirst()) {
                Toast.makeText(
                        this,
                        "Ingredient no longer exists",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
                return;
            }

            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow("name")
            );

            double quantity = cursor.getDouble(
                    cursor.getColumnIndexOrThrow("quantity")
            );

            String savedUnit = cursor.getString(
                    cursor.getColumnIndexOrThrow("unit")
            );

            String expiryDate = cursor.getString(
                    cursor.getColumnIndexOrThrow("expiry_date")
            );

            etIngredientName.setText(name);

            etQuantity.setText(
                    quantity == Math.floor(quantity)
                            ? String.valueOf((long) quantity)
                            : String.valueOf(quantity)
            );

            // Preserve previously saved units, even if they are
            // not among the standard units listed above.
            int unitPosition = -1;

            for (int i = 0; i < units.size(); i++) {
                if (units.get(i).equalsIgnoreCase(savedUnit)) {
                    unitPosition = i;
                    break;
                }
            }

            if (unitPosition == -1
                    && savedUnit != null
                    && !savedUnit.trim().isEmpty()) {

                units.add(savedUnit);
                adapterNotifyDataSetChanged();
                unitPosition = units.size() - 1;
            }

            if (unitPosition >= 0) {
                spinnerUnit.setSelection(unitPosition);
            }

            etExpiryDate.setText(
                    expiryDate == null ? "" : expiryDate
            );
        }
    }

    /**
     * Refreshes the spinner adapter after adding a saved unit.
     */
    private void adapterNotifyDataSetChanged() {
        if (spinnerUnit.getAdapter() instanceof ArrayAdapter<?>) {
            ((ArrayAdapter<?>) spinnerUnit.getAdapter())
                    .notifyDataSetChanged();
        }
    }

    /**
     * Validates the form and inserts or updates the database record.
     */
    private void saveIngredient() {

        String name = etIngredientName.getText()
                .toString().trim();

        String quantityText = etQuantity.getText()
                .toString().trim();

        Object selectedUnit = spinnerUnit.getSelectedItem();

        if (selectedUnit == null) {
            Toast.makeText(
                    this,
                    "Please select a unit",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String unit = selectedUnit.toString();

        String expiryDate = etExpiryDate.getText()
                .toString().trim();

        // Validate ingredient name.
        if (name.isEmpty()) {
            etIngredientName.setError(
                    "Enter an ingredient name"
            );
            etIngredientName.requestFocus();
            return;
        }

        // Validate quantity input.
        if (quantityText.isEmpty()) {
            etQuantity.setError("Enter a quantity");
            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);

            if (Double.isNaN(quantity)
                    || Double.isInfinite(quantity)
                    || quantity <= 0) {

                etQuantity.setError(
                        "Quantity must be greater than zero"
                );
                etQuantity.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {
            etQuantity.setError(
                    "Enter a valid numeric quantity"
            );
            etQuantity.requestFocus();
            return;
        }

        // Validate a real calendar date in YYYY-MM-DD format.
        if (!expiryDate.isEmpty()
                && !isValidExpiryDate(expiryDate)) {

            etExpiryDate.setError(
                    "Enter a valid date in YYYY-MM-DD format"
            );
            etExpiryDate.requestFocus();
            return;
        }

        String savedExpiryDate = expiryDate.isEmpty()
                ? null
                : expiryDate;

        boolean success;

        if (editMode) {

            int updated = databaseHelper.updateIngredient(
                    ingredientId,
                    name,
                    quantity,
                    unit,
                    savedExpiryDate
            );

            success = updated > 0;

        } else {

            long inserted = databaseHelper.addIngredient(
                    name,
                    quantity,
                    unit,
                    savedExpiryDate
            );

            success = inserted != -1;
        }

        if (success) {

            Toast.makeText(
                    this,
                    editMode
                            ? "Ingredient updated successfully"
                            : "Ingredient saved successfully",
                    Toast.LENGTH_SHORT
            ).show();

            // Return to the previous screen.
            // PantryListActivity reloads its data in onResume().
            finish();

        } else {

            Toast.makeText(
                    this,
                    editMode
                            ? "Could not update ingredient"
                            : "Could not save ingredient",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    /**
     * Strictly checks the date format and calendar validity.
     * Examples: 2026-10-23 is valid; 2026-15-40 is invalid.
     */
    private boolean isValidExpiryDate(String value) {

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.ROOT
        );

        format.setLenient(false);

        ParsePosition position = new ParsePosition(0);
        Date date = format.parse(value, position);

        return date != null
                && position.getIndex() == value.length();
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
