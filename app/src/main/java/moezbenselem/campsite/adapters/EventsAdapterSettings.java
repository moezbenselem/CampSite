package moezbenselem.campsite.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.SettingsActivity;
import moezbenselem.campsite.dialogs.DialogDecision;
import moezbenselem.campsite.entities.Event;

/**
 * Created by Moez on 31/01/2019.
 */

public class EventsAdapterSettings extends RecyclerView.Adapter<EventsAdapterSettings.eventsViewHolder> {


    public ArrayList<Event> listEvents;
    public HashMap<String, Boolean> listeventsKeys;
    Context context;
    DatabaseReference userRef;


    public EventsAdapterSettings(ArrayList<Event> listEvents, HashMap<String, Boolean> listeventsKeys, Context context) {
        this.listEvents = listEvents;
        this.listeventsKeys = listeventsKeys;
        this.context = context;
    }

    @Override
    public EventsAdapterSettings.eventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_event_setting, parent, false);

        return new EventsAdapterSettings.eventsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EventsAdapterSettings.eventsViewHolder holder, int position) {


        try {
            final Event event = listEvents.get(position);

            holder.tvEvent.setText("Event : " + event.getName());
            holder.tvLoc.setText("Location : " + event.getLocation());
            holder.tvTopic.setText("Topic : " + event.getTopic());
            holder.tvDate.setText("Date : " + event.getDate());
            holder.btInvite.setTag(event.getId());
            System.out.println("event bool : " + listeventsKeys.get(event.getId()));
            /*if(listeventsKeys.get(event.getId()).equals(true)){
                holder.btInvite.setBackgroundResource(R.drawable.add_png_disabled);
            }else {
                holder.btInvite.setBackgroundResource(R.drawable.add_png);
            }*/
            holder.btInvite.setChecked(listeventsKeys.get(event.getId()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DialogDecision((Activity) context, event).show();

                }
            });

            holder.btInvite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SettingsActivity.resultEvents.put(buttonView.getTag().toString(), isChecked);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

    public static class eventsViewHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView tvDate, tvLoc, tvTopic, tvEvent;
        CheckBox btInvite;

        public eventsViewHolder(View itemView) {

            super(itemView);
            try {

                mView = itemView;
                tvDate = itemView.findViewById(R.id.item_date);
                tvEvent = itemView.findViewById(R.id.item_event_name);
                tvLoc = itemView.findViewById(R.id.item_location);
                tvTopic = itemView.findViewById(R.id.item_desc);
                btInvite = itemView.findViewById(R.id.checkbox_event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
