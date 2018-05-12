package com.example.admin.medorg.Room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {UserMedicine.class, NonCompatMeds.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DBDao Dao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "word_database")
                            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
                            .allowMainThreadQueries()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final DBDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.Dao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            return null;
        }
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE medicals ADD COLUMN time_per,REAL;" +
                    " course_start,LONG;" +
                    " Duration,INTEGER;" +
                    " Weekdays,VARCHAR(7);" +
                    " Dose,REAL;" +
                    " DoseForm,VARCHAR;" +
                    " Instruct,TINYINT;" +
                    " AddInstruct,VARCHAR(255);" +
                    " IsActive,BOOLEAN DEFAULT 0 NOT NULL");
        }
    };
    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(final SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE medicals ADD COLUMN timeType,BOOLEAN DEFAULT 0 NOT NULL");
        }
    };
}
