package moezbenselem.campsite;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {


    FirebaseAuth mAuth;
    ArrayList<User> listFriends;
    DatabaseReference friendsRef, usersRef;
    RecyclerView recyclerView;

    FriendsAdapter adapter;


    DatabaseReference convRef;
    View mainView;
    DatabaseReference messageRef;
    RecyclerView recyclerConv;

    Query friendsQuery;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {


            recyclerView =  getView().findViewById(R.id.recycler_friends);
            recyclerConv =  getView().findViewById(R.id.recycler_discu);


            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            LinearLayoutManager layoutManagerDiscu
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

            layoutManagerDiscu.setReverseLayout(true);
            layoutManagerDiscu.setStackFromEnd(true);

            recyclerView.setLayoutManager(layoutManager);
            recyclerConv.setLayoutManager(layoutManagerDiscu);


            mAuth = FirebaseAuth.getInstance();
            friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mAuth.getCurrentUser().getDisplayName());

            friendsRef.keepSynced(true);

            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            usersRef.keepSynced(true);

            friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                                        adapter = new FriendsAdapter(listFriends, getContext());
                                        recyclerView.setAdapter(adapter);
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
            });

            messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(mAuth.getCurrentUser().getDisplayName());
            messageRef.keepSynced(true);

            convRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(mAuth.getCurrentUser().getDisplayName());
            convRef.keepSynced(true);

            FirebaseRecyclerAdapter<Conv, MessagesFragment.ConvViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(Conv.class, R.layout.card_user, ConvViewHolder.class, this.convRef.orderByChild("time")) {
                        public ConvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            return super.onCreateViewHolder(parent, viewType);
                        }

                        protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {
                            final String list_user_id = getRef(i).getKey();
                            System.out.println("list users id = " + list_user_id);
                            messageRef.child(list_user_id).limitToLast(1).addChildEventListener(new ChildEventListener() {

                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    System.out.println("s ====" + s);
                                    String data = dataSnapshot.child("message").getValue().toString();
                                    System.out.println(data);
                                    String type = dataSnapshot.child("type").getValue().toString();
                                    System.out.println(type);
                                    String from = dataSnapshot.child("from").getValue().toString();
                                    if (dataSnapshot.hasChild("online")) {
                                        convViewHolder.setUserOnline(dataSnapshot.child("online").getValue().toString());
                                    }

                                    if (type.equals("text")) {
                                        convViewHolder.setMessage(data, type, from, conv.isSeen());
                                    } else if (type.equals("image")) {
                                        convViewHolder.setMessage(data, type, from, conv.isSeen());
                                    }
                                }

                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                }

                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                }

                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                }

                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                            usersRef.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChildren()) {

                                        final User user = dataSnapshot.getValue(User.class);

                                        if (dataSnapshot.hasChild("online")) {
                                            convViewHolder.setUserOnline(dataSnapshot.child("online").getValue().toString());
                                        }
                                        convViewHolder.setName(user.getUsername());

                                        convViewHolder.setUserImage(user.getThumb_image(),user.getGender(), getContext());
                                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                System.out.println("uid =" + list_user_id);
                                                System.out.println("name ==" + user.getUsername());
                                                chatIntent.putExtra("uid", list_user_id);
                                                chatIntent.putExtra("name", user.getUsername());
                                                startActivity(chatIntent);
                                            }
                                        });
                                    }else
                                        System.out.println("datasnapshot user ref list is empty");


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            usersRef.child(list_user_id).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    System.out.println("in data changed userRef : datasnapshot count : "+dataSnapshot.getChildrenCount());

                                    if (dataSnapshot.hasChildren()) {

                                        final User user = dataSnapshot.getValue(User.class);

                                        if (dataSnapshot.hasChild("online")) {
                                            convViewHolder.setUserOnline(dataSnapshot.child("online").getValue().toString());
                                        }
                                        convViewHolder.setName(user.getUsername());
                                        convViewHolder.setUserImage(user.getThumb_image(),user.getGender(), getContext());
                                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                System.out.println("uid =" + list_user_id);
                                                System.out.println("name ==" + user.getUsername());
                                                chatIntent.putExtra("uid", list_user_id);
                                                chatIntent.putExtra("name", user.getUsername());
                                                startActivity(chatIntent);
                                            }
                                        });
                                    }else
                                        System.out.println("datasnapshot user ref list is empty");

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    System.out.println("in data changed userRef : datasnapshot count : "+dataSnapshot.getChildrenCount());

                                    if (dataSnapshot.hasChildren()) {

                                        final User user = dataSnapshot.getValue(User.class);

                                        if (dataSnapshot.hasChild("online")) {
                                            convViewHolder.setUserOnline(dataSnapshot.child("online").getValue().toString());
                                        }
                                        convViewHolder.setName(user.getUsername());

                                        convViewHolder.setUserImage(user.getThumb_image(),user.getGender(), getContext());
                                        convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View view) {
                                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                                System.out.println("uid =" + list_user_id);
                                                System.out.println("name ==" + user.getUsername());
                                                chatIntent.putExtra("uid", list_user_id);
                                                chatIntent.putExtra("name", user.getUsername());
                                                startActivity(chatIntent);
                                            }
                                        });
                                    }else
                                        System.out.println("datasnapshot user ref list is empty");

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            usersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild("online")) {
                                        convViewHolder.setUserOnline(dataSnapshot.child("online").getValue().toString());
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    };

            recyclerConv.setAdapter(firebaseRecyclerAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mainView = inflater.inflate(R.layout.fragment_messages, container, false);

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.mainView;
    }


    public static class ConvViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public ConvViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setMessage(String message, String type, String from, boolean isSeen) {

            try {
                TextView userStatusView =  this.mView.findViewById(R.id.item_status);
                System.out.println("from ==== " + from);
                String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (!type.equals("image")) {
                    userStatusView.setText(message);
                    userStatusView.setMaxLines(2);

                    System.out.println("isSeen === " + isSeen);
                    if (user.equals(from)) {
                        userStatusView.setTypeface(userStatusView.getTypeface(), 2);
                    } else if (isSeen) {
                        userStatusView.setTypeface(userStatusView.getTypeface(), 0);
                    } else {
                        userStatusView.setTypeface(userStatusView.getTypeface(), 1);
                    }
                } else if (user.equals(from)) {
                    userStatusView.setText("you sent a picture");
                    userStatusView.setTypeface(userStatusView.getTypeface(), 2);
                } else {
                    userStatusView.setText("sent you a picture");
                    if (isSeen) {
                        userStatusView.setTypeface(userStatusView.getTypeface(), 0);
                    } else {
                        userStatusView.setTypeface(userStatusView.getTypeface(), 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setName(String name) {
            try {
                ((TextView) this.mView.findViewById(R.id.item_display_name)).setText(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setUserImage(String thumb_image,String gender, Context ctx) {
            try {
                if (gender.equalsIgnoreCase("male"))

                    Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.male_user).into((CircleImageView)this.mView.findViewById(R.id.item_image), new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        }
                    });



                else if (gender.equalsIgnoreCase("female"))
                {
                    Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.male_user).into((CircleImageView)this.mView.findViewById(R.id.item_image), new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setUserOnline(String online_status) {
            try {


                ImageView userOnlineView =  this.mView.findViewById(R.id.online_icon);
                if (online_status.equals("true")) {
                    userOnlineView.setVisibility(View.VISIBLE);
                } else {
                    userOnlineView.setVisibility(View.INVISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

}




