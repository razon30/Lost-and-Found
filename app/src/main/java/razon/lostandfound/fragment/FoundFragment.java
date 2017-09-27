package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import razon.lostandfound.adapter.AdapterLost;
import razon.lostandfound.R;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.FragmentNode;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoundFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterLost adapter;
    LinearLayoutManager layoutManager;

    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    ArrayList<FoundLostItem> lostList;
    LinearLayout no_item;
    ProgressDialog progressDialouge;

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
        databaseReference = FirebaseDatabase.getInstance().getReference().child(FirebaseEndPoint.FOUND);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    lostList.add(postSnapshot.getValue(FoundLostItem.class));

                    Log.d("size", postSnapshot.getValue()+"");

                }

                if (lostList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    no_item.setVisibility(View.VISIBLE);
                } else {
                    Collections.reverse(lostList);
                    recyclerView.setVisibility(View.VISIBLE);
                    no_item.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                progressDialouge.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);

    }

    @Override
    public void onPause() {

        if (valueEventListener!=null && databaseReference!=null){
            databaseReference.removeEventListener(valueEventListener);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        if (valueEventListener!=null && databaseReference!=null){
            databaseReference.removeEventListener(valueEventListener);
        }
        super.onStop();
    }

    private void initialization(View view) {

        no_item = (LinearLayout) view.findViewById(R.id.no_item);
        recyclerView = (RecyclerView) view.findViewById(R.id.lost_recycler);
        lostList = new ArrayList<>();
        adapter = new AdapterLost(getActivity(), lostList, FragmentNode.FOUND);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        progressDialouge = new ProgressDialog(getActivity());
        progressDialouge.setMessage("Please Wait...");
        progressDialouge.show();

    }

    public FoundFragment() {
        // Required empty public constructor
    }



}
