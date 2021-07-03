package moezbenselem.campsite;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

public class UserActivity extends AppCompatActivity {

    int holderResource;
    TextView tvDisplay, tvStatus, tvFriends;
    Button btRequest, btDecline;
    ImageView imageView;
    String friends_state = "not_friend";
    DatabaseReference friendsDatabaseRef;
    DatabaseReference userDatabaseRef;
    DatabaseReference requestDatabaseRef;
    DatabaseReference notifDatabaseRef;
    String status, image, gender;
    FirebaseUser currentUser;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    String name;
    DatabaseReference userRef;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mAuth = FirebaseAuth.getInstance();


        try {


            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getDisplayName());
            userRef.child("online").setValue(true);
            userRef.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());


            swipeRefreshLayout = findViewById(R.id.refresh_layout_user);

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading User Data");
            progressDialog.setMessage("Please wait while loading user infos !");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            name = getIntent().getExtras().getString("name");
            if (name == null) {
                name = FirebaseMessagingService.theSender;

            }

            System.out.println("intent extra name : " + name);
            getSupportActionBar().setTitle("Profile : " + name);


            tvDisplay = findViewById(R.id.profile_tvName);
            tvStatus = findViewById(R.id.profile_tvStatus);
            tvFriends = findViewById(R.id.profile_tvFriends);
            btRequest = findViewById(R.id.profile_btRequest);
            btDecline = findViewById(R.id.profile_btDecline);
            btDecline.setVisibility(View.INVISIBLE);
            imageView = findViewById(R.id.profile_imageView);


            userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(name);
            friendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");
            requestDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_request");
            notifDatabaseRef = FirebaseDatabase.getInstance().getReference().child("notifications");

            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            fetchData();
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    fetchData();

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }


    public void fetchData() {

        requestDatabaseRef.child(currentUser.getDisplayName()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.hasChild(name)) {
                    progressDialog.show();
                    String req_type = dataSnapshot.child(name).child("request_type").getValue().toString();

                    if (req_type.equals("recieved")) {

                        System.out.println("you have recieved a friend request !");
                        friends_state = "req_recieved";
                        btRequest.setText("ACCEPT REQUEST");
                        btDecline.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    } else if (req_type.equals("sent")) {
                        System.out.println("you have sent a friend request !");
                        friends_state = "req_sent";
                        btRequest.setText("Cancel REQUEST");
                        progressDialog.dismiss();
                    }

                } else {

                    friendsDatabaseRef.child(currentUser.getDisplayName()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            progressDialog.show();
                            if (dataSnapshot.hasChild(name)) {

                                btRequest.setEnabled(true);
                                friends_state = "friends";
                                System.out.println("this is a friend!");
                                btDecline.setVisibility(View.INVISIBLE);
                                btRequest.setText("Unfriend " + name);
                                progressDialog.dismiss();
                            } else
                                progressDialog.dismiss();

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                            progressDialog.show();
                            btRequest.setEnabled(true);
                            friends_state = "not_friend";
                            System.out.println("this is not a friend!");
                            btDecline.setVisibility(View.INVISIBLE);
                            btRequest.setText("Send Request");
                            progressDialog.dismiss();
                        }


                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                progressDialog.show();
                btRequest.setEnabled(true);
                friends_state = "not_friend";
                System.out.println("this is not a friend!");
                btDecline.setVisibility(View.INVISIBLE);
                btRequest.setText("Send Request");
                progressDialog.dismiss();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //progressDialog.show();
                image = dataSnapshot.child("image").getValue().toString();
                status = dataSnapshot.child("status").getValue().toString();
                gender = dataSnapshot.child("gender").getValue().toString();

                tvDisplay.setText(name);
                tvStatus.setText(status);
                //Picasso.with(this).load(image).placeholder(R.drawable.male_avatar).into(imageView);
                if (gender.equalsIgnoreCase("male"))
                    holderResource = R.drawable.male_user;
                else if (gender.equalsIgnoreCase("female"))
                    holderResource = R.drawable.female_user;

                Picasso.with(UserActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(holderResource).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        if (gender.equalsIgnoreCase("male"))
                            Picasso.with(UserActivity.this).load(image).placeholder(holderResource).into(imageView);
                        else if (gender == "female")
                            Picasso.with(UserActivity.this).load(image).placeholder(holderResource).into(imageView);
                    }
                });
                System.out.println("status == " + status);
                System.out.println("image == " + image);
                requestDatabaseRef.child(currentUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(name)) {
                            progressDialog.show();
                            String req_type = dataSnapshot.child(name).child("request_type").getValue().toString();

                            if (req_type.equals("recieved")) {

                                System.out.println("you have recieved a friend request !");
                                friends_state = "req_recieved";
                                btRequest.setText("ACCEPT REQUEST");
                                btDecline.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            } else if (req_type.equals("sent")) {
                                System.out.println("you have sent a friend request !");
                                friends_state = "req_sent";
                                btRequest.setText("Cancel REQUEST");
                                progressDialog.dismiss();
                            }


                        } else {

                            friendsDatabaseRef.child(currentUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    progressDialog.show();
                                    if (dataSnapshot.hasChild(name)) {

                                        btRequest.setEnabled(true);
                                        friends_state = "friends";
                                        System.out.println("this is a friend!");
                                        btDecline.setVisibility(View.INVISIBLE);
                                        btRequest.setText("Unfriend " + name);
                                        progressDialog.dismiss();
                                    } else
                                        progressDialog.dismiss();

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


        btDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (friends_state.equalsIgnoreCase("req_recieved")) {

                    final String currentDate = getDateTime();


                    requestDatabaseRef.child(currentUser.getDisplayName()).child(name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            requestDatabaseRef.child(name).child(currentUser.getDisplayName()).removeValue();
                            Snackbar.make(btRequest, "Friend Request Declined !", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            btRequest.setEnabled(true);
                            btRequest.setText("Send Request");
                            friends_state = "not_friend";
                            btDecline.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }

        });


        btRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btRequest.setEnabled(false);
                if (friends_state.equalsIgnoreCase("not_friend")) {

                    requestDatabaseRef.child(currentUser.getDisplayName()).child(name).child("request_type").setValue("sent")
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    requestDatabaseRef.child(name).child(currentUser.getDisplayName()).child("request_type").setValue("recieved")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Snackbar.make(btRequest, "Friend Request Sent !", Snackbar.LENGTH_LONG)
                                                            .setAction("Action", null).show();

                                                    final String currentDate = getDateTime();
                                                    btRequest.setEnabled(true);
                                                    friends_state = "req_sent";
                                                    btRequest.setText("Cancel Request");

                                                    HashMap<String, String> notifData = new HashMap<String, String>();
                                                    notifData.put("from", currentUser.getDisplayName());
                                                    notifData.put("type", "request");
                                                    notifData.put("time", currentDate);
                                                    notifDatabaseRef.child(name).push().setValue(notifData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {

                                                        }
                                                    });

                                                }
                                            });

                                }
                            });
                }

                if (friends_state.equalsIgnoreCase("req_sent")) {


                    requestDatabaseRef.child(currentUser.getDisplayName()).child(name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            requestDatabaseRef.child(name).child(currentUser.getDisplayName()).removeValue();
                            Snackbar.make(btRequest, "Friend Request Cancelled !", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            btRequest.setEnabled(true);
                            friends_state = "not_friend";
                            btRequest.setText("SEND REQUEST");
                        }
                    });


                }

                if (friends_state.equalsIgnoreCase("friends")) {


                    friendsDatabaseRef.child(currentUser.getDisplayName()).child(name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendsDatabaseRef.child(name).child(currentUser.getDisplayName()).removeValue();
                            Snackbar.make(btRequest, name + " UNFRIENDED !", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            btRequest.setEnabled(true);
                            friends_state = "not_friend";
                            btRequest.setText("SEND REQUEST");
                        }
                    });


                }

                if (friends_state.equalsIgnoreCase("req_recieved")) {

                    final String currentDate = getDateTime();
                    friendsDatabaseRef.child(currentUser.getDisplayName()).child(name).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendsDatabaseRef.child(name).child(currentUser.getDisplayName()).child(("date")).setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    requestDatabaseRef.child(currentUser.getDisplayName()).child(name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            requestDatabaseRef.child(name).child(currentUser.getDisplayName()).removeValue();
                                            Snackbar.make(btRequest, "Friend Request Accepted !", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            btRequest.setEnabled(true);
                                            friends_state = "friends";
                                            btRequest.setText("Unfriend " + name);

                                        }
                                    });

                                }
                            });

                        }

                    });
                }

            }
        });

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }


    @Override
    protected void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getDisplayName());
        userRef.child("online").setValue(true);
        userRef.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());
    }


}
