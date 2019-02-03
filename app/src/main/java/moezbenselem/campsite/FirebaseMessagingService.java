package moezbenselem.campsite;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

/**
 * Created by Moez on 03/08/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notif_title = remoteMessage.getNotification().getTitle();
        String notif_body = remoteMessage.getNotification().getBody();
        String action = remoteMessage.getNotification().getClickAction();
        String sender_id = remoteMessage.getData().get("sender_id");
        String sender_name = remoteMessage.getData().get("sender_name");
        System.out.println("the sender id = "+sender_id);
        System.out.println("the sender name = "+sender_name);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.camp_icon)
                .setContentTitle(notif_title)
                .setContentText(notif_body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        Intent resultIntent = new Intent(action);
        resultIntent.putExtra("uid",sender_id);
        resultIntent.putExtra("name",sender_name);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(resultPendingIntent);

        int mNotificationId = new Random().nextInt();
        NotificationManager notifMAnager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

// notificationId is a unique int for each notification that you must define
        notifMAnager.notify(mNotificationId, mBuilder.build());




    }
}
