package moezbenselem.campsite.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import moezbenselem.campsite.R;
import moezbenselem.campsite.adapters.EventsAdapterSettings;
import moezbenselem.campsite.entities.Event;

public class SettingsActivity extends AppCompatActivity {
    public static HashMap<String, Boolean> resultEvents = new HashMap();
    RecyclerView recyclerEvents;
    FirebaseAuth mAuth;
    ArrayList<Event> listEvents;
    DatabaseReference eventsRef, myEventsRef, usersRef;
    EventsAdapterSettings adapter;
    RadioGroup radioGroup;
    RadioButton radioAll, radioNone, radioCustom;
    boolean selectedAll = true;
    Button btSave, btReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        radioAll = findViewById(R.id.radioAll);
        radioNone = findViewById(R.id.radioNobody);
        radioCustom = findViewById(R.id.radioCustom);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == radioAll.getId()) {
                    recyclerEvents.setVisibility(View.GONE);
                    setEvents(true);
                } else if (checkedId == radioNone.getId()) {
                    recyclerEvents.setVisibility(View.GONE);
                    setEvents(false);
                } else {
                    recyclerEvents.setVisibility(View.VISIBLE);
                }
            }
        });

        btSave = findViewById(R.id.save_set);
        btReset = findViewById(R.id.bt_reset);

        recyclerEvents = findViewById(R.id.recycler_events_track);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerEvents.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        myEventsRef = FirebaseDatabase.getInstance().getReference().child("My_Events").child(mAuth.getCurrentUser().getDisplayName()).getRef();

        eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Resetting GPS data")
                        .setMessage("Are you sure you want to reset you GPS data ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("Tracking").child(mAuth.getCurrentUser().getDisplayName())
                                        .child("data").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Data Cleared !", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioAll.isChecked()) {
                    for (HashMap.Entry<String, Boolean> entry : resultEvents.entrySet()) {
                        resultEvents.put(entry.getKey(), true);
                    }
                } else if (radioNone.isChecked()) {
                    for (HashMap.Entry<String, Boolean> entry : resultEvents.entrySet()) {
                        resultEvents.put(entry.getKey(), false);
                    }
                }

                HashMap<String, Object> map = new HashMap<>();
                map.putAll(resultEvents);
                btSave.setClickable(false);
                myEventsRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btSave.setClickable(true);
                        if (task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Changes Saved !", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(getApplicationContext(), "Failed to Save Changes !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        myEventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    resultEvents.clear();
                    for (DataSnapshot event : dataSnapshot.getChildren()) {
                        resultEvents.put(event.getKey(), event.getValue().equals(true));
                    }
                    listEvents = new ArrayList<>();
                    eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {

                                for (DataSnapshot event : dataSnapshot.getChildren()) {
                                    Event e = event.getValue(Event.class);
                                    e.setId(event.getKey());
                                    System.out.println(event.getKey());
                                    if (resultEvents.containsKey(event.getKey()))
                                        listEvents.add(e);
                                }
                                adapter = new EventsAdapterSettings(listEvents, resultEvents, SettingsActivity.this);
                                recyclerEvents.setAdapter(adapter);
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

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void setEvents(boolean all) {
        if (all) {
            selectedAll = true;
        } else {
            selectedAll = false;
        }
    }

}