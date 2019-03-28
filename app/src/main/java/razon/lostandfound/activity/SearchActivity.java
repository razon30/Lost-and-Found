package razon.lostandfound.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import razon.lostandfound.R;
import razon.lostandfound.adapter.AdapterSearch;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.model.FoundLostItemDb;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.MyApplication;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SearchView mSearchView;
    private RecyclerView recyclerView;
    AdapterSearch adapter;
    LinearLayoutManager layoutManager;

    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;

    DatabaseReference databaseReferenceF;
    ValueEventListener valueEventListenerF;

    ArrayList<FoundLostItemDb> lostList;
    ArrayList<FoundLostItem> lostList0;

   // private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        populateData();

        setupSearchView();

    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search Here...");
    }

    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private void populateData() {

        progressDialog.show();
        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseEndPoint.LOST);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {


                    FoundLostItem foundLostItem = postSnapshot.getValue(FoundLostItem.class);
                    FoundLostItemDb foundLostItemDB = new FoundLostItemDb();
                    foundLostItemDB.setId(foundLostItem != null ? foundLostItem.getId() : null);
                    foundLostItemDB.setUsername(foundLostItem != null ? foundLostItem.getUsername() : null);
                    foundLostItemDB.setName(foundLostItem != null ? foundLostItem.getName() : null);
                    foundLostItemDB.setCaption(foundLostItem != null ? foundLostItem.getCaption() : null);
                    foundLostItemDB.setImage(foundLostItem != null ? foundLostItem.getImage() : null);
                    foundLostItemDB.setTime(foundLostItem != null ? foundLostItem.getTime() : null);
                    foundLostItemDB.setProPic(foundLostItem != null ? foundLostItem.getProPic() : null);
                    foundLostItemDB.setEnable(foundLostItem != null ? foundLostItem.getEnable() : null);
                    foundLostItemDB.setType(FirebaseEndPoint.LOST);


                    lostList.add(foundLostItemDB);

                }


                populateFound();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);

    }

    private void populateFound() {

        databaseReferenceF = FirebaseDatabase.getInstance().getReference(FirebaseEndPoint.FOUND);
        valueEventListenerF = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {



                    FoundLostItem foundLostItem = postSnapshot.getValue(FoundLostItem.class);
                    FoundLostItemDb foundLostItemDB = new FoundLostItemDb();
                    foundLostItemDB.setId(foundLostItem != null ? foundLostItem.getId() : null);
                    foundLostItemDB.setUsername(foundLostItem != null ? foundLostItem.getUsername() : null);
                    foundLostItemDB.setName(foundLostItem != null ? foundLostItem.getName() : null);
                    foundLostItemDB.setCaption(foundLostItem != null ? foundLostItem.getCaption() : null);
                    foundLostItemDB.setImage(foundLostItem != null ? foundLostItem.getImage() : null);
                    foundLostItemDB.setTime(foundLostItem != null ? foundLostItem.getTime() : null);
                    foundLostItemDB.setProPic(foundLostItem != null ? foundLostItem.getProPic() : null);
                    foundLostItemDB.setEnable(foundLostItem != null ? foundLostItem.getEnable() : null);
                    foundLostItemDB.setType(FirebaseEndPoint.FOUND);



                    lostList.add(foundLostItemDB);

                }



               adapter.notifyDataSetChanged();
               progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        };

        databaseReferenceF.addValueEventListener(valueEventListenerF);

    }

    private void initView() {
        mSearchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.recycler_view);
        lostList = new ArrayList<>();
        lostList0 = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new AdapterSearch(SearchActivity.this, lostList);
        recyclerView.setAdapter(adapter);

    }
}
