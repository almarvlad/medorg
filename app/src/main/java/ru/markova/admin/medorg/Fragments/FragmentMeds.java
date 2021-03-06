package ru.markova.admin.medorg.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.markova.admin.medorg.MedAdd;
import ru.markova.admin.medorg.R;
import ru.markova.admin.medorg.Room.MedicineViewModel;
import ru.markova.admin.medorg.Room.UserMedicine;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMeds.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMeds#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMeds extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static final String TAG = "myLogs";
    private MedicineViewModel mMedicineViewModel;
    public static final int NEW_WORD_ACTIVITY_REQUEST_CODE = 1;

    public FragmentMeds() {
        // Required empty public constructor
    }

    public static FragmentMeds newInstance(String param1, String param2) {
        FragmentMeds fragment = new FragmentMeds();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Менять заголовок
        //((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Лекарства");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // связываем с файлом вёрстки
        final View v = inflater.inflate(ru.markova.admin.medorg.R.layout.fragment_meds, container, false);

        final RecyclerView recyclerView = v.findViewById(ru.markova.admin.medorg.R.id.recycler_meds); // наш список медикаментов
        final MedicineListAdapter adapter = new MedicineListAdapter(getActivity()); // адаптер для recyclerview
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mMedicineViewModel = ViewModelProviders.of(this).get(MedicineViewModel.class);
        mMedicineViewModel.getAllMeds().observe(this, new Observer<List<UserMedicine>>() {
            @Override
            public void onChanged(@Nullable final List<UserMedicine> words) {
                adapter.setWords(words); // обновить кэш-копию слов в репозитории
                // отображение замещающего текста в случае, если лекарств нет
                if (recyclerView.getAdapter().getItemCount() == 0) {
                    v.findViewById(R.id.no_meds).setVisibility(View.VISIBLE);
                } else v.findViewById(R.id.no_meds).setVisibility(View.GONE);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(ru.markova.admin.medorg.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // переход на активити с созданием нового лекарства
                Intent intent = new Intent(getActivity(), MedAdd.class);
                startActivity(intent);
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    */

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    // а это нужно?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
