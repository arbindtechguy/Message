package com.example.arbindtechguy.message;

import android.app.ActionBar;
import android.app.Activity;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends Activity  {
    private String messageReceiverId;
    private String messageReceiverName;
    private ConstraintLayout menubar;
    private TextView UserNameTitle;
    private TextView userLastSeen;
    private Toolbar ChatToolbar;
    private CircleImageView userChatProfileImage;
    private DatabaseReference rootRef;
    private ImageButton selectImageBtn;
    private ImageButton sendMessageBtn;
    private EditText input_message;
    private FirebaseAuth mAuth;
    private String messageSenderId;
    private RecyclerView userMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;


    static char[] chars = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', '!', '@',
            '#', '$', '%', '^', '&', '(', ')', '+',
            '-', '*', '/', '[', ']', '{', '}', '=',
            '<', '>', '?', '_', '"', '.', ',', ' '
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        messageReceiverId= getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("userName").toString();

        selectImageBtn = findViewById(R.id.select_image);
        sendMessageBtn = findViewById(R.id.send_message);

        userMessagesList = findViewById(R.id.messages_list_of_users);
        messageAdapter = new MessageAdapter(messagesList);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        rootRef = FirebaseDatabase.getInstance().getReference();
        UserNameTitle = findViewById(R.id.displayUserName);
        userLastSeen = findViewById(R.id.lastSeen);
        userChatProfileImage = findViewById(R.id.profilepic);
        input_message = findViewById(R.id.messageInput);
        mAuth = FirebaseAuth.getInstance();
        messageSenderId = mAuth.getCurrentUser().getUid();

        UserNameTitle.setText(messageReceiverName);
        FetchMessages();

        rootRef.child("Users").child(messageReceiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String online = dataSnapshot.child("online").getValue().toString();
                final String userThumb = dataSnapshot.child("user_thumb_image").getValue().toString();


                Picasso.with(ChatActivity.this).load(userThumb).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_profile)
                        .into(userChatProfileImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ChatActivity.this).load(userThumb).placeholder(R.drawable.default_profile).into(userChatProfileImage);

                            }
                        });

                if (online.equals("true")){
                    userLastSeen.setText("online");
                }
                else{
                    LastSeen getTime = new LastSeen();
                    long lastSeen = Long.parseLong(online);
                    String lastSeenDisplayTime = getTime.getTimeAgo(lastSeen, getApplicationContext()).toString();
                    userLastSeen.setText(lastSeenDisplayTime);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this, "Please Write message First...", Toast.LENGTH_SHORT).show();
                sendMessage();
            }
        });



    }
    private void FetchMessages() {
        rootRef.child("Messages").child(messageSenderId).child(messageReceiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Messages messages = dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
    private void sendMessage() {
        String messageText = input_message.getText().toString();
        if (TextUtils.isEmpty(messageText)){
            Toast.makeText(ChatActivity.this, "Please Write message First...", Toast.LENGTH_SHORT).show();
        }
        else{


            String text = messageText;
            int offset = 2;

            String encMessageText = encrypt(text, offset);
           // Toast.makeText(ChatActivity.this, enc, Toast.LENGTH_SHORT).show();





                String message_sender_ref = "Messages/" + messageSenderId + "/" + messageReceiverId;
                String message_receiver_ref = "Messages/" + messageReceiverId + "/" + messageSenderId;

                DatabaseReference userMessageKey = rootRef.child("Messages").child(messageSenderId)
                        .child(messageReceiverId).push();
                String message_push_id = userMessageKey.getKey();

                Map messageTextBody = new HashMap();
                messageTextBody.put("message",encMessageText);
                messageTextBody.put("seen", false);
                messageTextBody.put("type", "text");
                messageTextBody.put("time", ServerValue.TIMESTAMP);


                Map messageBodyDetails = new HashMap();
                messageBodyDetails.put(message_sender_ref + "/" + message_push_id, messageTextBody);

                messageBodyDetails.put(message_receiver_ref + "/" + message_push_id, messageTextBody);

                rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError!=null){
                            Log.d("chat log", databaseError.getMessage().toString());
                        }
                        input_message.setText("");
                    }
                });
        }
    }










































    //encription and decription


    static String encrypt(String text, int offset)
    {
        char[] plain = text.toCharArray();

        for (int i = 0; i < plain.length; i++) {
            for (int j = 0; j < chars.length; j++) {
                if (j <= chars.length - offset) {
                    if (plain[i] == chars[j]) {
                        plain[i] = chars[j + offset];
                        break;
                    }
                }
                else if (plain[i] == chars[j]) {
                    plain[i] = chars[j - (chars.length - offset + 1)];
                }
            }
        }
        return String.valueOf(plain);
    }


}
