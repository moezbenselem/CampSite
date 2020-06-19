package moezbenselem.campsite.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import moezbenselem.campsite.dialogs.DialogDecision;
import moezbenselem.campsite.entities.Event;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupEventsFragment extends Fragment {


    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    DatabaseReference mDataBase, eventsRef;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("My_Events").child(mAuth.getCurrentUser().getDisplayName());
        eventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_events);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
//
//        FirebaseRecyclerAdapter<Event, EventsFragment.eventsViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<Event, EventsFragment.eventsViewHolder>(Event.class,
//                        R.layout.card_event,
//                        EventsFragment.eventsViewHolder.class,
//                        mDataBase) {
//                    @Override
//                    protected void populateViewHolder(EventsFragment.eventsViewHolder viewHolder, final Event model, int position) {
//
//                        viewHolder.tvDate.append(model.getDate());
//                        viewHolder.tvEvent.append(model.getName());
//                        viewHolder.tvLoc.append(model.getLocation());
//                        viewHolder.tvTopic.append(model.getTopic());
//                        viewHolder.tvAdmin.append(model.getAdmin());
//                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Toast.makeText(getContext(),"clicked event !",Toast.LENGTH_SHORT).show();
//                                System.out.println("clicked !!");
//                            }
//                        });
//
//                    }
//                };

        final ArrayList<Event> listEvents = new ArrayList<>();
        final MyAdapter myAdapter = new MyAdapter(listEvents);
        mDataBase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listEvents.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        eventsRef.child(child.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                event.setId(dataSnapshot.getKey());
                                listEvents.add(event);
                                myAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }

                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.eventsViewHolder> {
        eventsViewHolder viewHolder;
        private ArrayList<Event> list;

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<Event> myDataset) {
            list = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.eventsViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_event, parent, false);
            viewHolder = new eventsViewHolder(v);

            return viewHolder;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(eventsViewHolder viewHolder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            viewHolder.tvDate.setText("Date : " + list.get(position).getDate());
            viewHolder.tvEvent.setText("Event : " + list.get(position).getName());
            viewHolder.tvLoc.setText("Location : " + list.get(position).getLocation());
            viewHolder.tvTopic.setText("Topic : " + list.get(position).getTopic());
            viewHolder.tvAdmin.setText("Created By : " + list.get(position).getAdmin());
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogDecision(getActivity(), list.get(position)).show();
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return list.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class eventsViewHolder extends RecyclerView.ViewHolder {

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


}
