package moezbenselem.campsite;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RechercheFragment extends Fragment {


    DatabaseReference mDatabaseRef;
    RecyclerView recyclerView;
    String text;
    ArrayList<User> listResults;

    UserAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_search);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        MainActivity.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                text = query;
                listResults = new ArrayList<User>();
                mDatabaseRef.child(query).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        try {
                            if (dataSnapshot.hasChildren()) {


                                User user = dataSnapshot.getValue(User.class);
                                listResults.add(user);
                                System.out.println(user.email);
                                System.out.println(user.username);


                                adapter = new UserAdapter(listResults, getContext());
                                recyclerView.setAdapter(adapter);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recherche, container, false);
    }


    /*@Override public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User, usersViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<User, usersViewHolder>(User.class,
                        R.layout.card_user,
                        usersViewHolder.class,
                        mDatabaseRef) {
                    @Override
                    protected void populateViewHolder(usersViewHolder viewHolder, final User model, int position) {

                        viewHolder.tvName.setText(model.getUsername());
                        viewHolder.tvStatus.setText(model.getStatus());
                        CircleImageView circleImageView = viewHolder.imageView;
                        Picasso.with(getContext()).load(model.getThumb_image()).placeholder(R.drawable.male_user).into(circleImageView);

                        final String uid = getRef(position).getKey();

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent toProfile = new Intent(getContext(), UserActivity.class);
                                toProfile.putExtra("uid", uid);
                                toProfile.putExtra("name", model.getUsername());
                                toProfile.putExtra("status", model.getStatus());
                                toProfile.putExtra("image", model.getImage());
                                startActivity(toProfile);
                            }
                        });

                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
*/

    public static class usersViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView tvName, tvStatus;
        CircleImageView imageView;
        ImageView onlineIcon;

        public usersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tvName = (TextView) itemView.findViewById(R.id.item_display_name);
            tvStatus = (TextView) itemView.findViewById(R.id.item_status);
            imageView = (CircleImageView) itemView.findViewById(R.id.item_image);
            onlineIcon = itemView.findViewById(R.id.online_icon);

        }

    }


}
