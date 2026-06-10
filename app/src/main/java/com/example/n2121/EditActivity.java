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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;

/**
 * Activity for editing an existing expense in Firebase Realtime Database.
 */
public class EditActivity extends AppCompatActivity {

    EditText etDesc, etAmount;
    Spinner spCategory;
    TextView tvDate;
    Button btnUpdate;

    String expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        etDesc = findViewById(R.id.etDesc);
        etAmount = findViewById(R.id.etAmount);
        spCategory = findViewById(R.id.spCategory);
        tvDate = findViewById(R.id.tvDate);
        btnUpdate = findViewById(R.id.btnUpdate);

        // Get the specific ID passed from MainActivity
        expenseId = getIntent().getStringExtra("id");

        // Load the current data from Firebase once
        FBRef.refExpenses.child(expenseId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Expense exp = snapshot.getValue(Expense.class);
                if (exp != null) {
                    etDesc.setText(exp.getDesc());
                    etAmount.setText(String.valueOf(exp.getAmount()));
                    tvDate.setText(exp.getDate());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String sMonth = (month + 1 < 10) ? "0" + (month + 1) : String.valueOf(month + 1);
                        String sDay = (day < 10) ? "0" + day : String.valueOf(day);
                        tvDate.setText(year + "-" + sMonth + "-" + sDay);
                    }
                }, year, month, day);
                dpd.show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateExpenseInFirebase();
            }
        });
    }

    private void updateExpenseInFirebase() {
        String desc = etDesc.getText().toString();
        String amountStr = etAmount.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        String date = tvDate.getText().toString();

        if (desc.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);
        Expense updatedExpense = new Expense(expenseId, desc, amount, category, date);

        // Overwrite the existing data in Firebase
        FBRef.refExpenses.child(expenseId).setValue(updatedExpense).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditActivity.this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}