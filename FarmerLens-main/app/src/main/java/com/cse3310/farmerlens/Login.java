package com.cse3310.farmerlens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser !=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        // editTextUserId = findViewById(R.id.user_id);
        // editTextDateOfBirth = findViewById(R.id.date_of_birth);
        buttonLogin = findViewById(R.id.btn_login);
        textView = findViewById(R.id.registerNow);

        TextView forgotPassword = findViewById(R.id.forgotPassword);

        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();

            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the "Forgot Password" button click
                forgotPassword();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Authentication Successful", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    if (!task.isSuccessful()) {
                                        String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                        switch (errorCode) {
                                            case "ERROR_INVALID_EMAIL":
                                                Toast.makeText(Login.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                                                break;
                                            case "ERROR_WRONG_PASSWORD":
                                                Toast.makeText(Login.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                                break;
                                            case "ERROR_USER_NOT_FOUND":
                                                Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                                                break;
                                            // Add more cases as needed
                                            default:
                                                Toast.makeText(Login.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                    }
                                }
                            }
                        });
            }
        });

    }
    public void forgotPassword() {
        String email = String.valueOf(editTextEmail.getText());
        if (!TextUtils.isEmpty(email)) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Password reset email sent. Check your email.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(Login.this, "Enter your email to reset your password.", Toast.LENGTH_SHORT).show();
        }
    }
}