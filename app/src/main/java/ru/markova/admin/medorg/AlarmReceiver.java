package ru.markova.admin.medorg;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import ru.markova.admin.medorg.Fragments.FragmentMeds;
import ru.markova.admin.medorg.Fragments.TakeMedDialogFragment;
import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.TimetableComplete;
import ru.markova.admin.medorg.Room.TimetableCompleteDao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;
import static android.support.v4.app.NotificationCompat.VISIBILITY_PUBLIC;

public class AlarmReceiver extends BroadcastReceiver {
    AppDatabase adb;
    TimetableCompleteDao ttCompleteDao;
    MedicineDao medicineDao;

    @Override
    public void onReceive(Context context, Intent intent) {
        adb = AppDatabase.getDatabase(context);
        ttCompleteDao = adb.ttCompleteDao();
        medicineDao = adb.Dao();

        int notId = 1;

        long currTime = intent.getLongExtra("dateTime", -1);
        List<TimetableComplete> meds = ttCompleteDao.selectMedsAtTime(currTime);
        String title;
        String notifyText = "";
        if (meds.size()>0){
            title = "У Вас запланировано " + meds.size() + " ";
            title += (meds.size() == 1) ? "лекарство" : (meds.size() > 1 && meds.size() < 5) ? "лекарства" : "лекарств";
            for (TimetableComplete medTime: meds) {
                notifyText += medicineDao.getById(medTime.getIdMed()).getName() + ", ";
            }
            notifyText = notifyText.substring(0, notifyText.length()-2);
        } else {
            title = "Приём пищи";
            notifyText = "Соблюдение режима питания вместе с приёмом лекарств способствует успешному лечению.";
        }

        // Create PendingIntent
        Intent resultIntent = new Intent(context, MainActivity.class);
        //resultIntent.putExtra(EXTRA_ITEM_ID, itemId);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d("MYTAG", "HELLO");
        // Create Notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(notifyText)
                        .setVibrate(new long[] {0, 1000})
                        .setOngoing(true)
                        .setContentIntent(resultPendingIntent)
                        .setPriority(PRIORITY_MAX)
                        //.setGroup(GROUP_KEY)
                        .setLights(Color.BLUE, 500, 500)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(notifyText))
                        .setVisibility(VISIBILITY_PUBLIC);

        if (meds.size() > 0) {
            Intent skipIntent = new Intent(context, NotificationButtonListener.class);
            skipIntent.putExtra("datetime", currTime);
            skipIntent.setAction("skip_action");
            skipIntent.putExtra("notification_id", notId);
            PendingIntent skipPendingIntent = PendingIntent.getBroadcast(context, 0, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_cancel_all_40, "Пропустить все", skipPendingIntent);

            Intent takeIntent = new Intent(context, NotificationButtonListener.class);
            takeIntent.putExtra("datetime", currTime);
            takeIntent.putExtra("notification_id", notId);
            takeIntent.setAction("done_action"); // ЭЭЭЭ?
            PendingIntent takePendingIntent = PendingIntent.getService(context, 0, takeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.drawable.ic_done_all_40, "Принять все", takePendingIntent);

        }
        Notification notification = builder.build();

// Show Notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        /*
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "My channel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("My channel description");
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
        */
        notificationManager.notify(notId, notification);



        Calendar curr = Calendar.getInstance();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent1 = new Intent(context, AlarmReceiver.class);
        intent1.putExtra("dateTime", ttCompleteDao.nextAlarmTime(curr.getTimeInMillis()));
//        Date d1 = new Date(curr.getTimeInMillis());
//        Date d2 = new Date(ttCompleteDao.nextAlarmTime(curr.getTimeInMillis()));
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
