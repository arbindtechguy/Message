package com.example.arbindtechguy.message;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class WelcomeActivity extends AppCompatActivity {
    private Button signIn;
    private Button register;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        mAuth= FirebaseAuth.getInstance();
        signIn = (Button) findViewById(R.id.signIn);
        register = (Button) findViewById(R.id.register);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logInIntent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivity(logInIntent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logInIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(logInIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent logInIntent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(logInIntent);

        }
    }
}

