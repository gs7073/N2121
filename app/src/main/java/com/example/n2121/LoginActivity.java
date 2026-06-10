package com.example.n2121;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

/**
 * Activity handling user authentication and login operations.
 */
public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPass;
    Button btnLogin, btnGoToRegister;
    TextView tvMsg;
    CheckBox cbStayConnect;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Connect variables to XML views
        etEmail = findViewById(R.id.etEmail);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        tvMsg = findViewById(R.id.tvMsg);
        cbStayConnect = findViewById(R.id.cbStayConnect);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to registration screen
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Checks if the user chose to stay connected in a previous session.
     * This method runs every time the activity starts.
     */
    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences settings = getSharedPreferences("PREFS", MODE_PRIVATE);
        boolean stayConnected = settings.getBoolean("stayConnect", false);

        // If the user requested to stay connected and is already logged in
        if (stayConnected && FBRef.refAuth.getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Attempts to log in the user using Firebase Authentication.
     * Displays a ProgressDialog during the process and handles specific errors.
     */
    private void loginUser() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            tvMsg.setText("Please enter email and password");
            return;
        }

        // Show loading dialog exactly as shown in teacher's slides
        pd = ProgressDialog.show(this, "Login", "Logging in, wait", true);

        FBRef.refAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pd.dismiss();

                        if (task.isSuccessful()) {
                            // Save stay connected preference using SharedPreferences
                            SharedPreferences settings = getSharedPreferences("PREFS", MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("stay connect", cbStayConnect.isChecked());
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Move to main screen
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Error handling with instanceof as shown in slides
                            Exception e = task.getException();
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                tvMsg.setText("User doesnt exist.");
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                tvMsg.setText("Incorrect password or email");
                            } else {
                                tvMsg.setText("Login failed:" + e.getMessage());
                            }
                        }
                    }
                });
    }
}