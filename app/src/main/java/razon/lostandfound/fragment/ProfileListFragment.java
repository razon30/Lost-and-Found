package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.HashSet;

import razon.lostandfound.R;
import razon.lostandfound.adapter.AdapterInbox;
import razon.lostandfound.adapter.AdapterUserList;
import razon.lostandfound.model.Inbox;
import razon.lostandfound.model.UserGeneralInfo;
import razon.lostandfound.model.UserList;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.SharePreferenceSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileListFragment extends Fragment {
    private LinearLayout noItem;
    private RecyclerView inboxRecycler;
    ArrayList<UserList> inboxList;
    LinearLayoutManager layoutManager;
    AdapterUserList adapter;
    String username;

    ValueEventListener valueEventListener;
    DatabaseReference reference;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_list, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        username = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
        noItem = (LinearLayout) view.findViewById(R.id.no_item);
        inboxRecycler = (RecyclerView) view.findViewById(R.id.inbox_recycler);
        inboxList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new AdapterUserList(getActivity(), inboxList);
        inboxRecycler.setLayoutManager(layoutManager);
        inboxRecycler.setAdapter(adapter);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        populateData();
    }

    private void populateData() {

        reference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEndPoint.USER_INFO);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() == null) {

                        noItem.setVisibility(View.VISIBLE);
                        inboxRecycler.setVisibility(View.GONE);

                    } else {
                        noItem.setVisibility(View.GONE);
                        inboxRecycler.setVisibility(View.VISIBLE);
                        inboxList.clear();
                        for (DataSnapshot datasnap : dataSnapshot.getChildren()) {

                            UserList inbox = new UserList();

                            UserGeneralInfo userGeneralInfo = datasnap.child(FirebaseEndPoint.USER_GENERAL_INFO).getValue(UserGeneralInfo.class);
                            inbox.setUserGeneralInfo(userGeneralInfo);
                            String image = "1";
                            if (datasnap.hasChild("image")) {
                                image = datasnap.child("image").getValue().toString();
                            }
                            inbox.setImage(image);

                            inboxList.add(inbox);
                        }

                        adapter.notifyDataSetChanged();


                    }
                    progressDialog.dismiss();
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(valueEventListener);


    }



    public ProfileListFragment() {
        // Required empty public constructor
    }


}
