package moezbenselem.campsite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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



    @Override
    public void onBindViewHolder(final FriendsAdapter.usersViewHolder holder, int position) {


        try {
            final User user = listUsers.get(position);

            holder.tvName.setText(user.getUsername());

            Picasso.with(context).load(user.getImage()).placeholder(R.drawable.male_user).into(holder.imageView);

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

        public usersViewHolder(View itemView){

            super(itemView);
            try {

                mView = itemView;
                tvName = (TextView)itemView.findViewById(R.id.item_display_name);
                imageView = (CircleImageView) itemView.findViewById(R.id.item_image);

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


}
