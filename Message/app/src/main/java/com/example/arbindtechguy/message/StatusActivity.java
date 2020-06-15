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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Button changeStatusButton;
    private EditText changeText;
    private DatabaseReference changeStatusReference;
    private FirebaseAuth mAuth;
    private ProgressDialog loading_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mAuth = FirebaseAuth.getInstance();
        String user_id = mAuth.getCurrentUser().getUid();
        loading_bar = new ProgressDialog(this);
        changeStatusReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        changeStatusButton = findViewById(R.id.statusChangeButton);
        changeText = findViewById(R.id.changeStatus);//error
        changeStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String new_status = changeText.getText().toString();
               ChangeProfileStatus(new_status);
            }
        });

    }

    private void ChangeProfileStatus(String new_status) {
        if (TextUtils.isEmpty(new_status)){
            Toast.makeText(this, "Status Cant be empty..", Toast.LENGTH_SHORT).show();
        }
        else{
            loading_bar.setTitle("Change Profile Status");
            loading_bar.setMessage("Please Wait while we are updating your profile status..");
            loading_bar.show();
            changeStatusReference.child("user_status").setValue(new_status)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loading_bar.dismiss();
                                startActivity(new Intent(StatusActivity.this, SettingsActivity.class));
                                Toast.makeText(StatusActivity.this, "Status changed sucessfully...", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(StatusActivity.this, "Error Occured...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
