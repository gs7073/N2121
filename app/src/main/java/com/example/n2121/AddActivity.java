package com.example.n2121;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.Calendar;

/**
 * Activity for adding a new expense to Firebase Realtime Database.
 */
public class AddActivity extends AppCompatActivity {

    EditText etDesc, etAmount;
    Spinner spCategory;
    TextView tvDate;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Connect variables to XML views
        etDesc = findViewById(R.id.etDesc);
        etAmount = findViewById(R.id.etAmount);
        spCategory = findViewById(R.id.spCategory);
        tvDate = findViewById(R.id.tvDate);
        btnSave = findViewById(R.id.btnSave);

        // Setup DatePickerDialog for choosing a date
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String sMonth = String.valueOf(month + 1);
                        if (month + 1 < 10) sMonth = "0" + sMonth;

                        String sDay = String.valueOf(day);
                        if (day < 10) sDay = "0" + sDay;

                        tvDate.setText(year + "-" + sMonth + "-" + sDay);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        // Save button click listener
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExpenseToFirebase();
            }
        });
    }

    /**
     * Collects data from the form and saves it to Firebase Realtime Database.
     */
    private void saveExpenseToFirebase() {
        String desc = etDesc.getText().toString();
        String amountStr = etAmount.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        String date = tvDate.getText().toString();

        if (desc.isEmpty() || amountStr.isEmpty() || date.equals("Click here to choose date")) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // 1. Generate a unique ID using Firebase push()
        String id = FBRef.refExpenses.push().getKey();

        if (id != null) {
            // 2. Create the Expense object
            Expense newExpense = new Expense(id, desc, amount, category, date);

            // 3. Save to Firebase Database
            FBRef.refExpenses.child(id).setValue(newExpense).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddActivity.this, "Expense added successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Return to MainActivity
                    } else {
                        Toast.makeText(AddActivity.this, "Error adding expense", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}