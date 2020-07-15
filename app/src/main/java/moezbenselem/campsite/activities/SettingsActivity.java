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
    ArrayList<Event> listEvents = new ArrayList<>();
    DatabaseReference eventsRef, myEventsRef,groupChatRef, usersRef,settingsRef;
    EventsAdapterSettings adapter;
    RadioGroup radioGroup;
    RadioButton radioAll, radioNone, radioCustom;
    boolean selectedAll = true;
    Button btSave, btReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mAuth = FirebaseAuth.getInstance();

        radioAll = findViewById(R.id.radioAll);
        radioNone = findViewById(R.id.radioNobody);
        radioCustom = findViewById(R.id.radioCustom);
        radioGroup = findViewById(R.id.radioGroup);

        adapter = new EventsAdapterSettings(listEvents, resultEvents, SettingsActivity.this);

        settingsRef = FirebaseDatabase.getInstance().getReference().child("Tracking").child(mAuth.getCurrentUser().getDisplayName()).child("settings");

        settingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    if(dataSnapshot.child("allowed").getValue() != null){
                        String data = dataSnapshot.child("allowed").getValue().toString();
                        if(data.equalsIgnoreCase("none"))
                            radioNone.setChecked(true);
                        else if(data.equalsIgnoreCase("custom"))
                            radioCustom.setChecked(true);
                        else
                            radioAll.setChecked(true);
                    }else{
                        HashMap m = new HashMap<String, Object>();
                        m.put("allowed","all");
                        settingsRef.updateChildren(m).addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                }catch (Exception e){

                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        myEventsRef = FirebaseDatabase.getInstance().getReference().child("My_Events").child(mAuth.getCurrentUser().getDisplayName()).getRef();

        groupChatRef = FirebaseDatabase.getInstance().getReference().child("GroupChat");

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
        final HashMap<String,Object> mapAllowed = new HashMap<>();
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapAllowed.clear();
                if (radioAll.isChecked()) {
                    for (HashMap.Entry<String, Boolean> entry : resultEvents.entrySet()) {
                        resultEvents.put(entry.getKey(), true);
                    }
                    mapAllowed.put("allowed","all");
                    settingsRef.updateChildren(mapAllowed);
                } else if (radioNone.isChecked()) {
                    for (HashMap.Entry<String, Boolean> entry : resultEvents.entrySet()) {
                        resultEvents.put(entry.getKey(), false);
                    }
                    mapAllowed.put("allowed","none");
                    settingsRef.updateChildren(mapAllowed);
                }else if (radioCustom.isChecked()){
                    mapAllowed.put("allowed","Custom");
                    settingsRef.updateChildren(mapAllowed);
                }

                HashMap<String, Object> map = new HashMap<>();
                map.putAll(resultEvents);
                btSave.setClickable(false);
                myEventsRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        btSave.setClickable(true);
                        HashMap<String,Object> valueMap = new HashMap();
                        if (task.isSuccessful()){
                            //updating in GroupChat
                            for (HashMap.Entry<String, Boolean> entry : resultEvents.entrySet()) {
                                valueMap.clear();
                                valueMap.put(mAuth.getCurrentUser().getDisplayName(),entry.getValue());
                                groupChatRef.child(entry.getKey()).child("members").updateChildren(valueMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Changes Saved !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }else
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
                    listEvents.clear();
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

    }

    public void setEvents(boolean all) {
        if (all) {
            selectedAll = true;
        } else {
            selectedAll = false;
        }
    }

}