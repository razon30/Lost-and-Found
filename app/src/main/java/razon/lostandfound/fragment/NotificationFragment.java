package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import razon.lostandfound.R;
import razon.lostandfound.activity.HomeActivity;
import razon.lostandfound.adapter.AdapterNotification;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.model.Notification;
import razon.lostandfound.model.UserGeneralInfo;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.SharePreferenceSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    RecyclerView noti_recycler;
    AdapterNotification adapter;
    LinearLayoutManager layoutManager;
    ArrayList<Notification> arrayList;

    ValueEventListener valueEventListener;
    DatabaseReference reference;

    ProgressDialog progressDialog;
    LinearLayout no_item;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        initView(view);
        populateData();

        return view;
    }

    private void populateData() {

        final String username = SharePreferenceSingleton.getInstance(getActivity()).getString("username");

        reference = FirebaseDatabase.getInstance().getReference().child(FirebaseEndPoint.NOTIFICATION).child(username);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    arrayList.add(postSnapshot.getValue(Notification.class));

                    Log.d("size", postSnapshot.getValue() + "");

                }

                if (arrayList.size() == 0) {
                    noti_recycler.setVisibility(View.GONE);
                    no_item.setVisibility(View.VISIBLE);
                } else {
                    FirebaseDatabase.getInstance().getReference()
                            .child(FirebaseEndPoint.USER_INFO)
                            .child(username)
                            .child(FirebaseEndPoint.NOTI_COUNT)
                            .setValue("0");
                    Collections.reverse(arrayList);
                    noti_recycler.setVisibility(View.VISIBLE);
                    no_item.setVisibility(View.GONE);
                }


                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(valueEventListener);

    }

    @Override
    public void onPause() {

        if (valueEventListener!=null && reference!=null){
            reference.removeEventListener(valueEventListener);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        if (valueEventListener!=null && reference!=null){
            reference.removeEventListener(valueEventListener);
        }
        super.onStop();
    }


    private void initView(View view) {

        no_item = (LinearLayout) view.findViewById(R.id.no_item);
        noti_recycler = (RecyclerView) view.findViewById(R.id.noti_recycler);
        arrayList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new AdapterNotification(getActivity(), arrayList);
        noti_recycler.setLayoutManager(layoutManager);
        noti_recycler.setAdapter(adapter);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

    }

    public NotificationFragment() {
        // Required empty public constructor
    }


}
