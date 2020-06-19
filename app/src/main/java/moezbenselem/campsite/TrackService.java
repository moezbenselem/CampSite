package moezbenselem.campsite;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class TrackService extends Service {
    Double lat = 0.0, lon = 0.0;
    Location loc;
    String provider;
    LocationManager locationManager;
    String token;
    FirebaseAuth mAuth;
    DatabaseReference myTrackRef;
    LocationListener mLocationListener;
    private Timer timer;
    private TimerTask timerTask;


    public TrackService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) TrackService.this.getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        startForeground(1, new Notification());
        System.out.println("service started");
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationListener mLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    loc = location;
                    lon = location.getLongitude();
                    lat = location.getLatitude();

                    System.out.println("accuracy === " + loc.getAccuracy());

                    System.out.println("longgggg from listner ======  " + lon);
                    System.out.println("laaatttt from listner ======  " + lat);
                    Map updateHashMap = new HashMap();
                    updateHashMap.put("lon", lon);
                    updateHashMap.put("lat", lat);
                    updateHashMap.put("time", ServerValue.TIMESTAMP);
                    myTrackRef.child("data").push().setValue(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                                System.out.println("Data Added from listner");
                            else
                                System.out.println("ERROR FIREBASE FROM SERVICE");
                        }
                    });

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            loc = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000,
                    500, mLocationListener);

            mAuth = FirebaseAuth.getInstance();
            myTrackRef = FirebaseDatabase.getInstance().getReference().child("Tracking").child(mAuth.getCurrentUser().getDisplayName());

        } catch (Exception fire) {
            fire.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        //startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {

            System.out.println("service destroyed");
            stoptimertask();
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void startTimer() {
        timer = new Timer();

        timerTask = new TimerTask() {
            public void run() {
                Context context = TrackService.this;


                try {


                    loc = locationManager.getLastKnownLocation(provider);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                            5, mLocationListener);
                    if (loc != null) {

                        lat = loc.getLatitude();
                        lon = loc.getLongitude();
                        System.out.println("longgggg SERVICE ======  " + lon);
                        System.out.println("laaatttt SERVICE ======  " + lat);
                        Map updateHashMap = new HashMap();
                        updateHashMap.put("lon", lon);
                        updateHashMap.put("lat", lat);
                        updateHashMap.put("time", ServerValue.TIMESTAMP);
                        myTrackRef.child("data").push().setValue(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    System.out.println("Data Added from timer");
                                else
                                    System.out.println("ERROR FIREBASE FROM SERVICE");
                            }
                        });
                        ;

                    }

                } catch (SecurityException e) {
                    e.printStackTrace();
                }

            }
        };
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void stoptimertask() {
        try {


            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
