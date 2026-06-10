package com.example.n2121;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

/**
 * Activity handling new user registration using Firebase Authentication.
 */
public class RegisterActivity extends AppCompatActivity {

    EditText etEmail, etPass;
    Button btnRegister, btnBackToLogin;
    TextView tvMsg;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Connect variables to XML views
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btnRegister = findViewById(R.id.btnRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
        tvMsg = findViewById(R.id.tvMsg);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to login screen
                finish();
            }
        });
    }

    /**
     * Attempts to register a new user in Firebase Authentication.
     * Shows a ProgressDialog during registration and handles specific validation errors.
     */
    private void registerUser() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            tvMsg.setText("Please fill all fields");
            return;
        }

        // Show loading dialog exactly as shown in teacher's slides
        pd = ProgressDialog.show(this, "Registration", "Creating account, wait", true);

        FBRef.refAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            // Move to main screen
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Error handling using instanceof exactly like the slides
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthWeakPasswordException) {
                                tvMsg.setText("Password must be at least 6 characters).");
                            } else if (e instanceof FirebaseAuthUserCollisionException) {
                                tvMsg.setText("Email is already registered.");
                            } else {
                                tvMsg.setText("Registration failed:" + e.getMessage());
                            }
                        }
                    }
                });
    }
}