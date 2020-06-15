package com.example.arbindtechguy.message;

import android.icu.util.Calendar;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {
    private String receiver_user_id;
    private Button sendFriendRequest;
    private Button cancelFriendRequest;
    private TextView profileName;
    private TextView profileStatus;
    private ImageView profileImage;
    private DatabaseReference profileReference;
    private String CURRENT_STATE;
    private DatabaseReference friendRequestReference;
    private FirebaseAuth mAuth;
    private String senderId;
    private DatabaseReference friendsReference;
    private DatabaseReference notificationReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        receiver_user_id = getIntent().getExtras().get("visit_user_id").toString();
        profileReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendsReference.keepSynced(true);
        sendFriendRequest = findViewById(R.id.profileVisitRequestBtn);
        cancelFriendRequest = findViewById(R.id.profileVisitDeclineRequest);
        profileName = findViewById(R.id.profileVisitUsername);
        profileStatus = findViewById(R.id.profileVisitUserStatus);
        profileImage = findViewById(R.id.profile_visit_user_image);
        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");
        friendRequestReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        senderId = mAuth.getCurrentUser().getUid().toString();
        CURRENT_STATE = "not_friends";
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);






        profileReference.child(receiver_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("user_name").getValue().toString();
                String status = dataSnapshot.child("user_status").getValue().toString();
                String image = dataSnapshot.child("user_image").getValue().toString();


                profileName.setText(name);
                profileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_profile).into(profileImage);

                friendRequestReference.child(senderId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(receiver_user_id)){
                                        String req_type = dataSnapshot.child(receiver_user_id).child("request_type").getValue().toString();//



                                        if (req_type.equals("sent")){
                                            CURRENT_STATE = "request_sent";
                                            sendFriendRequest.setText("Cancel Request");

                                            cancelFriendRequest.setVisibility(View.INVISIBLE);
                                            cancelFriendRequest.setEnabled(false);
                                        }

                                        else if (req_type.equals("received")){
                                            CURRENT_STATE = "request_received";
                                            sendFriendRequest.setText("Accept Request");

                                            cancelFriendRequest.setVisibility(View.VISIBLE);
                                            cancelFriendRequest.setEnabled(true);

                                            cancelFriendRequest.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    RejectFriendRequest();
                                                }
                                            });
                                        }
                                    }

                                else{
                                    friendsReference.child(senderId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(receiver_user_id)){
                                                CURRENT_STATE = "friends";
                                                sendFriendRequest.setText("Unfriend ");

                                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                                cancelFriendRequest.setEnabled(false);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cancelFriendRequest.setVisibility(View.INVISIBLE);
        cancelFriendRequest.setEnabled(false);
        if (!senderId.equals(receiver_user_id)){
            sendFriendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendFriendRequest.setEnabled(false);


                    if (CURRENT_STATE == "not_friends"){
                        SendRequest();
                    }
                    if (CURRENT_STATE == "request_sent"){
                        CancelRequest();
                    }
                    if (CURRENT_STATE == "request_received"){
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE == "friends"){
                        Unfriend();
                    }
                }
            });
        }
        else{
            sendFriendRequest.setVisibility(View.INVISIBLE);
            cancelFriendRequest.setVisibility(View.INVISIBLE);
        }


    }

    private void RejectFriendRequest() {

        friendRequestReference.child(senderId).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            friendRequestReference.child(receiver_user_id).child(senderId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                sendFriendRequest.setEnabled(true);
                                                CURRENT_STATE="not_friends";
                                                sendFriendRequest.setText("Send Request");
                                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                                cancelFriendRequest.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void Unfriend() {
        friendsReference.child(senderId).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    friendsReference.child(receiver_user_id).child(receiver_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                sendFriendRequest.setEnabled(true);
                                CURRENT_STATE="not_friends";
                                sendFriendRequest.setText("Send Request");

                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                cancelFriendRequest.setEnabled(false);
                            }

                        }
                    });
                }
            }
        });
    }

    private void AcceptFriendRequest() {



        Date date = new Date();
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = formatter.format(date);

        friendsReference.child(senderId).child(receiver_user_id).child("date").setValue(saveCurrentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendsReference.child(receiver_user_id).child(senderId).child("date").setValue(saveCurrentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendRequestReference.child(senderId).child(receiver_user_id).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            friendRequestReference.child(receiver_user_id).child(senderId).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                sendFriendRequest.setEnabled(true);
                                                                                CURRENT_STATE="friends";
                                                                                sendFriendRequest.setText("Unfriend");

                                                                                cancelFriendRequest.setVisibility(View.INVISIBLE);
                                                                                cancelFriendRequest.setEnabled(false);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });//deletes requests
                                    }
                                });
                    }
                });

    }

    private void CancelRequest() {
        friendRequestReference.child(senderId).child(receiver_user_id).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            friendRequestReference.child(receiver_user_id).child(senderId).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                HashMap<String, String> notificationData = new HashMap<String, String>();
                                                notificationData.put("from", senderId);
                                                notificationData.put("type_request", "request");
                                                notificationReference.child(receiver_user_id).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    sendFriendRequest.setEnabled(true);
                                                                    CURRENT_STATE="not_friends";
                                                                    sendFriendRequest.setText("Send Request");

                                                                    cancelFriendRequest.setVisibility(View.INVISIBLE);
                                                                    cancelFriendRequest.setEnabled(false);
                                                                }
                                                            }
                                                        });


                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void SendRequest() {
        friendRequestReference.child(senderId).child(receiver_user_id)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            friendRequestReference.child(receiver_user_id).child(senderId).child("request_type").setValue("received")
                                     .addOnCompleteListener(new OnCompleteListener<Void>() {
                                         @Override
                                         public void onComplete(@NonNull Task<Void> task) {
                                             if (task.isSuccessful()){
                                                 sendFriendRequest.setEnabled(true);
                                                 CURRENT_STATE = "request_sent";
                                                 sendFriendRequest.setText("Cancel Request");

                                                 cancelFriendRequest.setVisibility(View.INVISIBLE);
                                                 cancelFriendRequest.setEnabled(false);
                                             }
                                         }
                                     });
                        }
                    }
                });
    }
}
