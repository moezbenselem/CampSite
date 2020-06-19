package moezbenselem.campsite.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import moezbenselem.campsite.R;
import moezbenselem.campsite.adapters.ConvAdapter;
import moezbenselem.campsite.adapters.FriendsAdapter;
import moezbenselem.campsite.entities.Conv;
import moezbenselem.campsite.entities.User;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {


    FirebaseAuth mAuth;
    ArrayList<User> listFriends;
    DatabaseReference friendsRef, usersRef, messageRef, convRef;
    RecyclerView recyclerView, recyclerConv;

    FriendsAdapter adapter;
    ConvAdapter convAdapter;
    ArrayList<Conv> listConv;
    View mainView;


    Query friendsQuery;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {


            recyclerView = getView().findViewById(R.id.recycler_friends);
            recyclerConv = getView().findViewById(R.id.recycler_discu);


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

                            //System.out.println("key " + child.getKey());

                            Query userQuery = usersRef.child(child.getKey()).orderByChild("online");

                            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    try {


                                        User user = dataSnapshot.getValue(User.class);
                                        System.out.println(user.getEmail());
                                        System.out.println(user.getUsername());
                                        listFriends.add(user);

                                        adapter = new FriendsAdapter(listFriends, getContext());
                                        recyclerView.setAdapter(adapter);
                                    } catch (Exception e) {
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

            listConv = new ArrayList<>();

            //fetch convs
            convRef.orderByChild("time").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //fetch last message data for each conv
                    for (final DataSnapshot conv : dataSnapshot.getChildren()) {

                        messageRef.child(conv.getKey()).limitToLast(1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        Conv c = data.getValue(Conv.class);

                                        User u = findUser(conv.getKey(), listFriends);

                                        c.setUserImage(u.getImage());
                                        c.setPartner(conv.getKey());
                                        c.setPartnerGender(u.getGender());
                                        listConv.add(c);

                                    }
                                    try {
                                        convAdapter = new ConvAdapter(listConv, getContext());

                                        recyclerConv.setAdapter(convAdapter);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                                //System.out.println(conv.getKey()+" last message data : "+dataSnapshot);

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


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.mainView = inflater.inflate(R.layout.fragment_messages, container, false);

        return this.mainView;
    }


    public User findUser(String userId, ArrayList<User> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUsername().equalsIgnoreCase(userId))
                return list.get(i);
        }
        return null;
    }

}
