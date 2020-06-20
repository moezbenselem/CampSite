package moezbenselem.campsite.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import me.leolin.shortcutbadger.ShortcutBadger;
import moezbenselem.campsite.FirebaseMessagingService;
import moezbenselem.campsite.GetTimeAgo;
import moezbenselem.campsite.R;
import moezbenselem.campsite.adapters.MessageAdapter;
import moezbenselem.campsite.entities.Message;

public class ChatActivity extends AppCompatActivity {

    private static final int SELECT_VIDEO = 2;
    public static int messages_numer = 10, GALLERY_PICK = 1321;
    String chatUser, user_name;
    DatabaseReference rootRef, messagesRef;
    StorageReference imageRef;
    StorageReference videoRef;


    TextView tvOnline;
    EditText etMessage;
    Button btSend, btImage, btVideo;
    ImageView onlineImage;
    CircleImageView userImageView;
    FirebaseAuth mAuth;
    String current_user_id;
    RecyclerView recyclerMessages;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<Message> messages;
    LinearLayoutManager linearLayoutManager;
    MessageAdapter adapter;
    int current_page = 1;
    boolean fromRefresh = false;
    DatabaseReference userRef;
    private Uri selectedVideoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if(getIntent().getExtras().getString("intent")!=null)
            if(FirebaseMessagingService.badgeCount>0){
                FirebaseMessagingService.badgeCount--;
                ShortcutBadger.applyCount(getApplicationContext(),FirebaseMessagingService.badgeCount);
            }



        try {
            mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getDisplayName());
                userRef.child("online").setValue(true);
                userRef.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());
            }


            current_user_id = mAuth.getCurrentUser().getDisplayName();

            //Get the default actionbar instance
            androidx.appcompat.app.ActionBar mActionBar = getSupportActionBar();
            mActionBar.setDisplayShowHomeEnabled(false);
            mActionBar.setDisplayShowTitleEnabled(false);

