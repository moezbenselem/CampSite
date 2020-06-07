package moezbenselem.campsite;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Moez on 31/01/2019.
 */

public class EventsAdapter  extends RecyclerView.Adapter<EventsAdapter.eventsViewHolder>{


    public ArrayList<Event> listEvents;
    Context context;

    public EventsAdapter(ArrayList<Event> listEvents,Context context) {
        this.listEvents = listEvents;
        this.context = context;
    }


    @Override
    public EventsAdapter.eventsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_event, parent, false);

        return new EventsAdapter.eventsViewHolder(v);
    }


    DatabaseReference userRef;

    @Override
    public void onBindViewHolder(final EventsAdapter.eventsViewHolder holder, int position) {


        try {
            final Event event = listEvents.get(position);

            holder.tvEvent.setText("Event : " +event.getName());
            holder.tvLoc.setText("Location : " +event.getLocation());
            holder.tvTopic.setText("Topic : " +event.getTopic());
            holder.tvAdmin.setText("Created By : " +event.getAdmin());
            holder.tvDate.setText("Date : " +event.getDate());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new EventDialog((Activity) context,event).show();

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

    public static class eventsViewHolder extends RecyclerView.ViewHolder{


        View mView;
        TextView tvDate,tvLoc,tvTopic,tvEvent,tvAdmin;

        public eventsViewHolder(View itemView){

            super(itemView);
            try {

                mView = itemView;
                tvAdmin = itemView.findViewById(R.id.tv_admin);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvEvent = itemView.findViewById(R.id.tv_event);
                tvLoc = itemView.findViewById(R.id.tv_location);
                tvTopic = itemView.findViewById(R.id.tv_topic);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


}
