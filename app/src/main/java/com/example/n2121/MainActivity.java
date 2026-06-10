package com.example.n2121;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

/**
 * Main Activity that reads expenses from Firebase and handles Delete/Update via long click.
 */
public class MainActivity extends AppCompatActivity {

    ListView lvExpenses;
    Button btnAdd;
    TextView tvTotal;

    ArrayList<String> arr;
    ArrayList<String> arrIds;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvExpenses = findViewById(R.id.lvExpenses);
        btnAdd = findViewById(R.id.btnAdd);
        tvTotal = findViewById(R.id.tvTotal);

        arr = new ArrayList<>();
        arrIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        lvExpenses.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        // Long click to Update or Delete
        lvExpenses.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String expenseId = arrIds.get(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose Action");
                builder.setMessage("Do you want to update or delete this expense?");

                // Delete Action
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FBRef.refExpenses.child(expenseId).removeValue();
                        Toast.makeText(MainActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                    }
                });

                // Update Action
                builder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, EditActivity.class);
                        intent.putExtra("id", expenseId);
                        startActivity(intent);
                    }
                });

                builder.show();
                return true;
            }
        });

        // Listen to database changes in real-time
        FBRef.refExpenses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arr.clear();
                arrIds.clear();
                double totalSum = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Expense exp = data.getValue(Expense.class);
                    if (exp != null) {
                        arrIds.add(0, exp.getId()); // Match index with the text list
                        totalSum += exp.getAmount();

                        String row = "Date: " + exp.getDate() + "\n" + exp.getCategory() + " - " + exp.getDesc() + "\nAmount: " + exp.getAmount() + " ₪";
                        arr.add(0, row);
                    }
                }

                adapter.notifyDataSetChanged();
                tvTotal.setText("Total: " + totalSum + " ₪");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}