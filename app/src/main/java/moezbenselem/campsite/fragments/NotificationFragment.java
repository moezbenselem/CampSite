package moezbenselem.campsite.fragments;


import android.app.Activity;
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
import me.leolin.shortcutbadger.ShortcutBadger;
import moezbenselem.campsite.FirebaseMessagingService;
import moezbenselem.campsite.Notification;
import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.UserActivity;
import moezbenselem.campsite.dialogs.EventDialog;
import moezbenselem.campsite.entities.Event;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    static Bundle arguments;
    View mainView;
    DatabaseReference notifRef;
    RecyclerView recyclerNotif;
    FirebaseAuth mAuth;
    DatabaseReference userRef;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            if (getArguments() != null)
                arguments = getArguments();

            if (FirebaseMessagingService.badgeCount > 0) {
                FirebaseMessagingService.badgeCount = 0;
                ShortcutBadger.applyCount(getActivity().getApplicationContext(), FirebaseMessagingService.badgeCount);
            }

            recyclerNotif = getView().findViewById(R.id.recycler_notif);
            LinearLayoutManager layoutManagerNotif
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

            layoutManagerNotif.setReverseLayout(true);
            layoutManagerNotif.setStackFromEnd(true);

            recyclerNotif.setLayoutManager(layoutManagerNotif);

            mAuth = FirebaseAuth.getInstance();
            notifRef = FirebaseDatabase.getInstance().getReference().child("notifications").child(mAuth.getCurrentUser().getDisplayName());


            FirebaseRecyclerAdapter<Notification, NotifViewHolder> firebaseRecyclerAdapter =
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
                                                final String from = dataSnapshot.child("from").getValue().toString(),
                                                        time = dataSnapshot.child("time").getValue().toString(),
                                                        type = dataSnapshot.child("type").getValue().toString();

                                                System.out.println(from + " " + type + " " + time);
                                                String image = "";
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(from).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String image = dataSnapshot.child("image").getValue().toString();

                                                        viewHolder.setBody(type, from, time, getContext(), image);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

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

        public void setBody(final String type, final String from, String time, final Context cnx, String image) {

            try {
                TextView bodyView = this.mView.findViewById(R.id.item_body);
                String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                ((TextView) this.mView.findViewById(R.id.item_display_name)).setText(from);
                ((TextView) this.mView.findViewById(R.id.item_time)).setText(time);
                if (type.equalsIgnoreCase("request")) {
                    bodyView.setText("has sent you a friend request !");
                    System.out.println("image notif : " + image);
                    Picasso.with(cnx).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.request).into((CircleImageView) this.mView.findViewById(R.id.item_image), new Callback() {
                        @Override
                        public void onSuccess() {
                            System.out.println("notif pic loaded !");
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

                } else {
                    bodyView.setText("has sent you an Event invitation !");
                    System.out.println("image notif : " + image);
                    Picasso.with(cnx).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.request).into((CircleImageView) this.mView.findViewById(R.id.item_image), new Callback() {
                        @Override
                        public void onSuccess() {
                            System.out.println("notif pic loaded !");
                        }

                        @Override
                        public void onError() {
                        }
                    });

                    mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            FirebaseDatabase.getInstance().getReference().child("Events").child(type).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Event event = dataSnapshot.getValue(Event.class);
                                    event.setId(dataSnapshot.getKey());
                                    new EventDialog((Activity) cnx, event).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
