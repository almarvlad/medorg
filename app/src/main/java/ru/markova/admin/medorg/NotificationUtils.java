package ru.markova.admin.medorg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String MEAL_CHANNEL_ID = "ru.markova.admin.medorg.meal";
    public static final String MED_CHANNEL_ID = "ru.markova.admin.medorg.med";
    public static final String MEAL_CHANNEL_NAME = "MEALS CHANNEL";
    public static final String MED_CHANNEL_NAME = "MEDS CHANNEL";

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {
        // create meals channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mealsChannel = new NotificationChannel(MEAL_CHANNEL_ID, MEAL_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            mealsChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            mealsChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            mealsChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            mealsChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(mealsChannel);

            // create meds channel
            NotificationChannel medsChannel = new NotificationChannel(MED_CHANNEL_ID, MED_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            medsChannel.enableLights(true);
            medsChannel.enableVibration(true);
            medsChannel.setLightColor(Color.BLUE);
            medsChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(medsChannel);
        }

    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
}
