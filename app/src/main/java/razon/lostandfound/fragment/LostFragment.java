package razon.lostandfound.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import razon.lostandfound.AdapterLost;
import razon.lostandfound.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LostFragment extends Fragment {


    RecyclerView recyclerView;
    AdapterLost adapter;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lost, container, false);
        initialization(view);
        bindData();
        populateData();

        return view;
    }

    private void bindData() {

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void populateData() {



    }

    private void initialization(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.lost_recycler);
        adapter = new AdapterLost(getActivity());
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

    }

    public LostFragment() {
        // Required empty public constructor
    }


}
