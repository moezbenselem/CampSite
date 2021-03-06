package moezbenselem.campsite.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.GroupChatActivity;
import moezbenselem.campsite.entities.Cord;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrackFragment extends Fragment implements PermissionsListener {

    private static final String MAP_TOKEN = "sk.eyJ1IjoiYmVuc2VsZW1tb2V6IiwiYSI6ImNrOHp1cXdrMTB2MHozZnM3aTk3dDl5MDQifQ.eOyRaWad8d8b2Bmm5sEPEw";
    public MapboxMap myMapboxMap;
    Button btMyLocation, btEventLocation;
    FirebaseAuth mAuth;
    DatabaseReference FriendTrackRef;
    private MapView mapView;
    CameraPosition currentPosition;
    private PermissionsManager permissionsManager;
    private List<Point> routeCoordinates;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        try {
            Mapbox.getInstance(getContext(), MAP_TOKEN);
            // Inflate the layout for this fragment
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            view = inflater.inflate(R.layout.fragment_map, container, false);
            mAuth = FirebaseAuth.getInstance();
            FriendTrackRef = FirebaseDatabase.getInstance().getReference().child("Tracking").child(mAuth.getCurrentUser().getDisplayName());
            btMyLocation = view.findViewById(R.id.btn_mylocation);
            btEventLocation = view.findViewById(R.id.btn_event_location);

            mapView = view.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);

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
                                    FriendTrackRef.child("data").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                Cord cord = null;
                                                routeCoordinates = new ArrayList<>();
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    cord = snapshot.getValue(Cord.class);
                                                    System.out.println("lat = " + cord.getLat());
                                                    System.out.println("lon = " + cord.getLon());
                                                    routeCoordinates.add(Point.fromLngLat(cord.getLon(), cord.getLat()));

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
                                                        PropertyFactory.lineDasharray(new Float[]{0.01f, 1f}),
                                                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                                                        PropertyFactory.lineWidth(5f),
                                                        PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                                                ));

                                                btMyLocation.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        currentPosition = myMapboxMap.getCameraPosition();
                                                        myMapboxMap.getLocationComponent().setCameraMode(CameraMode.TRACKING);
                                                    }
                                                });

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


                                }
                            });
                }


            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @SuppressWarnings({"MissingPermission"})
    public void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

// Get an instance of the component
            LocationComponent locationComponent = myMapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(TrackFragment.this);
            permissionsManager.requestLocationPermissions(getActivity());
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
            Toast.makeText(getContext(), "permission not granted !", Toast.LENGTH_LONG).show();

        }
    }

    private void initRouteCoordinates() {
// Create a list to store our line coordinates.
        routeCoordinates = new ArrayList<>();

    }

}
