package com.example.my_assigned_application;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.my_assigned_application.database.PantryDatabaseHelper;
import com.example.my_assigned_application.model.PantryItem;
import com.example.my_assigned_application.util.QuantityFormat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * One screen used for both adding a new pantry item and editing an existing one.
 * If the launching Intent carries EXTRA_ITEM_ID the screen is in edit mode.
 */
public class AddEditIngredientActivity extends AppCompatActivity {

    /** Key for the Intent extra holding the id of the item to edit. */
    public static final String EXTRA_ITEM_ID = "item_id";

    private PantryDatabaseHelper db;
    private long itemId = -1;
    private PantryItem existingItem;

    private TextInputLayout layoutName;
    private TextInputLayout layoutQuantity;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editExpiry;
    private Spinner spinnerUnit;
    private String[] units;

    /** Stored as "yyyy-MM-dd", or null when no expiry date is set. */
    private String expiryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        db = PantryDatabaseHelper.getInstance(this);

        layoutName = findViewById(R.id.layoutName);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonClearDate = findViewById(R.id.buttonClearDate);

        // Unit dropdown
        units = getResources().getStringArray(R.array.units);
        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, units);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(unitAdapter);

        // Back arrow in the title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Add mode or edit mode?
        itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);
        if (itemId != -1) {
            existingItem = db.getPantryItem(itemId);
            if (existingItem == null) {
                finish(); // the item no longer exists
                return;
            }
            setTitle(R.string.title_edit);
            fillForm(existingItem);
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle(R.string.add_ingredient);
        }

        editExpiry.setOnClickListener(v -> showDatePicker());
        buttonClearDate.setOnClickListener(v -> setExpiry(null));
        buttonSave.setOnClickListener(v -> saveItem());
        buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    /** Puts an existing item's values into the form fields. */
    private void fillForm(PantryItem item) {
        editName.setText(item.getName());
        editQuantity.setText(QuantityFormat.format(item.getQuantity()));
        for (int i = 0; i < units.length; i++) {
            if (units[i].equals(item.getUnit())) {
                spinnerUnit.setSelection(i);
                break;
            }
        }
        setExpiry(item.getExpiryDate());
    }

    private void setExpiry(String date) {
        expiryDate = date;
        editExpiry.setText(date == null ? "" : date);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (expiryDate != null) { // open on the date already chosen
            String[] parts = expiryDate.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]) - 1;
            day = Integer.parseInt(parts[2]);
        }

        new DatePickerDialog(this, (picker, y, m, d) ->
                setExpiry(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                year, month, day).show();
    }

    /** Validates the form; if everything is valid, creates or updates the item. */
    private void saveItem() {
        String name = String.valueOf(editName.getText()).trim();
        String quantityText = String.valueOf(editQuantity.getText()).trim();

        layoutName.setError(null);
        layoutQuantity.setError(null);
        boolean valid = true;

        // Name: required, and must contain at least one letter
        if (name.isEmpty()) {
            layoutName.setError(getString(R.string.error_name_required));
            valid = false;
        } else if (!name.matches(".*\\p{L}.*")) {
            layoutName.setError(getString(R.string.error_name_invalid));
            valid = false;
        }

        // Quantity: required, a valid number, greater than 0 and not absurdly large
        double quantity = 0;
        if (quantityText.isEmpty()) {
            layoutQuantity.setError(getString(R.string.error_quantity_required));
            valid = false;
        } else {
            try {
                quantity = Double.parseDouble(quantityText);
                if (quantity <= 0) {
                    layoutQuantity.setError(getString(R.string.error_quantity_invalid));
                    valid = false;
                } else if (quantity > 100000) {
                    layoutQuantity.setError(getString(R.string.error_quantity_too_large));
                    valid = false;
                }
            } catch (NumberFormatException e) { // e.g. "." or "1.2.3"
                layoutQuantity.setError(getString(R.string.error_quantity_invalid));
                valid = false;
            }
        }

        if (!valid) {
            return; // stay on the form so the user can fix the errors
        }

        String unit = (String) spinnerUnit.getSelectedItem();

        if (existingItem == null) {
            long newId = db.addPantryItem(new PantryItem(name, quantity, unit, expiryDate));
            if (newId == -1) {
                Toast.makeText(this, R.string.toast_save_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, R.string.toast_added, Toast.LENGTH_SHORT).show();
        } else {
            existingItem.setName(name);
            existingItem.setQuantity(quantity);
            existingItem.setUnit(unit);
            existingItem.setExpiryDate(expiryDate);
            db.updatePantryItem(existingItem);
            Toast.makeText(this, R.string.toast_updated, Toast.LENGTH_SHORT).show();
        }
        finish(); // back to the pantry list, which reloads in onResume()
    }

    /** Asks for confirmation before permanently deleting the item. */
    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(getString(R.string.dialog_delete_message, existingItem.getName()))
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    db.deletePantryItem(itemId);
                    Toast.makeText(this, R.string.toast_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}