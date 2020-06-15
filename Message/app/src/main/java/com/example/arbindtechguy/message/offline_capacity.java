package com.example.arbindtechguy.message;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by arbindtechguy on 9/3/18.
 */

public class offline_capacity extends Application {
    private DatabaseReference UsersReferences;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //Images offline

        Picasso.Builder loader = new Picasso.Builder(this);
        loader.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = loader.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            String onlineUserId= mAuth.getCurrentUser().getUid();

            UsersReferences = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(onlineUserId);
            UsersReferences.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    UsersReferences.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                    UsersReferences.child("online").setValue(true);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
