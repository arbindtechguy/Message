package com.example.arbindtechguy.message;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.solver.widgets.Snapshot;
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
public class FriendsFragment extends Fragment {
    private RecyclerView friends_list;
    private DatabaseReference friendsReference;
    private DatabaseReference userReference;

    private FirebaseAuth mAuth;
    String user_id;
    private View myMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        friends_list = (RecyclerView) myMainView.findViewById(R.id.friends_list);
        user_id = mAuth.getCurrentUser().getUid();
        friendsReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);
        friends_list.setLayoutManager(new LinearLayoutManager(getContext()));






        return myMainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<User_friends,FriendsViewHolder > firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User_friends, FriendsViewHolder>(
                User_friends.class,
                R.layout.all_users_display_layout,
                FriendsViewHolder.class,
                friendsReference
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, User_friends model,final int position) {
                viewHolder.setDate(model.getDate());
                final String listUserId = getRef(position).getKey();
                userReference.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                         final String userName = dataSnapshot.child("user_name").getValue().toString();
                         String thumbImage = dataSnapshot.child("user_thumb_image").getValue().toString();


                         if (dataSnapshot.hasChild("online")){
                             String online_status = (String) dataSnapshot.child("online").getValue().toString();
                             viewHolder.setUserOnline(online_status);
                        }

                         viewHolder.setUserName(userName);
                         viewHolder.setThumbImage(thumbImage, getContext());
                         viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View view) {
                                 CharSequence options[] = new CharSequence[]{
                                         userName + "'s Profile",
                                         "Send Message"
                                 };



                                 AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                 builder.setTitle("Select Options");
                                 builder.setItems(options, new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialogInterface, int position) {
                                         Toast.makeText(getContext(),"Clicked", Toast.LENGTH_SHORT).show();
                                         if (position == 0){
                                             Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                             profileIntent.putExtra("visit_user_id",listUserId);
                                             startActivity(profileIntent);
                                         }
                                         if (position == 1){
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
        };

        friends_list.setAdapter(firebaseRecyclerAdapter);
    }



    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setDate(String date){
            TextView sinceFriendsDate = mView.findViewById(R.id.all_users_status);
            sinceFriendsDate.setText(date);
        }
        public void setUserName(String userName){
            TextView userNameDisplay = mView.findViewById(R.id.all_users_username);
            userNameDisplay.setText(userName);
        }

        public void setThumbImage(final String thumbImage,final Context ctx) {
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

        public void setUserOnline(String online_status) {
            ImageView onlineStatusView = mView.findViewById(R.id.online_status);

            if (online_status.equals("true")){
                onlineStatusView.setVisibility(View.VISIBLE);
            }
            else {
                onlineStatusView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
