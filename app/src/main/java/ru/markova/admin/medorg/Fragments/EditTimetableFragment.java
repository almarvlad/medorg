package ru.markova.admin.medorg.Fragments;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ru.markova.admin.medorg.R;
import ru.markova.admin.medorg.Room.AppDatabase;
import ru.markova.admin.medorg.Room.MedicineDao;
import ru.markova.admin.medorg.Room.NonCompatMeds;
import ru.markova.admin.medorg.Room.Timetable;
import ru.markova.admin.medorg.Room.TimetableCompleteDao;
import ru.markova.admin.medorg.Room.TimetableDao;
import ru.markova.admin.medorg.Room.UserMedicine;
import ru.markova.admin.medorg.TimetableMaker;


public class EditTimetableFragment extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";
    static final String TAG = "DRAGNDROP";
    int pageNumber;

    TimetableMaker ttMaker;
    int db, de;

    public List<MyItem> getItems() {
        return mItems;
    }

    List<MyItem> mItems;

    AppDatabase adb = AppDatabase.getDatabase(getContext());
    TimetableDao ttDao = adb.ttDao();
    TimetableCompleteDao ttCompleteDao = adb.ttCompleteDao();
    MedicineDao mDao = adb.Dao();

//    private RecyclerView.LayoutManager mLayoutManager;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.Adapter mWrappedAdapter;
//    private RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    public static EditTimetableFragment newInstance(int page) {
        EditTimetableFragment EditTimetableFragment = new EditTimetableFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        EditTimetableFragment.setArguments(arguments);
        return EditTimetableFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
        ttMaker = TimetableMaker.getInstance(getContext());

        int savedPageNumber = -1;
        if (savedInstanceState != null) {
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
        }

        db = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("day_begin", 360);
        de = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("day_end", 1320);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_timetable_fragment, null);

        List<Timetable> wdaytt= ttDao.getWeekdayTimetable(getArguments().getInt(ARGUMENT_PAGE_NUMBER) + 1);
                
        RecyclerView recyclerView = view.findViewById(R.id.timetable_recyclerview);

        // Setup D&D feature and RecyclerView
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();

        dragMgr.setInitiateOnMove(false);
        dragMgr.setInitiateOnLongPress(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(dragMgr.createWrappedAdapter(new MyAdapter(wdaytt)));

        dragMgr.attachRecyclerView(recyclerView);
        return view;
    }

    static class MyItem {
        public final long id;
        public final int mark; 
        public final String text; //название лекарства
        public String timeStr;
        public int timeInt;
        public long id_DB;

        public MyItem(long id, String text, int time, int mark, long iddb) {
            this.id = id;
            this.text = text; //название
            this.timeInt = time;
            this.mark = mark;
            this.timeStr = getTimeStr(time);
            this.id_DB = iddb;
        }

        public String getTimeStr() {
            return timeStr;
        }

        public void setTimeStr(int time) {
            this.timeStr = getTimeStr(time);
        }

        public int getTimeInt() {
            return timeInt;
        }

        public void setTimeInt(int timeInt) {
            this.timeInt = timeInt;
        }

        String getTimeStr(int time) {
            int hour = time / 60;
            int min = time % 60;
            String h, m;
            if (hour < 10)
                h = "0"+hour;
            else h = Integer.toString(hour);
            if (min < 10)
                m = "0" + min;
            else m = Integer.toString(min);;
            return h + ":" + m;
        }
    }

    class MyViewHolder extends AbstractDraggableItemViewHolder {
        RelativeLayout rl;
        TextView textView;
        TextView time;

        public MyViewHolder(View itemView) {
            super(itemView);
            rl = (RelativeLayout) itemView;
            textView = itemView.findViewById(R.id.text);
            time = itemView.findViewById(R.id.time_mark);
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int h = mItems.get(position).getTimeInt() / 60;
                    int m = mItems.get(position).getTimeInt() % 60;
                    new TimePickerDialog(getActivity(), t, h, m, true)
                                .show();
                }
            });
        }

        // установка обработчика выбора времени
        TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int position = getAdapterPosition();
                int timeall = hourOfDay*60 + minute;
                mItems.get(position).setTimeInt(timeall);
                mItems.get(position).setTimeStr(timeall);
                time.setText(mItems.get(position).timeStr);

                ttDao.updateRowTimetable(timeall, mItems.get(position).id_DB);
                new updateTimeAsyncTask(ttCompleteDao, getArguments().getInt(ARGUMENT_PAGE_NUMBER)+1, timeall/60, timeall%60, position).execute();

                ttMaker.createNextAlarm();
            }
        };
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> /*implements DraggableItemAdapter<MyViewHolder> */{


        public MyAdapter(List<Timetable> tt) {
            setHasStableIds(true); // this is required for D&D feature.

            mItems = new ArrayList<>();
            int meals = 1;
            for (int i = 0; i < tt.size(); i++) {
                if (tt.get(i).getMark() > -1)
                    mItems.add(new MyItem(i, mDao.getById(tt.get(i).getMark()).getName(), tt.get(i).getTime(), tt.get(i).getMark(), tt.get(i).getId() ));
                else {
                    String s = meals + " приём пищи";
                    mItems.add(new MyItem(i, s, tt.get(i).getTime(), tt.get(i).getMark(), tt.get(i).getId() ));
                    meals++;
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).id; // need to return stable (= not change even after reordered) value
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_minimal, parent, false);
            return new MyViewHolder(v.findViewById(R.id.timetable_row));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MyItem item = mItems.get(position);
            holder.textView.setText(item.text);
            holder.time.setText(item.timeStr);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        /*
        @Override
        public void onMoveItem(int fromPosition, int toPosition) { //срабатывает по окончанию перемещения
            MyItem movedItem = mItems.remove(fromPosition);
            mItems.add(toPosition, movedItem);
            //Log.d(TAG, "fromPosition " + fromPosition + " ; toPosition " + toPosition);
        }

        @Override
        public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) { //вычисляет позицию элемента,
                                                                                                // который сейчас будут двигать
            return true;
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) { // тоже определяет стартовую позицию
            //Log.d(TAG, "onGetItemDraggableRange position " + position);
            return null;
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) { // вообще здесь не работает???
            //Log.d(TAG, "onCheckCanDrop draggingPosition " + draggingPosition + " ; dropPosition " + dropPosition);
            return true;
        }

        @Override
        public void onItemDragStarted(int position) { // находит позицию, с которой началось перемещение
            //Log.d(TAG, "onItemDragStarted position " + position);
            //notifyDataSetChanged();
        }

        @Override
        public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
            Log.d(TAG, "from " + fromPosition + " to " + toPosition);
            int prev, next, t;
            if (toPosition == 0) {
                next = mItems.get(toPosition + 1).timeInt;
                t = next - 30;
                if (t < db)
                    t = db;
                mItems.get(toPosition).setTimeInt(t);
                mItems.get(toPosition).setTimeStr(t);
            } else if (toPosition == mItems.size() - 1) {
                prev = mItems.get(toPosition - 1).timeInt;
                t = prev + 30;
                if (t > de && de>0)
                    t = de;
                mItems.get(toPosition).setTimeInt(t);
                mItems.get(toPosition).setTimeStr(t);
            } else {
                next = mItems.get(toPosition + 1).timeInt;
                prev = mItems.get(toPosition - 1).timeInt;
                t = next - ((next - prev) / 2);
                mItems.get(toPosition).setTimeInt(t);
                mItems.get(toPosition).setTimeStr(t);

            }
            ttDao.updateRowTimetable(t, mItems.get(toPosition).id_DB);
            new updateTimeAsyncTask(ttCompleteDao, getArguments().getInt(ARGUMENT_PAGE_NUMBER)+1, t/60, t%60, toPosition).execute();
            //notifyDataSetChanged();
        }
        */
    }

    //
    private static class updateTimeAsyncTask extends AsyncTask<Void, Void, Void> {
        private final TimetableCompleteDao ttCompleteDao;
        private int wday;
        private int hour, minute, position;

        updateTimeAsyncTask(TimetableCompleteDao ttCompleteDao, int weekday, int hour, int min, int newpos) {
            this.ttCompleteDao = ttCompleteDao;
            wday = weekday;
            this.hour = hour;
            minute = min;
            position = newpos;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Calendar date1 = Calendar.getInstance();
            Calendar date2 = Calendar.getInstance();
            date2.add(Calendar.MONTH, 1); // дата через месяц
            //int nowTime = date1.get(Calendar.HOUR_OF_DAY)*60 + date1.get(Calendar.MINUTE);

            while (!date1.after(date2)) {
                int dayNumber = (date1.get(Calendar.DAY_OF_WEEK) - 1 > 0) ? date1.get(Calendar.DAY_OF_WEEK) - 1 : 7; // находим день недели текущей даты

                if (dayNumber == wday) {
                    Calendar newTime = new GregorianCalendar(date1.get(Calendar.YEAR), date1.get(Calendar.MONTH), date1.get(Calendar.DAY_OF_MONTH), hour, minute, 0);
                    //текущая дата в цикле
                    Calendar date_one = new GregorianCalendar(date1.get(Calendar.YEAR), date1.get(Calendar.MONTH), date1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                    // текущая дата в цикле + 1 день
                    Calendar date_two = new GregorianCalendar(date1.get(Calendar.YEAR), date1.get(Calendar.MONTH), date1.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                    ttCompleteDao.updateTime(newTime.getTimeInMillis(), date_one.getTimeInMillis(), date_two.getTimeInMillis(), position);
                }

                // переходим к сл дню - увеличиваем переменную цикла
                date1.add(Calendar.DATE, 1); // переходим к сл дню
            }

            return null;
        }
    }
}
