package moezbenselem.campsite;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {


    FirebaseAuth mAuth;
    ArrayList<User> listFriends;
    DatabaseReference friendsRef,usersRef;
    RecyclerView recyclerView;

    FriendsAdapter adapter;


    DatabaseReference convRef;
    String current_user_id;
    View mainView;
    DatabaseReference messageRef;
    RecyclerView recyclerConv;
    DatabaseReference userRef;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {


            recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_friends);

            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

            recyclerView.setLayoutManager(layoutManager);

            mAuth = FirebaseAuth.getInstance();
            friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mAuth.getCurrentUser().getDisplayName());
            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

            friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    listFriends = new ArrayList<User>();

                    if (dataSnapshot.hasChildren()) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            System.out.println("key " + child.getKey());

                            usersRef.child(child.getKey()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    User user = dataSnapshot.getValue(User.class);
                                    System.out.println(user.email);
                                    System.out.println(user.username);
                                    listFriends.add(user);

                                    adapter = new FriendsAdapter(listFriends, getContext());
                                    recyclerView.setAdapter(adapter);

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


            this.recyclerConv.setAdapter(new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(Conv.class, R.layout.card_user, ConvViewHolder.class, this.convRef.orderByChild("timestamp")) {
                public ConvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return (ConvViewHolder) super.onCreateViewHolder(parent, viewType);
                }

                protected void populateViewHolder(final ConvViewHolder convViewHolder, final Conv conv, int i) {
                    final String list_user_id = getRef(i).getKey();
                    MessagesFragment.this.messageRef.child(list_user_id).limitToLast(1).addChildEventListener(new ChildEventListener() {
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            System.out.println("s ====" + s);
                            String data = dataSnapshot.child("message").getValue().toString();
                            String type = dataSnapshot.child("type").getValue().toString();
                            String from = dataSnapshot.child("from").getValue().toString();
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
                    MessagesFragment.this.userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final String userName = dataSnapshot.child("name").getValue().toString();
                            String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                            if (dataSnapshot.hasChild("online")) {
                                convViewHolder.setUserOnline(dataSnapshot.child("online").getValue().toString());
                            }
                            convViewHolder.setName(userName);
                            convViewHolder.setUserImage(userThumb, MessagesFragment.this.getContext());
                            convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    Intent chatIntent = new Intent(MessagesFragment.this.getContext(), ChatActivity.class);
                                    System.out.println("uid =" + list_user_id);
                                    System.out.println("name ==" + userName);
                                    chatIntent.putExtra("uid", list_user_id);
                                    chatIntent.putExtra("name", userName);
                                    MessagesFragment.this.startActivity(chatIntent);
                                }
                            });
                        }

                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mainView = inflater.inflate(R.layout.fragment_messages, container, false);
        try {

            this.recyclerConv = (RecyclerView) this.mainView.findViewById(R.id.recycler_discu);
            this.mAuth = FirebaseAuth.getInstance();
            this.current_user_id = this.mAuth.getCurrentUser().getUid();
            this.convRef = FirebaseDatabase.getInstance().getReference().child("Chat").child(this.current_user_id);
            this.convRef.keepSynced(true);
            this.userRef = FirebaseDatabase.getInstance().getReference().child("Users");
            this.messageRef = FirebaseDatabase.getInstance().getReference().child("messages").child(this.current_user_id);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            this.recyclerConv.setLayoutManager(linearLayoutManager);

        }catch (Exception e){
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


                TextView userStatusView = (TextView) this.mView.findViewById(R.id.item_status);
                System.out.println("from ==== " + from);
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void setUserImage(String thumb_image, Context ctx) {
            try {
                Picasso.with(ctx).load(thumb_image).placeholder((int) R.drawable.male_avatar).into((CircleImageView) this.mView.findViewById(R.id.item_image));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void setUserOnline(String online_status) {
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

        }


    }

}




