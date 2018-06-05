package com.example.admin.medorg;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class NotificationService extends Service {
    private static final String TAG = "SERVICE_TEST";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        someTask();
        return super.onStartCommand(intent, flags, startId);
    }

    void someTask() {
    }

}
