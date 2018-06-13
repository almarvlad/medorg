package ru.markova.admin.medorg;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.TimetableCompleteDao;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MYTAG", "HELLO");
        // Create Notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Title")
                        .setContentText("Notification text")
                        .setVibrate(new long[] {0, 1000})
                        .setPriority(PRIORITY_MAX)
                        .setLights(Color.BLUE, 500, 500)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setVisibility(VISIBILITY_PUBLIC);

        Notification notification = builder.build();

// Show Notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

        AppDatabase adb = AppDatabase.getDatabase(context);
        TimetableCompleteDao ttCompleteDao = adb.ttCompleteDao();

        Calendar curr = Calendar.getInstance();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(context, AlarmReceiver.class);
        //intent.putExtra("alarm message", "alarm message");
        Date d1 = new Date(curr.getTimeInMillis());
        Date d2 = new Date(ttCompleteDao.nextAlarmTime(curr.getTimeInMillis()));
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, ttCompleteDao.nextAlarmTime(curr.getTimeInMillis()), pi);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, ttCompleteDao.nextAlarmTime(curr.getTimeInMillis()), pi);
        }
        else {
            am.set(AlarmManager.RTC_WAKEUP, ttCompleteDao.nextAlarmTime(curr.getTimeInMillis()), pi);
        }
    }
}
