package com.cse3310.farmerlens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import kotlinx.coroutines.scheduling.Task;

public class Register extends Activity {

    TextInputEditText editTextEmail, editTextPassword, editTextUserId, editTextDateOfBirth;
    Button buttonReg;
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
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth= FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextUserId = findViewById(R.id.user_id);
        editTextDateOfBirth = findViewById(R.id.date_of_birth);
        buttonReg = findViewById(R.id.btn_register);
        textView = findViewById(R.id.loginNow);

       textView.setOnClickListener(new  View.OnClickListener(){
           public void onClick(View view){
               Intent intent = new Intent(getApplicationContext(), Login.class);
               startActivity(intent);
               finish();

           }
       });
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editTextEmail.getText());
                String password = String.valueOf(editTextPassword.getText());
                String user_id = String.valueOf(editTextUserId.getText());
                String date_of_birth = String.valueOf(editTextDateOfBirth.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(user_id)) {
                    Toast.makeText(Register.this, "Enter your user ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(date_of_birth)) {
                    Toast.makeText(Register.this, "Enter your date of birth", Toast.LENGTH_SHORT).show();
                    return;
                }

                createUserWithEmailAndPassword(email, password, user_id, date_of_birth);
            }
        });
    }

      private void createUserWithEmailAndPassword(String email, String password, String user_id, String date_of_birth) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userID = user.getUid();
                                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                                usersRef.child(userID).child("user_id").setValue(user_id);
                                usersRef.child(userID).child("date_of_birth").setValue(date_of_birth);
                            }
                            Log.d("RegisterActivity", "Registration successful.");
                            Toast.makeText(Register.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                Log.e("RegisterActivity", "Registration failed: Email already in use.");
                                Toast.makeText(Register.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                            } else {

                                Log.e("RegisterActivity", "Registration failed: " + exception.getMessage());
                                Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                });
    }
}
