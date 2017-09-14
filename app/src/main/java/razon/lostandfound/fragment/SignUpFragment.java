package razon.lostandfound.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import razon.lostandfound.R;
import razon.lostandfound.activity.HomeActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    private TextView signUp;
    private TextView logIn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initView(view);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment()).commit();

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));

            }
        });

        return view;
    }


    public SignUpFragment() {
        // Required empty public constructor
    }

    private void initView(View view) {
        signUp = (TextView) view.findViewById(R.id.sign_up);
        logIn = (TextView) view.findViewById(R.id.log_in);
    }
}
