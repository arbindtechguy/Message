package com.example.arbindtechguy.message;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private View myMainView;
    private RecyclerView myChatList;

    private DatabaseReference friendsReference;
    private DatabaseReference userReference;

    private FirebaseAuth mAuth;
    String user_id;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_chats, container, false);
        myChatList = (RecyclerView) myMainView.findViewById(R.id.chats_list);
        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();

        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");

        myChatList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        myChatList.setLayoutManager(linearLayoutManager);




        // Inflate the layout for this fragment
        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Chats,ChatsFragment.ChatsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Chats,ChatsFragment.ChatsViewHolder>(
                Chats.class,
                R.layout.all_users_display_layout,
                ChatsFragment.ChatsViewHolder.class,
                friendsReference
        ) {
            @Override
            protected void populateViewHolder(final ChatsFragment.ChatsViewHolder viewHolder, final Chats model,final int position) {
                 final String listUserId = getRef(position).getKey();
               // Toast.makeText(getContext(), listUserId, Toast.LENGTH_SHORT).show();
                userReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("user_name").getValue().toString();
                        String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("user_status").getValue().toString();

                        if (dataSnapshot.hasChild("online")){
                            String online_status = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(online_status);
                        }

                        viewHolder.setUserName(userName);
                        viewHolder.setThumbImage(thumbImage, getContext());

                        viewHolder.setUserStatus(userStatus);
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (dataSnapshot.child("online").exists()){
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id",listUserId);
                                    chatIntent.putExtra("userName",userName);
                                    startActivity(chatIntent);
                                }
                                else{
                                    userReference.child(listUserId).child("online").setValue(ServerValue.TIMESTAMP)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                    chatIntent.putExtra("visit_user_id",listUserId);
                                                    chatIntent.putExtra("userName",userName);
                                                    startActivity(chatIntent);

                                                }
                                            });
                                }
                            }
                        });



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        myChatList.setAdapter(firebaseRecyclerAdapter);

    }




    public static class ChatsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        private void setUserStatus(String userStatus) {
            TextView user_status = mView.findViewById(R.id.all_users_status);
            user_status.setText(userStatus);

        }

        public void setUserOnline(String online_status) {
            ImageView onlineStatusView = mView.findViewById(R.id.online_status);

            if (online_status.equals("true")){
                onlineStatusView.setVisibility(View.VISIBLE);
            }
            else {
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
        }
        public void setUserName(String userName) {
            TextView userNameDisplay = mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }

        public void setThumbImage(final String thumbImage, final Context ctx) {
            final CircleImageView thumb_image = mView.findViewById(R.id.all_user_profile_image);

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


    }


}
