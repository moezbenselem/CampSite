package moezbenselem.campsite.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import moezbenselem.campsite.R;
import moezbenselem.campsite.adapters.InviteUserAdapter;
import moezbenselem.campsite.entities.User;

public class InviteActivity extends AppCompatActivity {

    public static String event_id, event_name;
    RecyclerView recyclerView;
    ArrayList<User> listFriends;
    DatabaseReference mDatabaseRef, usersRef;
    InviteUserAdapter adapter;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Bundle extra = getIntent().getExtras();
        event_name = extra.getString("event_name");
        event_id = extra.getString("event_id");

        getSupportActionBar().setTitle("Invite to : " + event_name);

        recyclerView = findViewById(R.id.recycler_invite_friends);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mAuth.getCurrentUser().getDisplayName());
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        listFriends = new ArrayList<>();
        adapter = new InviteUserAdapter(listFriends, getApplicationContext());
        recyclerView.setAdapter(adapter);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String friendId = data.getKey();
                        usersRef.child(friendId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                listFriends.add(user);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
