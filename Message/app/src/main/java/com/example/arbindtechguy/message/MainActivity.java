package com.example.arbindtechguy.message;

import android.content.Intent;
import android.icu.util.Calendar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;
    private DatabaseReference userReference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            String onlineUserId= mAuth.getCurrentUser().getUid();
            userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(onlineUserId);
        }




        //Tab for main Activity

        myViewPager = findViewById(R.id.mainViewPager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout = findViewById(R.id.mainTabLayout);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            LogOutUser();
        }
        else if (currentUser !=null){
            userReference.child("online").setValue(true);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser !=null){
            userReference.child("online").setValue("true");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentUser !=null){
            userReference.child("online").setValue("true");
        }
    }

    private void LogOutUser() {
        Intent startPageIntent = new Intent(MainActivity.this, StartPageActivity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.menuLogout){
            if (currentUser != null){
                userReference.child("online").setValue(ServerValue.TIMESTAMP);
            }

            mAuth.signOut();
            LogOutUser();
        }
        if (item.getItemId() == R.id.menu_accSettings){
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
        }
        if (item.getItemId() == R.id.menu_all_users){
            startActivity(new Intent(MainActivity.this, AllUsersActivity.class));
        }
        return true;
    }
}