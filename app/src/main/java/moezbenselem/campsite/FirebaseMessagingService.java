package moezbenselem.campsite;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;
import moezbenselem.campsite.activities.ChatActivity;
import moezbenselem.campsite.activities.UserActivity;

/**
 * Created by Moez on 03/08/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static String theSender;
    String image, sender_id, sender_name, action, notif_title, notif_body;
    Bitmap contactPic = null;
    NotificationCompat.Builder mBuilder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int badgeCount;
    int mNotificationId;
    Intent resultIntent;
    PendingIntent resultPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        mBuilder = new NotificationCompat.Builder(this);

        /*sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        badgeCount = sharedPreferences.getInt("badgeCount",0);*/

        ShortcutBadger.applyCount(getApplicationContext(), badgeCount);

        /*try {
            contactPic = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        return Picasso.with(MainActivity.context).load(image)
                                .resize(200, 200)
                                .placeholder(R.drawable.camp_icon)
                                .error(R.drawable.camp_icon)
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (contactPic != null) {
            mBuilder.setLargeIcon(contactPic);
            //mBuilder.setSmallIcon(BitmapFactory.decodeResource(MainActivity.context.getResources(),contactPic);
        } else {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(MainActivity.context.getResources(), R.drawable.camp_icon));
        }
*/
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {

            //badgeCount++;
            //ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
            //editor.putInt("badgeCount",badgeCount);
            //editor.apply();


            notif_title = remoteMessage.getNotification().getTitle();
            notif_body = remoteMessage.getNotification().getBody();
            action = remoteMessage.getNotification().getClickAction();
            sender_id = remoteMessage.getData().get("sender_id");
            sender_name = remoteMessage.getData().get("sender_name");
            image = remoteMessage.getNotification().getIcon();

            theSender = sender_name;
            System.out.println("the sender id = " + sender_id);
            System.out.println("the sender name = " + sender_name);


            if (image.equalsIgnoreCase("request")) {
                resultIntent = new Intent(this, UserActivity.class);
                resultIntent.putExtra("uid", sender_id);
                resultIntent.putExtra("name", sender_name);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


                resultPendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
            } else {

                resultIntent = new Intent(this, ChatActivity.class);
                resultIntent.putExtra("uid", sender_id);
                resultIntent.putExtra("name", sender_name);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


                resultPendingIntent = PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
            }

            mBuilder
                    .setSmallIcon(R.drawable.camp_icon)
                    .setContentTitle(notif_title)
                    .setContentText(notif_body)
                    .setAutoCancel(true)
                    .setTicker(notif_body)
                    .setContentIntent(resultPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            android.app.Notification notification = mBuilder.build();


            mNotificationId = new Random().nextInt();
            NotificationManager notifMAnager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


// notificationId is a unique int for each notification that you must define
            notifMAnager.notify(mNotificationId, notification);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
