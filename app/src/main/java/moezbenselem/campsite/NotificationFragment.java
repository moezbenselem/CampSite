package moezbenselem.campsite;


import android.content.Context;
import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    View mainView;
    DatabaseReference notifRef;
    RecyclerView recyclerNotif;
    FirebaseAuth mAuth;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {


            recyclerNotif = getView().findViewById(R.id.recycler_notif);
            LinearLayoutManager layoutManagerNotif
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

            layoutManagerNotif.setReverseLayout(true);
            layoutManagerNotif.setStackFromEnd(true);

            recyclerNotif.setLayoutManager(layoutManagerNotif);

            mAuth = FirebaseAuth.getInstance();
            notifRef = FirebaseDatabase.getInstance().getReference().child("notifications").child(mAuth.getCurrentUser().getDisplayName());

            FirebaseRecyclerAdapter<Notification,NotifViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Notification, NotifViewHolder>(Notification.class, R.layout.card_notif, NotifViewHolder.class, this.notifRef) {
                        @Override
                        protected void populateViewHolder(final NotifViewHolder viewHolder, Notification model, int i) {

                            try {
                                final String list_notif_id = getRef(i).getKey();
                                System.out.println(list_notif_id);
                                notifRef.child(list_notif_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {
                                            if (dataSnapshot.hasChildren()) {
                                                String from = dataSnapshot.child("from").getValue().toString(),
                                                        time = dataSnapshot.child("time").getValue().toString(),
                                                        type = dataSnapshot.child("type").getValue().toString();

                                                System.out.println(from + " " + type + " " + time);
                                                viewHolder.setBody(type, from, time,getContext());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };


            recyclerNotif.setAdapter(firebaseRecyclerAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);


    }


    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public NotifViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

        }

        public void setBody(String type, final String from, String time, final Context cnx) {

            try {
                TextView bodyView = this.mView.findViewById(R.id.item_body);
                String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                ((TextView) this.mView.findViewById(R.id.item_display_name)).setText(from);
                ((TextView) this.mView.findViewById(R.id.item_time)).setText(time);
                if(type.equalsIgnoreCase("request"))
                {
                    bodyView.setText("has sent you a friend request !");
                    Picasso.with(cnx).load(R.drawable.request).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.request).into((CircleImageView)this.mView.findViewById(R.id.item_image), new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        }
                    });

                    mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent toProfile = new Intent(cnx, UserActivity.class);
                            toProfile.putExtra("name", from);
                            toProfile.putExtra("status", "");
                            toProfile.putExtra("image", "");
                            cnx.startActivity(toProfile);
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*public void setUserOnline(String online_status) {
            try {


                ImageView userOnlineView = (ImageView) this.mView.findViewById(R.id.online_icon);
                if (online_status.equals("true")) {
                    userOnlineView.setVisibility(View.VISIBLE);
                } else {
                    userOnlineView.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }*/


    }


}
