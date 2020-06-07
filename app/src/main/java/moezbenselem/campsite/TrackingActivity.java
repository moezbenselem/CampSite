package moezbenselem.campsite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackingActivity extends AppCompatActivity implements PermissionsListener, MapboxMap.OnMapClickListener {

    private MapView mapView;
    public MapboxMap myMapboxMap;
    private PermissionsManager permissionsManager;
    private List<Point> routeCoordinates;
    FirebaseAuth mAuth;
    DatabaseReference FriendTrackRef, MembersRef;
    String eventId;
    ArrayList<String> listMembers;
    private static final String MAP_TOKEN = "sk.eyJ1IjoiYmVuc2VsZW1tb2V6IiwiYSI6ImNrOHp1cXdrMTB2MHozZnM3aTk3dDl5MDQifQ.eOyRaWad8d8b2Bmm5sEPEw";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MAP_TOKEN);
        setContentView(R.layout.activity_tracking);

        mapView = findViewById(R.id.mapView_activity);
        mapView.onCreate(savedInstanceState);

        eventId = getIntent().getStringExtra("eventId");
        listMembers = new ArrayList<>();
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                myMapboxMap = mapboxMap;
                mapboxMap.setStyle(Style.SATELLITE_STREETS,
                        new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull final Style style) {
                                enableLocationComponent(style);
                                //initRouteCoordinates();

                                mAuth = FirebaseAuth.getInstance();

                                FriendTrackRef = FirebaseDatabase.getInstance().getReference().child("Tracking");

                                MembersRef = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(eventId).child("members");

                                MembersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try {

                                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                                listMembers.add(user.getKey().toString());
                                                //System.out.println("user key : "+user.getKey());
                                            }
                                            for (final String memberId : listMembers) {
                                                FriendTrackRef.child(memberId).child("data").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        Cord cord = null;
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            cord = snapshot.getValue(Cord.class);
                                                            IconFactory iconFactory = IconFactory.getInstance(TrackingActivity.this);
                                                            Icon icon = iconFactory.fromResource(R.drawable.male_avatar);

                                                            myMapboxMap.addMarker(new MarkerOptions()
                                                                .position(new LatLng(cord.lat, cord.lon))
                                                                    //.setIcon(icon)
                                                                .title(memberId)).setSnippet(new Date(cord.time).toString());

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
