package com.example.n2121;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * Activity for searching and filtering expenses in Firebase Realtime Database.
 */
public class SearchActivity extends AppCompatActivity {

    EditText etSearchDesc, etMinAmount;
    Button btnSearchDesc, btnFilterAmount;
    ListView lvSearchResults;

    ArrayList<String> arr;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearchDesc = findViewById(R.id.etSearchDesc);
        etMinAmount = findViewById(R.id.etMinAmount);
        btnSearchDesc = findViewById(R.id.btnSearchDesc);
        btnFilterAmount = findViewById(R.id.btnFilterAmount);
        lvSearchResults = findViewById(R.id.lvSearchResults);

        arr = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        lvSearchResults.setAdapter(adapter);

        // Search by exact description
        btnSearchDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = etSearchDesc.getText().toString();
                if (desc.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter description", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Firebase query: order by description and find exact match
                FBRef.refExpenses.orderByChild("desc").equalTo(desc).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        processResults(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        // Filter by minimum amount
        btnFilterAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etMinAmount.getText().toString();
                if (amountStr.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Enter minimum amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                double minAmount = Double.parseDouble(amountStr);

                // Firebase query: order by amount and get all values starting from minAmount
                FBRef.refExpenses.orderByChild("amount").startAt(minAmount).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        processResults(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    /**
     * Processes the snapshot returned from Firebase and updates the ListView.
     */
    private void processResults(DataSnapshot snapshot) {
        arr.clear();
        for (DataSnapshot data : snapshot.getChildren()) {
            Expense exp = data.getValue(Expense.class);
            if (exp != null) {
                String row = "Date: " + exp.getDate() + "\n" + exp.getCategory() + " - " + exp.getDesc() + "\nAmount: " + exp.getAmount() + " ₪";
                arr.add(row);
            }
        }

        adapter.notifyDataSetChanged();

        if (arr.isEmpty()) {
            Toast.makeText(SearchActivity.this, "No results found", Toast.LENGTH_SHORT).show();
        }
    }
}