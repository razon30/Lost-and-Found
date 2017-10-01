package razon.lostandfound.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import razon.lostandfound.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {


    private LinearLayout noItem;
    private RecyclerView inboxRecycler;

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
        noItem = (LinearLayout) view.findViewById(R.id.no_item);
        inboxRecycler = (RecyclerView) view.findViewById(R.id.inbox_recycler);
    }
}