//Initializes the custom action bar layout
            LayoutInflater mInflater = LayoutInflater.from(this);
            View mCustomView = mInflater.inflate(R.layout.chat_toolbar, null);
            mActionBar.setCustomView(mCustomView);
            mActionBar.setDisplayShowCustomEnabled(true);

            mActionBar.setDisplayShowHomeEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(true);


            TextView tvName = findViewById(R.id.tv_appbar_name);
            tvOnline = findViewById(R.id.tv_appbar_online);
            onlineImage = findViewById(R.id.image_appbar_online);
            userImageView = findViewById(R.id.icon_app_bar);

            chatUser = getIntent().getStringExtra("name");
            user_name = getIntent().getStringExtra("name");

            if (chatUser == null) {
                chatUser = FirebaseMessagingService.theSender;
                user_name = FirebaseMessagingService.theSender;
            }


            DatabaseReference uRef = FirebaseDatabase.getInstance().getReference().child("Users");
            uRef.child(chatUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    Picasso.with(getApplicationContext()).load(image).placeholder(R.drawable.male_user).into(userImageView);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            userImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent toProfile = new Intent(ChatActivity.this, UserActivity.class);
                    toProfile.putExtra("name", chatUser);
                    startActivity(toProfile);

                }
            });

            swipeRefreshLayout = findViewById(R.id.swipeMessage);

            btSend = findViewById(R.id.bt_send);
            btImage = findViewById(R.id.bt_image);
            btVideo = findViewById(R.id.bt_video);

            etMessage = findViewById(R.id.input);


            tvName.setText(user_name);

            recyclerMessages = findViewById(R.id.recycler_messages);
            linearLayoutManager = new LinearLayoutManager(this);

            linearLayoutManager.setStackFromEnd(true);
            recyclerMessages.setLayoutManager(linearLayoutManager);

            rootRef = FirebaseDatabase.getInstance().getReference();
            imageRef = FirebaseStorage.getInstance().getReference();

            loadMessages();

            rootRef.child("Users").child(chatUser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String online = "true";
                    if (dataSnapshot.hasChild("online")) {
                        online = dataSnapshot.child("online").getValue().toString();
                    }
                    String thumb = dataSnapshot.child("thumb_image").getValue().toString();
                    if (online.equals("true")) {
                        tvOnline.setText("Online");
                        onlineImage.setImageResource(R.drawable.online);
                    } else {
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        long time = Long.parseLong(online);

                        String lastTime = getTimeAgo.getTimeAgo(time, ChatActivity.this);

                        tvOnline.setText(lastTime);

                        onlineImage.setImageResource(R.drawable.offline);

                        Picasso.with(ChatActivity.this).load(thumb).placeholder(R.drawable.male_user).into(userImageView);
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            btSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessage();

                }
            });

            btImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(ChatActivity.this);

                }
            });
            btVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, SELECT_VIDEO);

                }
            });

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    current_page++;
                    fromRefresh = true;
                    loadMessages();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMessages() {

        try {
            System.out.println("from load message !!!");
            System.out.println("current page === " + current_page);
            messagesRef = FirebaseDatabase.getInstance().getReference().child("messages").child(current_user_id).child(chatUser);
            Query messageQuery = messagesRef.limitToLast(current_page * messages_numer);
            messages = new ArrayList<Message>();
            messageQuery
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                            try {

                                System.out.println("count ==== " + dataSnapshot.getChildrenCount());
                                System.out.println("datasnapshot === " + dataSnapshot.getValue());


                                //System.out.println(dataSnapshot1.getValue(Message.class).getMessage());
                                Message message = dataSnapshot.getValue(Message.class);

                                System.out.println("message from objet message ===" + message.getMessage());
                                messages.add(message);

                                if (messages.get(messages.size() - 1).getFrom().equals(chatUser)) {
                                    System.out.println("in test from");
                                    Map chatAddMap = new HashMap();
                                    chatAddMap.put("seen", true);
                                    chatAddMap.put("time", ServerValue.TIMESTAMP);

                                    Map chatUserMap = new HashMap();
                                    chatUserMap.put("Chat/" + current_user_id + "/" + chatUser, chatAddMap);
                                    chatUserMap.put("Chat/" + chatUser + "/" + current_user_id, chatAddMap);

                                    rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            System.out.println("in update seen to true");
                                            if (fromRefresh) {
                                                swipeRefreshLayout.setRefreshing(false);
                                            } else {
                                                swipeRefreshLayout.setRefreshing(false);
                                                recyclerMessages.scrollToPosition(adapter.getItemCount() - 1);
                                            }

                                        }
                                    });
                                }

                                adapter = new MessageAdapter(messages, ChatActivity.this);
                                recyclerMessages.setAdapter(adapter);
                                if (fromRefresh) {
                                    swipeRefreshLayout.setRefreshing(false);
                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    recyclerMessages.scrollToPosition(adapter.getItemCount() - 1);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        try {

            btSend.setEnabled(false);
            String message = etMessage.getText().toString();
            if (!message.isEmpty()) {

                String current_user_ref = "messages/" + current_user_id + "/" + chatUser;
                String chat_user_ref = "messages/" + chatUser + "/" + current_user_id;

                DatabaseReference message_push_ref = rootRef.child("messages").child(current_user_id).child(chatUser).push();
                String push_id = message_push_ref.getKey();
                Map messageMap = new HashMap();

                messageMap.put("message", message);
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("from", current_user_id);

                Map messageUserMap = new HashMap();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if (databaseError == null) {
                            etMessage.setText("");
                            btSend.setEnabled(true);
                            Map chatAddMap = new HashMap();
                            chatAddMap.put("seen", false);
                            chatAddMap.put("time", ServerValue.TIMESTAMP);

                            Map chatUserMap = new HashMap();
                            chatUserMap.put("Chat/" + current_user_id + "/" + chatUser, chatAddMap);
                            chatUserMap.put("Chat/" + chatUser + "/" + current_user_id, chatAddMap);

                            rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (fromRefresh) {
                                        swipeRefreshLayout.setRefreshing(false);
                                    } else {
                                        swipeRefreshLayout.setRefreshing(false);
                                        recyclerMessages.scrollToPosition(adapter.getItemCount() - 1);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ChatActivity.this, "Message Not Sent !", Toast.LENGTH_LONG).show();
                            btSend.setEnabled(true);
                        }

                        if (fromRefresh) {
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            recyclerMessages.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }
                });


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                System.out.println("inside result if");

                if (resultCode == RESULT_OK) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();

                    final ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("Sending ...");
                    progressDialog.setMessage("Please Wait, Sending the Picture ...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    try {
                        File thumb_file = new File(resultUri.getPath());
                        Bitmap thumb_image = new Compressor(this).setMaxHeight(200).setMaxWidth(200)
                                .setQuality(75)
                                .compressToBitmap(thumb_file);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        thumb_image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        final byte[] thumb_byte = baos.toByteArray();

                        final String current_user = "messages/" + current_user_id + "/" + chatUser;
                        final String current_chat = "messages/" + chatUser + "/" + current_user_id;

                        DatabaseReference userMPush = rootRef.child("messages").child(current_user_id).child(chatUser).push();
                        final String push_key = userMPush.getKey();

                        final StorageReference filePath = imageRef.child("message_images").child(push_key + ".jpg");

                        filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                                System.out.println("Upload success !");

                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(final Uri uri) {
                                        // Got the download URL for 'users/me/profile.png'

                                        Map messageMap = new HashMap();
                                        messageMap.put("message", uri.toString());
                                        messageMap.put("time", ServerValue.TIMESTAMP);
                                        messageMap.put("seen", false);
                                        messageMap.put("type", "image");
                                        messageMap.put("from", current_user_id);


                                        Map messageUserMap = new HashMap();
                                        messageUserMap.put(current_user + "/" + push_key, messageMap);
                                        messageUserMap.put(current_chat + "/" + push_key, messageMap);

                                        rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                                if (databaseError == null) {
                                                    etMessage.setText("");
                                                    btSend.setEnabled(true);
                                                    progressDialog.dismiss();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ChatActivity.this, "Message Not Sent !", Toast.LENGTH_LONG).show();
                                                    btSend.setEnabled(true);
                                                }

                                            }
                                        });

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                exception.printStackTrace();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else if (resultCode == RESULT_OK) {
                if (requestCode == SELECT_VIDEO) {
                    selectedVideoPath = data.getData();
                    try {
                        if (selectedVideoPath == null) {
                            System.out.println("selected video path = null!");
                        } else {
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                            DatabaseReference userMPush = rootRef.child("messages").child(current_user_id).child(chatUser).push();

                            final String push_key = userMPush.getKey();

                            videoRef = storageRef.child("/videos").child(push_key);

                            //TODO: save the video in the db
                            uploadData(selectedVideoPath, push_key);
                        }
                    } catch (Exception e) {
                        //#debug
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadData(Uri videoUri, final String push_key) {
        if (videoUri != null) {
            UploadTask uploadTask = videoRef.putFile(videoUri);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Sending ...");
            progressDialog.setMessage("Please Wait, Sending the Video ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Upload Complete", Toast.LENGTH_SHORT).show();
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.dismiss();
                        videoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                // Got the download URL for 'users/me/profile.png'

                                final String current_user = "messages/" + current_user_id + "/" + chatUser;
                                final String current_chat = "messages/" + chatUser + "/" + current_user_id;

                                Map messageMap = new HashMap();
                                messageMap.put("message", uri.toString());
                                messageMap.put("time", ServerValue.TIMESTAMP);
                                messageMap.put("seen", false);
                                messageMap.put("type", "video");
                                messageMap.put("from", current_user_id);


                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user + "/" + push_key, messageMap);
                                messageUserMap.put(current_chat + "/" + push_key, messageMap);

                                rootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        if (databaseError == null) {
                                            etMessage.setText("");
                                            btSend.setEnabled(true);
                                            progressDialog.dismiss();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(ChatActivity.this, "Message Not Sent !", Toast.LENGTH_LONG).show();
                                            btSend.setEnabled(true);
                                        }

                                    }
                                });


                            }
                        });
                    }

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //updateProgress(taskSnapshot);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Nothing to upload", Toast.LENGTH_SHORT).show();
        }
    }
/*

    private void release() {
        if (mPlayer == null) {
            return;
        }
        // save current position and release player
        mCurrentMillis = mPlayer.getCurrentPosition();
        mPlayer.release();
        mPlayer = null;
    }
*/


    @Override
    protected void onPause() {
        //release();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        onSupportNavigateUp();
        //this.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (MainActivity.player != null)
            MainActivity.player.release();
        finish();
        return true;
    }
}


