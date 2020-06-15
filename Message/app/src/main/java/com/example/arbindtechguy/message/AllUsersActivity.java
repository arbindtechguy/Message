package com.example.arbindtechguy.message;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersActivity extends AppCompatActivity {
    private RecyclerView allUserList;
    private DatabaseReference allDatabaseUsersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        allUserList = findViewById(R.id.all_user_list);
        allUserList.setHasFixedSize(true);
        allUserList.setLayoutManager(new LinearLayoutManager(this));
        allDatabaseUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        allDatabaseUsersRef.keepSynced(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>(
                        AllUsers.class,
                        R.layout.all_users_display_layout,
                        AllUsersViewHolder.class,
                        allDatabaseUsersRef
                ) {
                    @Override
                    protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position) {
                        viewHolder.setUser_name(model.getUser_name());
                        viewHolder.setUser_status(model.getUser_status());
                        viewHolder.setUser_thumb_image(getApplicationContext(),model.getUser_thumb_image());

                        //for Click

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visit_user_id = getRef(position).getKey();
                                Intent profile = new Intent(AllUsersActivity.this, ProfileActivity.class);
                                profile.putExtra("visit_user_id",visit_user_id);
                                startActivity(profile);
                            }
                        });

                    }
                };

        allUserList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllUsersViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public AllUsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setUser_name(String user_name){
            TextView name = mView.findViewById(R.id.all_users_username);
            name.setText(user_name);
        }
        public void setUser_status(String user_status){
            TextView status = mView.findViewById(R.id.all_users_status);
            status.setText(user_status);
        }
        public void setUser_thumb_image(final Context ctx,final String user_thumb_image){
            final CircleImageView thumb_image = mView.findViewById(R.id.all_user_profile_image);

               Picasso.with(ctx).load(user_thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile)
                    .into(thumb_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(user_thumb_image).placeholder(R.drawable.default_profile).into(thumb_image);

                        }
                    });

        }

    }
}
