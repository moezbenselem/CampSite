package moezbenselem.campsite;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import me.leolin.shortcutbadger.ShortcutBadger;
import moezbenselem.campsite.activities.ChatActivity;
import moezbenselem.campsite.activities.MainActivity;
import moezbenselem.campsite.activities.UserActivity;

/**
 * Created by Moez on 03/08/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    public static String theSender;
    public static int badgeCount = 0;
    String image, sender_id, sender_name, action, notif_title, notif_body, event_id;
    Bitmap contactPic = null;
    NotificationCompat.Builder mBuilder;
    int mNotificationId;
    Intent resultIntent;
    PendingIntent resultPendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "CAMPSITE");

        if (badgeCount >= 0) {
            ShortcutBadger.applyCount(getApplicationContext(), badgeCount);
        } else
            ShortcutBadger.removeCount(getApplicationContext());


    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        try {

            //badgeCount++;
//            System.out.println("badge count : " + badgeCount);
//            ShortcutBadger.applyCount(getApplicationContext(), 0);
            /*notif_title = remoteMessage.getNotification().getTitle();
            notif_body = remoteMessage.getNotification().getBody();
            action = remoteMessage.getNotification().getClickAction();
            image = remoteMessage.getNotification().getIcon();*/

            if (ChatActivity.active_chat_user == null) {

                notif_title = remoteMessage.getData().get("title");
                notif_body = remoteMessage.getData().get("body");
                action = remoteMessage.getData().get("click_action");
                image = remoteMessage.getData().get("icon");

                sender_id = remoteMessage.getData().get("sender_id");
                sender_name = remoteMessage.getData().get("sender_name");

                System.out.println(notif_title);
                System.out.println(notif_body);
                System.out.println(action);

                theSender = sender_name;
            /*System.out.println("notif the sender id = " + sender_id);
            System.out.println("notif the sender name = " + sender_name);*/

                if (remoteMessage.getData().get("event_id") != null) {
                    event_id = remoteMessage.getData().get("event_id");
                    System.out.println(event_id);
                    resultIntent = new Intent(this, MainActivity.class);
                    resultIntent.putExtra("uid", sender_id);
                    resultIntent.putExtra("name", sender_name);
                    resultIntent.putExtra("intent", "notification");
                    resultIntent.putExtra("eventId", event_id);
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    resultPendingIntent = PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );
                } else if (image.equalsIgnoreCase("request")) {

                    resultIntent = new Intent(this, UserActivity.class);
                    resultIntent.putExtra("uid", sender_id);
                    resultIntent.putExtra("name", sender_name);
                    resultIntent.putExtra("intent", "notification");
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    resultPendingIntent = PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );
                } else {

                    resultIntent = new Intent(this, ChatActivity.class);
                    resultIntent.putExtra("uid", sender_id);
                    resultIntent.putExtra("name", sender_name);
                    resultIntent.putExtra("intent", "notification");
                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    resultPendingIntent = PendingIntent.getActivity(
                            getApplicationContext(),
                            0,
                            resultIntent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );
                }

                mBuilder
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.camp_icon)
                        .setContentTitle(notif_title)
                        .setContentText(notif_body)
                        .setAutoCancel(true)
                        .setTicker(notif_body)
                        .setContentIntent(resultPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                android.app.Notification notification = mBuilder.build();

                mNotificationId = new Random().nextInt();

                NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


// notificationId is a unique int for each notification that you must define
                notifManager.notify(mNotificationId, notification);

            }else if(ChatActivity.active_chat_user.equalsIgnoreCase("all")) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(200);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
