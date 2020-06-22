package moezbenselem.campsite.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentManager;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Map;

import moezbenselem.campsite.R;
import moezbenselem.campsite.TrackService;
import moezbenselem.campsite.fragments.EventsFragment;
import moezbenselem.campsite.fragments.MessagesFragment;
import moezbenselem.campsite.fragments.NotificationFragment;
import moezbenselem.campsite.fragments.ProfileFragment;
import moezbenselem.campsite.fragments.RechercheFragment;
import moezbenselem.campsite.fragments.TeamFragment;

public class MainActivity extends AppCompatActivity {


    public static SimpleExoPlayer player = null;
    public static FirebaseAuth mAuth;
    public static SearchView searchView;
    public static Context context;
    public static MenuItem myActionMenuItem;
    public static BottomNavigationView navigation;
    FragmentManager fragmentManager = getSupportFragmentManager();
    DatabaseReference userRef;
    StorageReference mStorageRef;
    DatabaseReference mDatabase;
    Uri resultUri = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_events:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, EventsFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
                case R.id.navigation_profile:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, ProfileFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
                case R.id.navigation_group:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, TeamFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
                case R.id.navigation_messages:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, MessagesFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
                case R.id.navigation_notif:

                    try {
                        fragmentManager.beginTransaction().replace(R.id.content, NotificationFragment.class.newInstance())
                                .addToBackStack(null).commit();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return true;
            }
            return false;
        }

    };
    private moezbenselem.campsite.TrackService TrackService;
    private Intent trackIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = getApplicationContext();
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_profile);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getDisplayName());

        try {
            fragmentManager.beginTransaction().replace(R.id.content, ProfileFragment.class.newInstance())
                    .addToBackStack(null).commit();


            TrackService = new TrackService();
            trackIntent = new Intent(this, TrackService.getClass());

            if (!isMyServiceRunning(TrackService.getClass())) {
                this.startService(trackIntent);
                System.out.println("service started from Track fragment");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        userRef.child("online").setValue(true);
        userRef.child("device_token").setValue(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fragmentManager.beginTransaction().replace(R.id.content, RechercheFragment.class.newInstance())
                            .addToBackStack(null).commit();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    fragmentManager.beginTransaction().replace(R.id.content, RechercheFragment.class.newInstance())
                            .addToBackStack(null).commit();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                //UserFeedback.show( "SearchOnQueryTextSubmit: " + query);
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        try {
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_logout) {

                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    userRef.child("device_token").setValue("null");
                    userRef.child("online").setValue(ServerValue.TIMESTAMP);

                    toStart();
                }


                return true;
            }
            if (id == R.id.action_settings) {

                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    public void toStart() {
        try {
            Intent toStart = new Intent(MainActivity.this, LoginActivity.class);
            mAuth.signOut();
            startActivity(toStart);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        userRef.child("online").setValue(ServerValue.TIMESTAMP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        userRef.child("online").setValue(ServerValue.TIMESTAMP);
        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            } else {
                resultUri = result.getUri();
                ProfileFragment.imageView.setImageURI(resultUri);

                uploadImage(mAuth.getCurrentUser().getDisplayName());

            }
        }

    }

    public void uploadImage(final String nom) {

        try {

            mStorageRef = FirebaseStorage.getInstance().getReference();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getDisplayName());

            final StorageReference filePath = mStorageRef.child("users").child(nom + ".jpg");

            filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    System.out.println("Upload success !");

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            // Got the download URL for 'users/me/profile.png'

                            System.out.println("Uri == " + uri);
                            Map updateHashMap = new HashMap();
                            updateHashMap.put("image", uri.toString());
                            updateHashMap.put("thumb_image", uri.toString());
                            mDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (!task.isSuccessful()) {

                                        task.getException().printStackTrace();

                                    } else {
                                        System.out.println("Update success !");
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(uri)
                                                .build();

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            exception.printStackTrace();
                        }
                    });

                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                System.out.println("service is running");
                return true;
            } else {
                System.out.println("service is not running");
            }
        }
        Log.i("Service status", "Not running");
        return false;
    }


}
