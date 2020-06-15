package com.example.arbindtechguy.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by arbindtechguy on 11/3/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private FirebaseAuth mAuth;
    private FirebaseDatabase userDatabaseRef;
    private List<Messages> userMessagesList;

    public MessageAdapter(List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View V = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_layout_of_userss, parent,false);
        return new MessageViewHolder(V);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Messages messages = userMessagesList.get(position);
        holder.messagesText.setText(messages.getMessage());



    }

    @Override
    public int getItemCount() {

        return userMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messagesText;
        public CircleImageView userprofileImage;

        public MessageViewHolder(View view){
            super(view);
            messagesText = view.findViewById(R.id.message_text);
            userprofileImage = view.findViewById(R.id.messages_profile_image);


        }
    }
}
