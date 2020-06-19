package moezbenselem.campsite.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

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
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import moezbenselem.campsite.R;

public class ShowMediaActivity extends Activity {

    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1234;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 12345;
    Button save;
    String url, type, name;
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
        name = extra.getString("name");


        try {
            photoView = findViewById(R.id.photo_view);
            playerView = findViewById(R.id.video_view);
            save = findViewById(R.id.btn_download);
            if (type.equalsIgnoreCase("video")) {
                playerView.setVisibility(View.VISIBLE);
                photoView.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
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


            } else if (type.equalsIgnoreCase("image")) {
                System.out.println("loading image");
                playerView.setVisibility(View.GONE);
                photoView.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext()).load(url).placeholder(R.drawable.black).into(photoView);
            }

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("download clicked !");
                    if (type.equalsIgnoreCase("image")) {
                        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                        Picasso.with(getApplicationContext()).load(url).into(picassoImageTarget(getApplicationContext(), name + ".jpg"));
                        Toast.makeText(getApplicationContext(),
                                "Picture Saved !",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPlayer != null) {
            mPlayer.release();
            MainActivity.player = null;
        }

        super.onBackPressed();
    }

    private void scanFile(String path) {

        MediaScannerConnection.scanFile(ShowMediaActivity.this,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("TAG", "Finished scanning " + path);
                    }
                });
    }

    private Target picassoImageTarget(Context context, final String filename) {
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {

                        try {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                // Should we show an explanation?
                                if (shouldShowRequestPermissionRationale(
                                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                    // Explain to the user why we need to read the contacts
                                }

                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                                // app-defined int constant that should be quite unique

                                return;
                            }
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                // Should we show an explanation?
                                if (shouldShowRequestPermissionRationale(
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    // Explain to the user why we need to read the contacts
                                }

                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                                // app-defined int constant that should be quite unique

                                return;
                            }
                            File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/campsite");
                            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/campsite", filename);

                            dir.mkdirs();
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.flush();
                            ostream.close();
                            scanFile(file.getAbsolutePath());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //Toast.makeText(getApplicationContext(), "image saved to >>> "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {
                }
            }
        };
    }

    @Override
    protected void onPause() {
        if (mPlayer != null) {
            if (mPlayer.getPlayWhenReady())
                mPlayer.setPlayWhenReady(false);
        }
        super.onPause();
    }


}
