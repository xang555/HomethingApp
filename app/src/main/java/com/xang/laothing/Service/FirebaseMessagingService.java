package com.xang.laothing.Service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.xang.laothing.Activity.MainActivity;
import com.xang.laothing.R;

import java.util.Map;

/**
 * Created by xang on 09/05/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    private static final String CONTENT_TITLE = "content";
    private static final String CONTENT_TEXT = "subcontent";
    private static final String NOTIFY_TITLE = "title";
    private static final int NOTIFY_REQ_CODE = 1995;
    private static final int NOTIFICATION_ID = 998;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String,String> mapmessage = remoteMessage.getData();
        if (mapmessage!=null && mapmessage.size() > 0){
            ShowNotification(mapmessage);
        }

    }


    private void ShowNotification( Map<String,String> msg){

        Intent intent = new Intent(FirebaseMessagingService.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(FirebaseMessagingService.this,NOTIFY_REQ_CODE,intent,0);

        android.support.v4.app.NotificationCompat.Builder notification = new NotificationCompat.Builder(FirebaseMessagingService.this)
                .setTicker(msg.get(NOTIFY_TITLE))
                .setSmallIcon(R.drawable.ic_stat_noti)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.gass_sensor))
                .setContentTitle(msg.get(CONTENT_TITLE))
                .setContentText(msg.get(CONTENT_TEXT))
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.warning));

        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID,notification.build());

    }//show notification


}
