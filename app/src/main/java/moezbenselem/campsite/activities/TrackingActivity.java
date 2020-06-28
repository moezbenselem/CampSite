package moezbenselem.campsite.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import moezbenselem.campsite.R;
import moezbenselem.campsite.entities.Cord;

public class TrackingActivity extends AppCompatActivity implements PermissionsListener, MapboxMap.OnMapClickListener {

    private static final String MAP_TOKEN = "sk.eyJ1IjoiYmVuc2VsZW1tb2V6IiwiYSI6ImNrOHp1cXdrMTB2MHozZnM3aTk3dDl5MDQifQ.eOyRaWad8d8b2Bmm5sEPEw";
    public MapboxMap myMapboxMap;
    FirebaseAuth mAuth;
    DatabaseReference FriendTrackRef, MembersRef;
    String eventId, eventName;
    ArrayList<String> listMembers;
    String locationName;
    Cord locationCord;
    CameraPosition currentPosition;
    Button btMyLocation, btEventLocation;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private List<Point> routeCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MAP_TOKEN);
        setContentView(R.layout.activity_tracking);

        //Get the default actionbar instance
        androidx.appcompat.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        eventId = getIntent().getStringExtra("eventId");
        eventName = getIntent().getStringExtra("eventName");


//Initializes the custom action bar layout
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_toolbar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayShowTitleEnabled(false);

        TextView tvTitle = findViewById(R.id.tv_appbar_name);
        tvTitle.setText("Tracking : " + eventName);

        CircleImageView userImage = findViewById(R.id.icon_app_bar);
        userImage.setVisibility(View.GONE);

        ImageView onlineIcon = findViewById(R.id.image_appbar_online);
        onlineIcon.setVisibility(View.GONE);

        TextView onlineText = findViewById(R.id.tv_appbar_online);
        onlineText.setVisibility(View.GONE);

        mapView = findViewById(R.id.mapView_activity);
        mapView.onCreate(savedInstanceState);


        listMembers = new ArrayList<>();

        btMyLocation = findViewById(R.id.btn_mylocation);
        btEventLocation = findViewById(R.id.btn_event_location);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                myMapboxMap = mapboxMap;
                mapboxMap.setStyle(Style.SATELLITE_STREETS,
                        new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull final Style style) {
                                enableLocationComponent(style);
                                currentPosition = myMapboxMap.getCameraPosition();
                                mAuth = FirebaseAuth.getInstance();
                                try {
                                    //System.out.println("lon : "+GroupChatActivity.locationLon+" lat : "+GroupChatActivity.locationLat);
                                    IconFactory iconFactory = IconFactory.getInstance(TrackingActivity.this);
                                    MarkerOptions markerOptions = new MarkerOptions()
                                            .position(new LatLng(GroupChatActivity.locationLat, GroupChatActivity.locationLon))
                                            .title(GroupChatActivity.locationName)
                                            .snippet("Event Location")
                                            .icon(iconFactory.fromResource(R.drawable.event_location));

                                    myMapboxMap.addMarker(markerOptions);


                                    btMyLocation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            myMapboxMap.animateCamera(CameraUpdateFactory
                                                    .newCameraPosition(currentPosition), 7000);
                                        }
                                    });


                                    btEventLocation.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            CameraPosition position = new CameraPosition.Builder()
                                                    .target(new LatLng(GroupChatActivity.locationLat, GroupChatActivity.locationLon)) // Sets the new camera position
                                                    .build(); // Creates a CameraPosition from the builder

                                            myMapboxMap.animateCamera(CameraUpdateFactory
                                                    .newCameraPosition(position), 7000);
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                FriendTrackRef = FirebaseDatabase.getInstance().getReference().child("Tracking");

                                MembersRef = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(eventId).child("members");

                                MembersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {
                                            try {

                                                for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                    if (user.getKey() != mAuth.getCurrentUser().getDisplayName())
                                                        listMembers.add(user.getKey());
                                                }
                                                for (final String memberId : listMembers) {
                                                    FriendTrackRef.child(memberId).child("data").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.hasChildren()) {
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    final Cord cord = snapshot.getValue(Cord.class);
                                                                    myMapboxMap.addMarker(new MarkerOptions()
                                                                            .position(new LatLng(cord.getLat(), cord.getLon()))
                                                                            .title(memberId)).setSnippet(new Date(cord.getTime()).toString());

                                                            /*final IconFactory iconFactory = IconFactory.getInstance(TrackingActivity.this);


                                                            Target target = new Target() {
                                                                @Override
                                                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                                    myMapboxMap.addMarker(new MarkerOptions()
                                                                            .position(new LatLng(cord.lat, cord.lon))
                                                                            .icon(iconFactory.fromBitmap(bitmap))
                                                                            .title(memberId)).setSnippet(new Date(cord.time).toString());

                                                                }

                                                                @Override
                                                                public void onBitmapFailed(Drawable errorDrawable) {

                                                                }

                                                                @Override
                                                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                                }
                                                            };*/
                                                           /* Picasso.with(TrackingActivity.this)
                                                                    .load("https://firebasestorage.googleapis.com/v0/b/campsite-90984.appspot.com/o/users%2FMoez_benselem.jpg?alt=media&token=d76d02d1-7127-40e8-bb92-3f4b240eb2dc")
                                                                    .into(target);
                                                           */
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        System.out.println(databaseError.getMessage());
                                    }


                                });
/*
                                FriendTrackRef.child("data").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        Cord cord = null;
                                        routeCoordinates = new ArrayList<>();
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            cord = snapshot.getValue(Cord.class);
                                            System.out.println("lat = " + cord.lat);
                                            System.out.println("lon = " + cord.lon);
                                            routeCoordinates.add(Point.fromLngLat(cord.lon, cord.lat));

                                        }


// Create the LineString from the list of coordinates and then make a GeoJSON
// FeatureCollection so we can add the line to our map as a layer.
                                        style.addSource(new GeoJsonSource("line-source",
                                                FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                                                        LineString.fromLngLats(routeCoordinates)
                                                )})));


// The layer properties for our line. This is where we make the line dotted, set the
// color, etc.
                                        style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                                                PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                                                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                                PropertyFactory.lineWidth(5f),
                                                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))

                                        ));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

*/
                            }

                        });
            }


        });

    }


    @SuppressWarnings({"MissingPermission"})
    public void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = myMapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            myMapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "permission not granted !", Toast.LENGTH_LONG).show();

        }
    }

    private void initRouteCoordinates() {
// Create a list to store our line coordinates.
        routeCoordinates = new ArrayList<>();

    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }
}
