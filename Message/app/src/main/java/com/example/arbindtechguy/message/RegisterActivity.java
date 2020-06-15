package com.example.arbindtechguy.message;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText registerName;
    private EditText registerEmail;
    private EditText registerPassword;
    private Button registerButton;
    private ProgressDialog loadingBar;
    private DatabaseReference storeUserDefaultDataReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        registerName = findViewById(R.id.registerName);
        registerEmail = findViewById(R.id.registerEmail);
        registerPassword = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerBtn);
        loadingBar = new ProgressDialog(this);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String name = registerName.getText().toString();
                String password = registerPassword.getText().toString();
                String email = registerEmail.getText().toString();
                Register_Account(name,email,password);
            }
        });



    }


    private void Register_Account(final String name, String email, String password){
        if(TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this, "Name cant be Empty!!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Email id cant be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Password cant be blank", Toast.LENGTH_SHORT).show();
            return;

        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingBar.setTitle("Creating Account");
        loadingBar.setMessage("Please Wait!");
        loadingBar.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Account Created...",
                                    Toast.LENGTH_SHORT).show();
                            String current_user_id = mAuth.getCurrentUser().getUid();
                            storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                            storeUserDefaultDataReference.child("user_name").setValue(name);
                            storeUserDefaultDataReference.child("user_status").setValue("Hey, I am using powerful Message App");
                            storeUserDefaultDataReference.child("user_image").setValue("default_profile");
                            storeUserDefaultDataReference.child("user_thumb_image").setValue("default_image")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    });


                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            return;
                        }
                    }
                });


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
}