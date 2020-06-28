package moezbenselem.campsite.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import moezbenselem.campsite.R;
import moezbenselem.campsite.entities.Cord;
import moezbenselem.campsite.fragments.EventsFragment;

public class LocationActivity extends AppCompatActivity implements PermissionsListener, MapboxMap.OnMapClickListener {

    private static final String MAP_TOKEN = "sk.eyJ1IjoiYmVuc2VsZW1tb2V6IiwiYSI6ImNrOHp1cXdrMTB2MHozZnM3aTk3dDl5MDQifQ.eOyRaWad8d8b2Bmm5sEPEw";
    private static final String API_TOKEN = "pk.eyJ1IjoiYmVuc2VsZW1tb2V6IiwiYSI6ImNrOGR0amFwYTBiNnQzcW4xZTk1MTBhdmMifQ.R7H8rqR3Hd3vMsuJMMCO0g";
    public MapboxMap myMapboxMap;
    String api_url = "https://api.mapbox.com/geocoding/v5/mapbox.places/";
    FirebaseAuth mAuth;
    DatabaseReference FriendTrackRef, MembersRef;
    String eventId, eventName;
    ArrayList<String> listMembers;
    String place_name = "";
    Cord cord = null;
    Marker myMarker;
    Button btSave;
    private MapView mapView;
    private PermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, MAP_TOKEN);
        setContentView(R.layout.activity_location);

        mapView = findViewById(R.id.mapViewLocation);
        mapView.onCreate(savedInstanceState);
        try {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull MapboxMap mapboxMap) {
                    myMapboxMap = mapboxMap;
                    mapboxMap.setStyle(Style.SATELLITE_STREETS,
                            new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull final Style style) {
                                    enableLocationComponent(style);


                                }

                            });
                    myMapboxMap.addOnMapClickListener(LocationActivity.this);
                }


            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        btSave = findViewById(R.id.btn_save_cord);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("cord from location activiry :\n" + cord.getLat() + " " + cord.getLon());
                EventsFragment.locationCord = cord;
                EventsFragment.locationName = place_name;
                setResult(Activity.RESULT_OK);
                finish();
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


    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        System.out.println("map clicked !");
        getLocation(point);
        myMapboxMap.removeAnnotations();
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(point.getLatitude(), point.getLongitude()))
                .title(place_name);
        myMarker = new Marker(markerOptions);
        myMapboxMap.addMarker(markerOptions);

        return true;
    }

    public void getLocation(LatLng point) {
        System.out.println(api_url + point.getLongitude() + "," + point.getLatitude() + ".json?access_token=" + API_TOKEN);
        cord = new Cord();
        cord.setLat(point.getLatitude());
        cord.setLon(point.getLongitude());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api_url + point.getLongitude() + "," + point.getLatitude() + ".json?access_token=" + API_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject json = new JSONObject(response);
                            System.out.println(json.getJSONArray("features").get(0));
                            JSONObject jsonObject = new JSONObject(json.getJSONArray("features").get(0).toString());
                            place_name = jsonObject.get("place_name").toString().substring(jsonObject.get("place_name").toString().indexOf(',') + 1);


                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
            }
        }) {

        };


        {
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }

    }

}
