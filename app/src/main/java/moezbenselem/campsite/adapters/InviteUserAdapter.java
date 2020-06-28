package moezbenselem.campsite.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.InviteActivity;
import moezbenselem.campsite.activities.MainActivity;
import moezbenselem.campsite.activities.UserActivity;
import moezbenselem.campsite.entities.User;

/**
 * Created by Moez on 30/01/2019.
 */

public class InviteUserAdapter extends RecyclerView.Adapter<InviteUserAdapter.usersViewHolder> {


    public ArrayList<User> listUsers;
    Context context;
    DatabaseReference userEventsRef;

    public InviteUserAdapter(ArrayList<User> listUsers, Context context) {
        this.listUsers = listUsers;
        this.context = context;
    }


    @Override
    public usersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_invite_user, parent, false);

        return new usersViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final InviteUserAdapter.usersViewHolder holder, final int position) {


        try {
            final User user = listUsers.get(position);

            holder.tvName.setText(user.getUsername());
            holder.tvStatus.setText(user.getStatus());

            userEventsRef = FirebaseDatabase.getInstance().getReference().child("My_Events").child(user.getUsername());

            Picasso.with(context).load(user.getImage()).placeholder(R.drawable.male_user).into(holder.imageView);

            holder.btInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View view = v;
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(InviteActivity.event_id, true);
                    if (v.getTag().toString().equals("add")) {
                        final String currentDate = getDateTime();
                        HashMap<String, String> notifData = new HashMap<String, String>();
                        notifData.put("from", MainActivity.mAuth.getCurrentUser().getDisplayName());
                        notifData.put("type", InviteActivity.event_id);
                        notifData.put("time", currentDate);
                        DatabaseReference notifDatabaseRef = FirebaseDatabase.getInstance().getReference().child("notifications");
                        notifDatabaseRef.child(user.getUsername()).push().setValue(notifData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, listUsers.get(position).getUsername() + " added !", Toast.LENGTH_SHORT).show();
                                view.setTag("remove");
                                holder.btInvite.setBackgroundResource(R.drawable.valid);
                            }
                        });

                    }

                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent toProfile = new Intent(context, UserActivity.class);
                    toProfile.putExtra("name", user.getUsername());
                    toProfile.putExtra("status", user.getStatus());
                    toProfile.putExtra("image", user.getImage());
                    toProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        java.util.Date date = new java.util.Date();
        return dateFormat.format(date);
    }

    public static class usersViewHolder extends RecyclerView.ViewHolder {


        View mView;
        TextView tvName, tvStatus;
        CircleImageView imageView;
        Button btInvite;


        public usersViewHolder(View itemView) {

            super(itemView);
            try {

                mView = itemView;
                tvName = itemView.findViewById(R.id.item_display_name);
                tvStatus = itemView.findViewById(R.id.item_status);
                imageView = itemView.findViewById(R.id.item_image);
                btInvite = itemView.findViewById(R.id.btI_invite_friend);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
