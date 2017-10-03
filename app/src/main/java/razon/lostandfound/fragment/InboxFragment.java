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
import java.util.HashSet;
import java.util.Set;

import razon.lostandfound.R;
import razon.lostandfound.adapter.AdapterInbox;
import razon.lostandfound.model.Inbox;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.SharePreferenceSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {


    private LinearLayout noItem;
    private RecyclerView inboxRecycler;
    ArrayList<Inbox> inboxList;
    LinearLayoutManager layoutManager;
    AdapterInbox adapter;
    String username;

    ValueEventListener valueEventListener;
    DatabaseReference reference;

    ProgressDialog progressDialog;

    ValueEventListener msgListValueEventListener;
    DatabaseReference msgListReference;
    int getlist = 0;
    int getdata = 0;

    Set<Inbox> set;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        initView(view);

        return view;
    }

    public InboxFragment() {
        // Required empty public constructor
    }


    private void initView(View view) {
        username = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
        noItem = (LinearLayout) view.findViewById(R.id.no_item);
        inboxRecycler = (RecyclerView) view.findViewById(R.id.inbox_recycler);
        inboxList = new ArrayList<>();
        set = new HashSet<>();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new AdapterInbox(getActivity(), inboxList);
        inboxRecycler.setLayoutManager(layoutManager);
        inboxRecycler.setAdapter(adapter);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        populateData();
    }

    private void populateData() {

        getlist = 0;

        reference = FirebaseDatabase.getInstance().getReference()
                                    .child(FirebaseEndPoint.USER_INFO)
                                    .child(username)
                                    .child(FirebaseEndPoint.OPPOSIT_LIST);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (getlist == 0) {
                    if (dataSnapshot.getValue() == null) {

                        noItem.setVisibility(View.VISIBLE);
                        inboxRecycler.setVisibility(View.GONE);

                    } else {

                        noItem.setVisibility(View.GONE);
                        inboxRecycler.setVisibility(View.VISIBLE);
                        inboxList.clear();
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            if (msgListValueEventListener != null && msgListReference != null) {
                                msgListReference.removeEventListener(msgListValueEventListener);
                            }
                            getUserData(data.getValue().toString());

                        }



                    }
                    getlist = 1;
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(valueEventListener);


    }

    private void getUserData(final String receiver1) {
        Log.d("opposit", receiver1);
        final int[] getdata = {0};
       DatabaseReference msgListReference = FirebaseDatabase.getInstance().getReference().child(FirebaseEndPoint.USER_INFO).child(receiver1);
        msgListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (getdata[0] == 0) {
                    String name = dataSnapshot.child(FirebaseEndPoint.USER_GENERAL_INFO).child("name").getValue().toString();
                    String designation = dataSnapshot.child(FirebaseEndPoint.USER_GENERAL_INFO).child("designation").getValue().toString();
                    String image = "1";
                    if (dataSnapshot.hasChild("image")) {
                        image = dataSnapshot.child("image").getValue().toString();
                    }

                    Log.d("opposit", name);
                    Inbox inbox = new Inbox(name, receiver1,designation, image);
                    set.add(inbox);
                    getdata[0] = 1;
                    if (set.size()>0){
                        inboxList.clear();
                        inboxList.addAll(set);
                        Collections.reverse(inboxList);
                        adapter.notifyDataSetChanged();
                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //msgListReference.addValueEventListener(msgListValueEventListener);

    }


    @Override
    public void onPause() {

        if (valueEventListener != null && reference != null) {
            reference.removeEventListener(valueEventListener);
        }
        if (msgListValueEventListener != null && msgListReference != null) {
            msgListReference.removeEventListener(msgListValueEventListener);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        if (valueEventListener != null && reference != null) {
            reference.removeEventListener(valueEventListener);
        }
        if (msgListValueEventListener != null && msgListReference != null) {
            msgListReference.removeEventListener(msgListValueEventListener);
        }
        super.onStop();
    }
}
