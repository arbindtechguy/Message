package com.example.arbindtechguy.message;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private RecyclerView myRequestList;
    private View myMainView;
    private FirebaseAuth mAuth;
    private DatabaseReference friendRequestReference;
    String online_user_id;
    private DatabaseReference userReference;

    private DatabaseReference friendsDatabaseRef;
    private DatabaseReference friendsReqDatabaseRef;
    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myMainView =  inflater.inflate(R.layout.fragment_requests, container, false);
        myRequestList = (RecyclerView) myMainView.findViewById(R.id.request_list);
        myRequestList.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        friendRequestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Requests").child(online_user_id);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_Requests");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myRequestList.setLayoutManager(linearLayoutManager);

        return myMainView;
    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Requests, RequestViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>(
                Requests.class,
                R.layout.friends_request_layout,
                RequestViewHolder.class,
                friendRequestReference
        ) {
            @Override
            protected void populateViewHolder(final RequestsFragment.RequestViewHolder viewHolder,final Requests model,final int position) {
                final String listUserId = getRef(position).getKey();
                //Toast.makeText(getContext(), listUserId, Toast.LENGTH_SHORT).show();
                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();
                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String requestType = dataSnapshot.getValue().toString();
                            if (requestType.equals("received")){


                                userReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String userStatus = dataSnapshot.child("user_status").getValue().toString();


                                        viewHolder.setUser_name(userName);
                                        viewHolder.setUser_thumb_image(thumbImage, getContext());
                                        viewHolder.setUser_status(userStatus);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

//                                                String visit_user_id = getRef(position).getKey();
//                                                Intent profile = new Intent(getContext(), ProfileActivity.class);
//                                                profile.putExtra("visit_user_id",listUserId);
//                                                startActivity(profile);

                                                CharSequence options[] = new CharSequence[]{
                                                        "Accept",
                                                        "Cancel"
                                                };



                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Requests");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {
                                                        Toast.makeText(getContext(),"Clicked", Toast.LENGTH_SHORT).show();
                                                        if (position == 0){


                                                            Date date = new Date();
                                                            SimpleDateFormat formatter;
                                                            formatter = new SimpleDateFormat("dd-MMMM-yyyy");
                                                            final String saveCurrentDate = formatter.format(date);

                                                            friendsDatabaseRef.child(online_user_id).child(listUserId).child("date").setValue(saveCurrentDate)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            friendsDatabaseRef.child(listUserId).child(online_user_id).child("date").setValue(saveCurrentDate)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            friendRequestReference.child(online_user_id).child(listUserId).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()){
                                                                                                                friendRequestReference.child(listUserId).child(online_user_id).removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()){
                                                                                                                                    Toast.makeText(getContext(), "You guys are friends now..", Toast.LENGTH_SHORT).show();
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
                                                        if (position == 1){

                                                            friendsReqDatabaseRef.child(online_user_id).child(listUserId).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){
                                                                                friendsReqDatabaseRef.child(listUserId).child(online_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()){
                                                                                                    Toast.makeText(getContext(), "Request cancelled..", Toast.LENGTH_SHORT).show();

                                                                                                }
                                                                                            }
                                                                                        });
                                                                            }
                                                                        }
                                                                    });



                                                        }
                                                    }
                                                });
                                                builder.show();




                                            }
                                        });



                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });




                            }
                            else if (requestType.equals("sent")){
                                Button req_send_btn = viewHolder.mView.findViewById(R.id.requestAcceptBtn);
                                req_send_btn.setText("Requet Sent");

                                viewHolder.mView.findViewById(R.id.requestRejectBtn).setVisibility(View.INVISIBLE);
                                userReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                                        final String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                                        final String userStatus = dataSnapshot.child("user_status").getValue().toString();


                                        viewHolder.setUser_name(userName);
                                        viewHolder.setUser_thumb_image(thumbImage, getContext());
                                        viewHolder.setUser_status(userStatus);

                                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                String visit_user_id = getRef(position).getKey();
                                                Intent profile = new Intent(getContext(), ProfileActivity.class);
                                                profile.putExtra("visit_user_id",listUserId);
                                                startActivity(profile);

                                            }
                                        });



                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        myRequestList.setAdapter(firebaseRecyclerAdapter);
    }




    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public RequestViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUser_name(String userName) {
            TextView userNameDisplay = (TextView) mView.findViewById(R.id.requestProfileName);
            userNameDisplay.setText(userName);
        }

        public void setUser_thumb_image(final String thumbImage,final Context ctx) {
            final CircleImageView thumb_image = mView.findViewById(R.id.requestProfileImage);

            Picasso.with(ctx).load(thumbImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumbImage).placeholder(R.drawable.default_profile).into(thumb_image);

                        }
                    });
        }

        public void setUser_status(String userStatus) {
            TextView status = mView.findViewById(R.id.requestProfiletatus);
            status.setText(userStatus);
        }
    }



}
