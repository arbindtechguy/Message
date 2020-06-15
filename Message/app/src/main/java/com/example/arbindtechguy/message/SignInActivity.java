package com.example.arbindtechguy.message;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        loginEmail = findViewById(R.id.signInEmail);
        loginPassword = findViewById(R.id.signin_password);
        loginButton = findViewById(R.id.Login_btn);
        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);
        //loadingBar = findViewById(R.id.loginProgressBar);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                login(email,password);
            }
        });
    }

    private void login(String email, String password) {

        if (TextUtils.isEmpty(email)){
            Toast.makeText(SignInActivity.this, "Email id cant be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(SignInActivity.this, "Password cant be blank", Toast.LENGTH_SHORT).show();
            return;

        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setTitle("logging Account");
        loadingBar.setMessage("Please Wait!");
        loadingBar.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(SignInActivity.this, "Logging In....",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(SignInActivity.this, MainActivity.class));
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                           // updateUI(null);
                        }

                        // ...
                    }
                });

    }
}
