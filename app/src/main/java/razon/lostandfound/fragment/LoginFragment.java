package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import razon.lostandfound.R;
import razon.lostandfound.activity.HomeActivity;
import razon.lostandfound.utils.SharePreferenceSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    private TextView logIn;
    private TextView signUp;
    private EditText username;
    private EditText pass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initView(view);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SignUpFragment()).commit();

            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submit();
                // getActivity().startActivity(new_item Intent(getActivity(), HomeActivity.class));

            }
        });

        return view;
    }


    public LoginFragment() {
        // Required empty public constructor
    }

    private void initView(View view) {
        logIn = (TextView) view.findViewById(R.id.log_in);
        signUp = (TextView) view.findViewById(R.id.sign_up);
        username = (EditText) view.findViewById(R.id.username);
        pass = (EditText) view.findViewById(R.id.pass);
    }

    private void submit() {
        // validate
        final String usernameString = username.getText().toString().trim();
        if (TextUtils.isEmpty(usernameString)) {
            username.setError("Username is Required");
            Toast.makeText(getContext(), "Username is Required", Toast.LENGTH_SHORT).show();
            return;
        }

        String passString = pass.getText().toString().trim();
        if (TextUtils.isEmpty(passString)) {
            pass.setError("Password is Required");
            Toast.makeText(getContext(), "Password is Required", Toast.LENGTH_SHORT).show();
            return;
        }

        String tempEMail = usernameString + "@gmail.com";

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Logging In");
        progressDialog.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(tempEMail, passString)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            SharePreferenceSingleton.getInstance(getActivity()).saveString("user","1");
                            SharePreferenceSingleton.getInstance(getActivity()).saveString("username",usernameString);
                            SharePreferenceSingleton.getInstance(getActivity()).saveString("propic","1");

                            progressDialog.dismiss();
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                            getActivity().finish();


                        } else {
                            // If sign in fails, display a message to the user.

                            progressDialog.dismiss();

                            if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(getActivity(), "Username Do not exists, please register.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }


                        }

                    }
                });


    }
}
