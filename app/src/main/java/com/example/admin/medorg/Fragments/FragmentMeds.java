package com.example.admin.medorg.Fragments;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.admin.medorg.R;
import com.example.admin.medorg.Room.AppDatabase;
import com.example.admin.medorg.Room.UserMedicine;

import java.util.ArrayList;
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
    RecyclerView recMeds;
    RecyclerView.Adapter recAdapter;
//    ArrayList<UserMedicine> meds;

    public FragmentMeds() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMeds.
     */
    // TODO: Rename and change types and number of parameters
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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_meds, container, false);

        recMeds = (RecyclerView) v.findViewById(R.id.recycler_meds);
//        meds = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            UserMedicine med = new UserMedicine("name", 45);
//            meds.add(med);
//        }

        AppDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "production")
                .allowMainThreadQueries()
                .build();

        List<UserMedicine> meds = db.Dao().getAllMeds();

        recMeds.setLayoutManager(new LinearLayoutManager(getActivity()));
        recAdapter = new UserAdapter(meds);
        recMeds.setAdapter(recAdapter);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick pressed!");
                Intent intent = new Intent(getActivity(), com.example.admin.medorg.MedEdit.class);
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
}
