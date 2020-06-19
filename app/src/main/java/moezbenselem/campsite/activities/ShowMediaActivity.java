package moezbenselem.campsite.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
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
import com.squareup.picasso.Picasso;

import moezbenselem.campsite.R;

public class ShowMediaActivity extends Activity {

    ImageView save;
    String url, type;
    PhotoView photoView;
    PlayerView playerView;
    SimpleExoPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_media);
        Bundle extra = getIntent().getExtras();

        type = extra.getString("type");
        url = extra.getString("url");

        try {
            photoView = findViewById(R.id.photo_view);
            playerView = findViewById(R.id.video_view);
            save = findViewById(R.id.btn_download);
            if (type.equalsIgnoreCase("video")){
                playerView.setVisibility(View.VISIBLE);
                photoView.setVisibility(View.GONE);

                long mCurrentMillis = 0;

                mPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getApplicationContext()),
                        new DefaultTrackSelector());

                DefaultDataSourceFactory dataSourceFactory =
                        new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "player"));

                ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url));

                boolean isResuming = mCurrentMillis != 0;
                mPlayer.prepare(extractorMediaSource, isResuming, false);
                mPlayer.setPlayWhenReady(false);

                playerView.setPlayer(mPlayer);

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


            }else if(type.equalsIgnoreCase("image")){
                System.out.println("loading image");
                playerView.setVisibility(View.GONE);
                photoView.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext()).load(url).placeholder(R.drawable.loading).into(photoView);
            }

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if(mPlayer!=null){
            mPlayer.release();
            MainActivity.player = null;
        }

        super.onBackPressed();
    }
}
