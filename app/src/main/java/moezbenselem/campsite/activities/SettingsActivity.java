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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import moezbenselem.campsite.R;
import moezbenselem.campsite.adapters.EventsAdapter;
import moezbenselem.campsite.entities.Event;

public class SettingsActivity extends AppCompatActivity {

    RecyclerView recyclerFreinds;
    FirebaseAuth mAuth;
    ArrayList<Event> listEvents;
    ArrayList<String> listMyEvents;
    DatabaseReference eventsRef, myEventsRef, usersRef;


    EventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        recyclerFreinds = findViewById(R.id.recycler_events_track);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerFreinds.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        myEventsRef = FirebaseDatabase.getInstance().getReference().child("My_Events").child(mAuth.getCurrentUser().getDisplayName()).getRef();

        eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        listMyEvents = new ArrayList<String>();
        myEventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    listMyEvents.clear();
                    for (DataSnapshot event : dataSnapshot.getChildren()) {
                        listMyEvents.add(event.getKey());
                    }
                    listEvents = new ArrayList<>();
                    eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {

                                for (DataSnapshot event : dataSnapshot.getChildren()) {
                                    listEvents.add(event.getValue(Event.class));
                                }
                                adapter = new EventsAdapter(listEvents, SettingsActivity.this);
                                recyclerFreinds.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        System.out.println("key " + child.getKey());

                        Query Query = usersRef.child(child.getKey()).orderByChild("online");
                        if (child.getKey() == myEventsRef.toString())
                            System.out.println("child key " + child.getKey());

                        /*userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                try {


                                    User user = dataSnapshot.getValue(User.class);
                                    System.out.println(user.email);
                                    System.out.println(user.username);
                                    listFriends.add(user);

                                    adapter = new FriendsAdapter(listFriends, SettingsActivity.this);
                                    recyclerFreinds.setAdapter(adapter);
                                }catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
*/


                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
/*

.addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        listFriends = new ArrayList<User>();

        if (dataSnapshot.hasChildren()) {

        for (DataSnapshot child : dataSnapshot.getChildren()) {

        System.out.println("key " + child.getKey());

        Query userQuery = usersRef.child(child.getKey()).orderByChild("online");

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        try {


        User user = dataSnapshot.getValue(User.class);
        System.out.println(user.email);
        System.out.println(user.username);
        listFriends.add(user);

        adapter = new FriendsAdapter(listFriends, SettingsActivity.this);
        recyclerFreinds.setAdapter(adapter);
        }catch (Exception e)
        {
        e.printStackTrace();
        }
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
        });*/
