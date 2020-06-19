package moezbenselem.campsite;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import moezbenselem.campsite.activities.MainActivity;

/**
 * Created by Moez on 30/01/2019.
 */

public class CampSite extends Application {

    DatabaseReference userDatabase;
    FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        try {


            FirebaseDatabase.getInstance().setPersistenceEnabled(true);

            Picasso.Builder builder = new Picasso.Builder(this);
            builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
            Picasso built = builder.build();
            built.setIndicatorsEnabled(true);
            built.setLoggingEnabled(true);
            Picasso.setSingletonInstance(built);

            mAuth = MainActivity.mAuth;
            if (mAuth != null)
                if (mAuth.getCurrentUser() != null) {

                    FirebaseUser user = mAuth.getCurrentUser();
                    userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getDisplayName());
                    userDatabase.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
