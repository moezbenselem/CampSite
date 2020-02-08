package moezbenselem.campsite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Moez on 31/01/2019.
 */

public class FriendsAdapter  extends RecyclerView.Adapter<FriendsAdapter.usersViewHolder>{


    public ArrayList<User> listUsers;
    Context context;

    public FriendsAdapter(ArrayList<User> listUsers,Context context) {
        this.listUsers = listUsers;
        this.context = context;
    }


    @Override
    public FriendsAdapter.usersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_card, parent, false);

        return new FriendsAdapter.usersViewHolder(v);
    }


    DatabaseReference userRef;

    @Override
    public void onBindViewHolder(final FriendsAdapter.usersViewHolder holder, int position) {


        try {
            final User user = listUsers.get(position);

            holder.tvName.setText(user.getUsername());

            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUsername());

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild("online"))
                    {
                        String online = dataSnapshot.child("online").getValue().toString();
                        if(online.equalsIgnoreCase("true")){

                            holder.onlineIcon.setVisibility(View.VISIBLE);

                        }else
                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            if(user.getGender().equalsIgnoreCase("male"))
            {
                Picasso.with(context).load(user.getImage()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.male_user).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(context).load(user.getImage())
                                .placeholder(R.drawable.male_user).into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                            }
                        });

                    }
                });

            }
            else{
                Picasso.with(context).load(user.getImage()).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.female_user).into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                        Picasso.with(context).load(user.getImage())
                                .placeholder(R.drawable.female_user).into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {


                            }
                        });

                    }
                });

            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent toChat = new Intent(context, ChatActivity.class);
                    toChat.putExtra("name", user.getUsername());
                    toChat.putExtra("status", user.getStatus());
                    toChat.putExtra("image", user.getImage());
                    context.startActivity(toChat);

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public static class usersViewHolder extends RecyclerView.ViewHolder{


        View mView;
        TextView tvName;
        CircleImageView imageView;
        ImageView onlineIcon;

        public usersViewHolder(View itemView){

            super(itemView);
            try {

                mView = itemView;
                tvName = itemView.findViewById(R.id.item_display_name);
                imageView = itemView.findViewById(R.id.item_image);
                onlineIcon = itemView.findViewById(R.id.online_icon);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


}
