package moezbenselem.campsite.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.ChatActivity;
import moezbenselem.campsite.entities.Conv;

public class ConvAdapter extends RecyclerView.Adapter<ConvAdapter.ConvViewHolder> {

    public ArrayList<Conv> listConv;
    Context context;

    public ConvAdapter(ArrayList<Conv> listConv, Context context) {
        this.listConv = listConv;
        this.context = context;
    }

    @NonNull
    @Override
    public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_user, parent, false);

        return new ConvViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ConvViewHolder holder, final int position) {

        final Conv conv = listConv.get(position);

        holder.setName(conv.getPartner());

        holder.setMessage(conv.getMessage(), conv.getType(), conv.getFrom(), conv.isSeen());

        holder.setUserImage(conv.getUserImage(), conv.getPartnerGender(), context);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("uid", conv.getPartner());
                chatIntent.putExtra("name", conv.getPartner());
                context.startActivity(chatIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listConv.size();
    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView userStatusView, userName;
        CircleImageView userImage;
        ImageView onlineImage;

        public ConvViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.userImage = mView.findViewById(R.id.item_image);
            this.onlineImage = mView.findViewById(R.id.online_icon);
            this.userStatusView = mView.findViewById(R.id.item_status);
            this.userName = mView.findViewById(R.id.item_display_name);

        }

        public void setMessage(String message, String type, String from, boolean isSeen) {

            try {
                TextView userStatusView = this.mView.findViewById(R.id.item_status);
                String user = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                if (!type.equals("image")) {
                    userStatusView.setText(message);
                    userStatusView.setMaxLines(2);

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

                if (!type.equals("video")) {
                    userStatusView.setText(message);
                    userStatusView.setMaxLines(2);

                    if (user.equals(from)) {
                        userStatusView.setTypeface(userStatusView.getTypeface(), 2);
                    } else if (isSeen) {
                        userStatusView.setTypeface(userStatusView.getTypeface(), 0);
                    } else {
                        userStatusView.setTypeface(userStatusView.getTypeface(), 1);
                    }
                } else if (user.equals(from)) {
                    userStatusView.setText("you sent a video");
                    userStatusView.setTypeface(userStatusView.getTypeface(), 2);
                } else {
                    userStatusView.setText("sent you a video");
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

        public void setUserImage(String thumb_image, String gender, Context ctx) {
            try {
                if (gender.equalsIgnoreCase("male"))

                    Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.male_user).into((CircleImageView) this.mView.findViewById(R.id.item_image), new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        }
                    });


                else if (gender.equalsIgnoreCase("female")) {
                    Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.male_user).into((CircleImageView) this.mView.findViewById(R.id.item_image), new Callback() {
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

                ImageView userOnlineView = this.mView.findViewById(R.id.online_icon);
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
