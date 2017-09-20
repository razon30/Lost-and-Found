package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import razon.lostandfound.R;
import razon.lostandfound.model.UserGeneralInfo;
import razon.lostandfound.activity.HomeActivity;
import razon.lostandfound.utils.SharePreferenceSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {


    private TextView signUp;
    private TextView logIn;
    String[] catagory = {"Select One", "Student", "Faculty", "Stuffs"};
    private EditText name;
    private EditText username;
    private EditText nsuId;
    private EditText email;
    private EditText pass;
    private EditText passAgain;
    private EditText designation;
    private EditText phone;
    private Spinner catagorySpinner;

    String catagorySelected = catagory[0];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initView(view);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, catagory);
        catagorySpinner.setAdapter(adapter);

        catagorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                catagorySelected = catagory[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                catagorySelected = catagory[0];
            }
        });


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

                submit();

              //  getActivity().startActivity(new Intent(getActivity(), HomeActivity.class));

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
        name = (EditText) view.findViewById(R.id.name);
        username = (EditText) view.findViewById(R.id.username);
        nsuId = (EditText) view.findViewById(R.id.nsu_id);
        email = (EditText) view.findViewById(R.id.email);
        pass = (EditText) view.findViewById(R.id.pass);
        passAgain = (EditText) view.findViewById(R.id.pass_again);
        designation = (EditText) view.findViewById(R.id.designation);
        phone = (EditText) view.findViewById(R.id.phone);
        catagorySpinner = (Spinner) view.findViewById(R.id.catagory_spinner);
    }

    private void submit() {
        // validate
        String nameString = name.getText().toString().trim();
        if (TextUtils.isEmpty(nameString)) {
            name.setError("Name is Required");
            Toast.makeText(getContext(), "Name is Required", Toast.LENGTH_SHORT).show();
            return;
        }

        final String usernameString = username.getText().toString().trim();
        if (TextUtils.isEmpty(usernameString)) {
            username.setError("Username is Required");
            Toast.makeText(getContext(), "Username is Required", Toast.LENGTH_SHORT).show();
            return;
        }else if (usernameString.contains(".") || usernameString.contains("#") || usernameString.contains("$")
                || usernameString.contains("[") || usernameString.contains("]")){

            username.setError("Use only alphabate and numbers");
            Toast.makeText(getContext(), "Use only alphabate and numbers", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = nsuId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            nsuId.setError("NSU ID is Required");
            Toast.makeText(getContext(), "NSU ID is Required", Toast.LENGTH_SHORT).show();
            return;
        }else if (id.length()<10){
            nsuId.setError("NSU ID must be more than 9 digit");
            Toast.makeText(getContext(), "NSU ID must be more than 9 digit", Toast.LENGTH_SHORT).show();
            return;
        }

        String emailString = email.getText().toString().trim();
        if (TextUtils.isEmpty(emailString)) {
            email.setError("Email is Required");
            Toast.makeText(getContext(), "Email is Required", Toast.LENGTH_SHORT).show();
            return;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(emailString).matches()){
            email.setError("Enter a valid mail");
            Toast.makeText(getContext(), "Enter a valid mail", Toast.LENGTH_SHORT).show();
            return;
        }else if (!emailString.split("@")[1].equals("northsouth.edu")){

            email.setError("Invalid email address");
            Toast.makeText(getContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
            return;

        }

        final String passString = pass.getText().toString().trim();
        if (TextUtils.isEmpty(passString)) {
            pass.setError("Password is Required");
            Toast.makeText(getContext(), "Password is Required", Toast.LENGTH_SHORT).show();
            return;
        }

        String again = passAgain.getText().toString().trim();
        if (!passString.equals(again)) {
            passAgain.setError("Password Do not match");
            Toast.makeText(getContext(), "Password Do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        String designationString = designation.getText().toString().trim();
        if (TextUtils.isEmpty(designationString)) {
            designation.setError("Designation is Required");
            Toast.makeText(getContext(), "Designation is Required", Toast.LENGTH_SHORT).show();
            return;
        }

        String phoneString = phone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneString)) {
            phone.setError("Phone number is Required");
            Toast.makeText(getContext(), "Phone number is Required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (catagorySelected.equals(catagory[0])){
            Toast.makeText(getContext(), "Please select a catagory", Toast.LENGTH_SHORT).show();
            return;
        }


        final UserGeneralInfo userDetailsData = new UserGeneralInfo(nameString, usernameString, id,
                emailString, passString, designationString, phoneString, catagorySelected);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Signing In");
        progressDialog.show();

        String tempEMail = usernameString+"@gmail.com";

        mAuth.createUserWithEmailAndPassword(tempEMail, passString)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            SharePreferenceSingleton.getInstance(getActivity()).saveString("user","1");
                            SharePreferenceSingleton.getInstance(getActivity()).saveString("username",usernameString);

                            DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference()
                                    .child("UserData").child(usernameString).child("generalInfo");
                            mDatabase1.setValue(userDetailsData);
                            getActivity().finish();
                            progressDialog.dismiss();
                            startActivity(new Intent(getActivity(), HomeActivity.class));


                        } else {

                            progressDialog.dismiss();

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getActivity(), "User with this username already exist.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });



    }
}
