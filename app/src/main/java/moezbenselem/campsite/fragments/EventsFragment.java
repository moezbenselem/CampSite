package moezbenselem.campsite.fragments;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import moezbenselem.campsite.R;
import moezbenselem.campsite.dialogs.EventDialog;
import moezbenselem.campsite.entities.Event;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragment extends Fragment {

    EditText etDate, etTime, etLoc, etTopic, etEvent;
    String dateF = "", dateD = "", timeD = "", timeF = "", dateFinal = "";
    FirebaseAuth mAuth;
    DatabaseReference mDataBase, groupChatRef;
    DatePicker dtp;
    TimePicker timep;
    Calendar newCalendar;
    RecyclerView recyclerView;
    CardView card_new;
    RelativeLayout layoutContenu;
    LinearLayout layoutHeader;
    Button btDate, btTime, btEffectuer, btPlus, btAnnuler;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            mAuth = FirebaseAuth.getInstance();
            mDataBase = FirebaseDatabase.getInstance().getReference().child("Events");
            groupChatRef = FirebaseDatabase.getInstance().getReference().child("GroupChat");

            card_new = getView().findViewById(R.id.card_new_event);
            btDate = card_new.findViewById(R.id.btDate);
            btTime = card_new.findViewById(R.id.btTime);
            btPlus = card_new.findViewById(R.id.btPlus);
            btAnnuler = card_new.findViewById(R.id.bt_annuler);
            btEffectuer = card_new.findViewById(R.id.bt_post);
            layoutContenu = card_new.findViewById(R.id.layout_contenu);
            layoutHeader = card_new.findViewById(R.id.layout_header);

            etDate = card_new.findViewById(R.id.etDate);
            etTime = card_new.findViewById(R.id.etTime);
            etEvent = card_new.findViewById(R.id.etEvent);
            etTopic = card_new.findViewById(R.id.etTopic);
            etLoc = card_new.findViewById(R.id.etLoc);

            card_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layoutHeader.setVisibility(View.GONE);
                    layoutContenu.setVisibility(View.VISIBLE);

                }
            });

            btAnnuler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layoutHeader.setVisibility(View.VISIBLE);
                    layoutContenu.setVisibility(View.GONE);
                }
            });

            btEffectuer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postEvent();
                }
            });

            Calendar c = Calendar.getInstance();
            final DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    Calendar indate = Calendar.getInstance();
                    indate.set(year, month, dayOfMonth);

                    etDate.setText(DateFormat.format("EEEE", indate) + " " + dayOfMonth + "-" + (month + 1) + "-" + year);

                    System.out.println(DateFormat.format("EEEE", indate));

                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

            btDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerDialog.show();
                }
            });


            btTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            timeD = selectedHour + ":" + selectedMinute + ":00";
                            if (selectedHour < 10)
                                timeD = "0" + selectedHour + ":" + selectedMinute + ":00";
                            if (selectedMinute < 10)
                                timeD = selectedHour + ":0" + selectedMinute + ":00";
                            if (selectedMinute < 10 && selectedHour < 10)
                                timeD = "0" + selectedHour + ":0" + selectedMinute + ":00";

                            System.out.println("Time Fin" + timeD);
                            etTime.setText(timeD);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Pick Time : ");
                    mTimePicker.show();

                }
            });

        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        recyclerView = view.findViewById(R.id.recycler_events);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    public void postEvent() {

        String place, date, topic, name, time, admin;

        place = etLoc.getText().toString();
        date = etDate.getText().toString();
        topic = etTopic.getText().toString();
        name = etEvent.getText().toString();
        time = etTime.getText().toString();
        admin = mAuth.getCurrentUser().getDisplayName();

        HashMap<String, String> eventMap = new HashMap();

        eventMap.put("location", place);
        eventMap.put("date", date);
        eventMap.put("time", time);
        eventMap.put("topic", topic);
        eventMap.put("name", name);
        eventMap.put("admin", admin);

        String id = mDataBase.push().getKey();

        mDataBase.child(id).setValue(eventMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    etDate.setText("");
                    etLoc.setText("");
                    etTime.setText("");
                    etTopic.setText("");
                    etEvent.setText("");
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(mAuth.getCurrentUser().getDisplayName(), null);
                    groupChatRef.updateChildren(updates);
                    btAnnuler.performClick();
                } else
                    task.getException().printStackTrace();

            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event, eventsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Event, eventsViewHolder>(Event.class,
                        R.layout.card_event,
                        eventsViewHolder.class,
                        mDataBase) {
                    @Override
                    protected void populateViewHolder(eventsViewHolder viewHolder, final Event model, int position) {
                        model.setId(getRef(position).getKey());

                        viewHolder.tvEvent.setText("Event : " + model.getName());
                        viewHolder.tvLoc.setText("Location : " + model.getLocation());
                        viewHolder.tvTopic.setText("Topic : " + model.getTopic());
                        viewHolder.tvAdmin.setText("Created By : " + model.getAdmin());
                        viewHolder.tvDate.setText("Date : " + model.getDate());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new EventDialog(getActivity(), model).show();

                            }
                        });

                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }

    public static class eventsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView tvDate, tvLoc, tvTopic, tvEvent, tvAdmin;


        public eventsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tvAdmin = itemView.findViewById(R.id.tv_admin);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvEvent = itemView.findViewById(R.id.tv_event);
            tvLoc = itemView.findViewById(R.id.tv_location);
            tvTopic = itemView.findViewById(R.id.tv_topic);

        }

    }


}
