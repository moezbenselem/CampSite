package moezbenselem.campsite.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import moezbenselem.campsite.R;


public class DialogMedia extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public ImageView save;
    String url, type;
    PhotoView photoView;
    PlayerView playerView;
    DatabaseReference mDatabase, groupChatRef;
    StorageReference storageReference;

    public DialogMedia(Activity a, String type, String url) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.type = type;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_media);

            photoView = findViewById(R.id.photo_view);
            playerView = findViewById(R.id.video_view);
            save = findViewById(R.id.btn_download);
            if (type.equalsIgnoreCase("video")) {
                playerView.setVisibility(View.VISIBLE);
                photoView.setVisibility(View.GONE);


            } else if (type.equalsIgnoreCase("image")) {
                System.out.println("loading image");
                playerView.setVisibility(View.GONE);
                photoView.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                Picasso.with(c).load(url).placeholder(R.drawable.loading).into(photoView);
            }

            save = findViewById(R.id.btn_download);
            save.setOnClickListener(this);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:


                break;

            default:
                break;
        }
        dismiss();
    }


}
