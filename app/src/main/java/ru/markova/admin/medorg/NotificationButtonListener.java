package ru.markova.admin.medorg;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.TimetableCompleteDao;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationButtonListener extends BroadcastReceiver {

    AppDatabase adb;
    TimetableCompleteDao ttCompleteDao;

    @Override
    public void onReceive(Context context, Intent intent) {
        adb = AppDatabase.getDatabase(context);
        ttCompleteDao = adb.ttCompleteDao();
        long time = intent.getLongExtra("datetime", 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        String action = intent.getAction();
        int nid = intent.getIntExtra("notification_id", 1);

        if(action.equals("skip_action")) {
            ttCompleteDao.updateFromNotification(0, time);
        } else {
            ttCompleteDao.updateFromNotification(1, time);
        }
        notificationManager.cancel(nid);
    }
}
