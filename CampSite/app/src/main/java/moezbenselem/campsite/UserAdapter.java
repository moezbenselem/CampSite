package moezbenselem.campsite;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Moez on 30/01/2019.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.usersViewHolder> {


    public ArrayList<User> listUsers;
    Context context;
    DatabaseReference userRef;

    public UserAdapter(ArrayList<User> listUsers, Context context) {
        this.listUsers = listUsers;
        this.context = context;
    }


    @Override
    public usersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_user, parent, false);

        return new usersViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final UserAdapter.usersViewHolder holder, int position) {


        try {
            final User user = listUsers.get(position);

            holder.tvName.setText(user.getUsername());
            holder.tvStatus.setText(user.getStatus());

            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUsername());

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("online")) {
                        String online = dataSnapshot.child("online").getValue().toString();
                        if (online.equalsIgnoreCase("true")) {

                            holder.onlineIcon.setVisibility(View.VISIBLE);

                        } else
                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            Picasso.with(context).load(user.getImage()).placeholder(R.drawable.male_user).into(holder.imageView);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent toProfile = new Intent(context, UserActivity.class);
                    toProfile.putExtra("name", user.getUsername());
                    toProfile.putExtra("status", user.getStatus());
                    toProfile.putExtra("image", user.getImage());
                    context.startActivity(toProfile);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public static class usersViewHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView tvName, tvStatus;
        CircleImageView imageView;
        ImageView onlineIcon;

        public usersViewHolder(View itemView) {

            super(itemView);
            try {

                mView = itemView;
                tvName = (TextView) itemView.findViewById(R.id.item_display_name);
                tvStatus = (TextView) itemView.findViewById(R.id.item_status);
                imageView = (CircleImageView) itemView.findViewById(R.id.item_image);
                onlineIcon = itemView.findViewById(R.id.online_icon);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}
