package moezbenselem.campsite.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
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
import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.MainActivity;
import moezbenselem.campsite.activities.ShowMediaActivity;
import moezbenselem.campsite.entities.Message;

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


            if (m.getFrom().equals(mCurrentUser.getDisplayName())) {
                return MessageAdapter.OUTGOING;

            }
            if (m.getFrom() != (mCurrentUser.getDisplayName())) {
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
            final Message m = listMessages.get(position);


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
                holder.messageImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toShow = new Intent(context, ShowMediaActivity.class);
                        toShow.putExtra("type","image");
                        toShow.putExtra("url",m.getMessage());
                        context.startActivity(toShow);
                    }
                });

            } else if (type.equals("video")) {

                final SimpleExoPlayer mPlayer;
                String mVideoUrl = m.getMessage();
                long mCurrentMillis = 0;

                // set default options for the player
                mPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(context),
                        new DefaultTrackSelector());


                holder.messageVideo.setPlayer(mPlayer);

                DefaultDataSourceFactory dataSourceFactory =
                        new DefaultDataSourceFactory(context, Util.getUserAgent(context, "player"));
                ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mVideoUrl));

                // now is the more important part. here we check to see if we want to resume, or start from the beggining
                boolean isResuming = mCurrentMillis != 0;
                mPlayer.prepare(extractorMediaSource, isResuming, false);
                mPlayer.setPlayWhenReady(false);
                if (isResuming) {
                    // want to resume? seek to the old position
                    mPlayer.seekTo(mCurrentMillis);
                }

                mPlayer.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        if (playWhenReady && playbackState == Player.STATE_READY) {
                            if (MainActivity.player != null) {
                                MainActivity.player.setPlayWhenReady(false);
                                MainActivity.player = mPlayer;
                            } else
                                MainActivity.player = mPlayer;

                        } else if (playWhenReady) {
                            // might be idle (plays after prepare()),
                            // buffering (plays when data available)
                            // or ended (plays when seek away from end)

                        } else {
                            // player paused in any state
                            MainActivity.player = null;
                        }
                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {

                    }
                });

                holder.messageVideo.setVisibility(View.VISIBLE);
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
        PlayerView messageVideo;


        public MessageViewHolder(final View itemView) {

            super(itemView);
            try {
                this.text = itemView.findViewById(R.id.message_message_text);
                this.tvTime = itemView.findViewById(R.id.message_time);
                this.userImage = itemView.findViewById(R.id.message_user_image);
                this.messageImage = itemView.findViewById(R.id.message_image);
                this.messageVideo = itemView.findViewById(R.id.message_video);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
