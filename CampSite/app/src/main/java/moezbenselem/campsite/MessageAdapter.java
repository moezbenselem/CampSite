package moezbenselem.campsite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Moez on 04/08/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static int INCOMING = 1;
    public static int OUTGOING = 0;
    public ArrayList<Message> listMessages;
    FirebaseAuth mAuth;
    Context context;
    FirebaseUser mCurrentUser;
    DatabaseReference databaseReference, mDatabaseUser;

    public MessageAdapter(ArrayList<Message> listMessages, Context context) {
        this.listMessages = listMessages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        //String last = mDatabaseUser.toString().substring(mDatabaseUser.toString().lastIndexOf('/') + 1);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child("name");

        try {


            Message m = listMessages.get(position);


            if (m.getFrom().equals(mCurrentUser.getDisplayName().toString())) {
                return MessageAdapter.OUTGOING;

            }
            if (m.getFrom() != (mCurrentUser.getDisplayName().toString())) {
                return MessageAdapter.INCOMING;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout2, parent, false);

        try {


            Message c = listMessages.get(viewType);
            String sender = c.getFrom();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(sender);


            if (viewType == MessageAdapter.OUTGOING) {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_layout2, parent, false);

            }

            if (viewType == MessageAdapter.INCOMING) {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_layout, parent, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MessageViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {


        try {

            mAuth = FirebaseAuth.getInstance();
            String current_user_id = mAuth.getCurrentUser().getDisplayName();
            Message m = listMessages.get(position);


            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String time = sfd.format(new Date(listMessages.get(position).getTime()));

            holder.tvTime.setText(time);

            holder.messageImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.messageImage.getTag().equals("default")) {

                        holder.tvTime.setVisibility(View.VISIBLE);
                        holder.messageImage.setTag("clicked");

                    } else {
                        holder.tvTime.setVisibility(View.GONE);
                        holder.messageImage.setTag("default");
                    }


                }
            });


            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.text.getTag().equals("default")) {

                        holder.tvTime.setVisibility(View.VISIBLE);
                        holder.text.setTag("clicked");

                    } else {
                        holder.tvTime.setVisibility(View.GONE);
                        holder.text.setTag("default");
                    }

                }
            });

            String from = m.getFrom();
            String type = m.getType();

            if (type.equals("text")) {

                holder.messageImage.setVisibility(View.GONE);
                holder.text.setText(m.getMessage());
                holder.text.setVisibility(View.VISIBLE);


            } else if (type.equals("image")) {


                Picasso.with(context).load(m.getMessage()).placeholder(R.drawable.loading).into(holder.messageImage);

                holder.messageImage.setVisibility(View.VISIBLE);
                holder.text.setVisibility(View.GONE);


            }

            final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            if (from.equals(mCurrentUser.getDisplayName())) {

                holder.text.setBackgroundResource(R.drawable.message_background2);

                usersRef.child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String image = dataSnapshot.child("thumb_image").getValue().toString();
                        CircleImageView imageView = holder.userImage;
                        Picasso.with(context).load(image).placeholder(R.drawable.male_user).into(imageView);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            } else {

                holder.text.setBackgroundResource(R.drawable.message_background);
                usersRef.child(from).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String image = dataSnapshot.child("thumb_image").getValue().toString();
                        CircleImageView imageView = holder.userImage;
                        Picasso.with(context).load(image).placeholder(R.drawable.male_user).into(imageView);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {


        public TextView text, tvTime;
        CircleImageView userImage;
        ImageView messageImage;


        public MessageViewHolder(View itemView) {

            super(itemView);
            try {
                this.text = itemView.findViewById(R.id.message_message_text);
                this.tvTime = itemView.findViewById(R.id.message_time);
                this.userImage = itemView.findViewById(R.id.message_user_image);
                this.messageImage = itemView.findViewById(R.id.message_image);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(t.getVisibility()==View.VISIBLE)
                        t.setVisibility(View.GONE);
                    else
                        t.setVisibility(View.VISIBLE);
                }
            });
*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
