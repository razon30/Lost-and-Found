package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.annotations.NonNull;
import razon.lostandfound.R;
import razon.lostandfound.activity.HomeActivity;
import razon.lostandfound.model.UserGeneralInfo;
import razon.lostandfound.utils.EncryptDecrypt;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.SharePreferenceSingleton;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateProfileFragment extends Fragment {

    ValueEventListener valueEventListener;
    DatabaseReference reference;
    ProgressDialog progressDialog;
    private EditText name;
    private EditText username;
    private EditText nsuId;
    private EditText email;
    private EditText pass;
    private EditText passAgain;
    private EditText designation;
    private EditText phone;
    private EditText esixting_pass;
    private Spinner catagorySpinner;
    private TextView update;
    private TextView cancel;

    UserGeneralInfo userGeneralInfo;

    String[] catagory = {"Select One", "Student", "Faculty", "Stuffs"};
    String catagorySelected = catagory[0];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        initView(view);
        populateData();


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


        update.setOnClickListener(v-> submit());

        cancel.setOnClickListener(view1 -> getActivity().finish());

        return view;
    }

    private void populateData() {

        String username1 = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
        reference = FirebaseDatabase.getInstance().getReference().child("UserData").child(username1);

        progressDialog.show();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                userGeneralInfo = dataSnapshot.child(FirebaseEndPoint.USER_GENERAL_INFO).getValue(UserGeneralInfo.class);

                setValue(userGeneralInfo);
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(valueEventListener);

    }

    private void setValue(UserGeneralInfo userGeneralInfo) {

//        String encryptPass;
//        String key = "Bar12345Bar12345"; // 128 bit key
//        String initVector = "RandomInitVector"; // 16 bytes IV
//        encryptPass = EncryptDecrypt.decrypt(key, initVector, userGeneralInfo.getPass());

        this.userGeneralInfo = userGeneralInfo;

        name.setText(userGeneralInfo.getName());
        username.setText(userGeneralInfo.getUsername());
        nsuId.setText(userGeneralInfo.getNsuId());
        email.setText(userGeneralInfo.getEmail());
        designation.setText(userGeneralInfo.getDesignation());
        phone.setText(userGeneralInfo.getPhone());

        //  final String catagoryString = userGeneralInfo.getCatagory();


        update.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.VISIBLE);

    }

    private void initView(View view) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait..");

        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        nsuId = view.findViewById(R.id.nsu_id);
        email = view.findViewById(R.id.email);
        pass = view.findViewById(R.id.pass);
        passAgain = view.findViewById(R.id.pass_again);
        designation = view.findViewById(R.id.designation);
        phone = view.findViewById(R.id.phone);
        esixting_pass = view.findViewById(R.id.esixting_pass);
        catagorySpinner = view.findViewById(R.id.catagory_spinner);
        update = view.findViewById(R.id.update);
        cancel = view.findViewById(R.id.cancel);

        update.setVisibility(View.GONE);
        cancel.setVisibility(View.GONE);

    }

    public UpdateProfileFragment() {
        // Required empty public constructor

    }

    private void submit() {
        // validate
        String nameString = name.getText().toString().trim();
        if (TextUtils.isEmpty(nameString)) {
            nameString = userGeneralInfo.getName();
        }

        String usernameString = username.getText().toString().trim();
        if (TextUtils.isEmpty(usernameString)) {
            usernameString = userGeneralInfo.getUsername();
        }

        String id = nsuId.getText().toString().trim();
        if (TextUtils.isEmpty(id)) {
            id = userGeneralInfo.getNsuId();
        }

        email.setVisibility(View.GONE);
        String emailString = userGeneralInfo.getEmail();

       String encryptPass = null;
//        String key = "Bar12345Bar12345"; // 128 bit key
//        String initVector = "RandomInitVector"; // 16 bytes IV
//        encryptPass = EncryptDecrypt.decrypt(key, initVector, userGeneralInfo.getPass());
//
//        Log.d("encrypt", encryptPass+"  "+userGeneralInfo.getPass());


        String esixting_pass_string = esixting_pass.getText().toString().trim();
        if (TextUtils.isEmpty(esixting_pass_string)){
            esixting_pass.setError("You have to Provide current password");
            Toast.makeText(getActivity(), "You have to Provide current password", Toast.LENGTH_LONG).show();
            return;
        }


        String passString = pass.getText().toString().trim();
        if (TextUtils.isEmpty(passString)) {
            passString = userGeneralInfo.getPass();
        }

        String again = passAgain.getText().toString().trim();
        if (TextUtils.isEmpty(again)) {
            again = userGeneralInfo.getPass();
        }

        String designationString = designation.getText().toString().trim();
        if (TextUtils.isEmpty(designationString)) {
            designationString = userGeneralInfo.getDesignation();
        }

        String phoneString = phone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneString)) {
            phoneString = userGeneralInfo.getPhone();
        }

        if (catagorySelected.equals(catagory[0])){
            Toast.makeText(getContext(), "Please select a catagory", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passString.equals(again)) {
            passAgain.setError("Password Do not match");
            Toast.makeText(getContext(), "Password Do not match", Toast.LENGTH_SHORT).show();
            return;
        } else {

            if (!pass.getText().toString().trim().isEmpty() && !passAgain.getText().toString().trim().isEmpty()){
                String key = "Bar12345Bar12345"; // 128 bit key
                String initVector = "RandomInitVector"; // 16 bytes IV
                encryptPass = EncryptDecrypt.encrypt(key, initVector, passString);

                final UserGeneralInfo userDetailsData = new UserGeneralInfo(nameString, usernameString, id,
                        emailString, encryptPass, designationString, phoneString, catagorySelected);
                final String finalUsernameString = usernameString;
                progressDialog.show();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                final String finalPassString = passString;
                String tempEMail = usernameString+"@gmail.com";
                mAuth.signInWithEmailAndPassword(tempEMail, esixting_pass_string)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                   // Toast.makeText(getActivity(), "I am In "+ finalPassString, Toast.LENGTH_LONG).show();
                                    if (user != null) {
                                        user.updatePassword(finalPassString)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            //Toast.makeText(getActivity(), "I am In too", Toast.LENGTH_LONG).show();

                                                            DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference()
                                                                    .child("UserData").child(finalUsernameString).child("generalInfo");
                                                            mDatabase1.setValue(userDetailsData);
                                                            getActivity().finish();
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_LONG).show();
                                                            startActivity(new Intent(getActivity(), HomeActivity.class));

                                                        }else {
                                                            Log.d("error", task.getException().getLocalizedMessage());
                                                        }
                                                    }
                                                });
                                    }else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "No user found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.

                                    progressDialog.dismiss();

                                  //  if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                        Toast.makeText(getActivity(), task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                   // }


                                }

                            }
                        });


            }else {

                progressDialog.show();

                encryptPass = userGeneralInfo.getPass();

                final UserGeneralInfo userDetailsData = new UserGeneralInfo(nameString, usernameString, id,
                        emailString, encryptPass, designationString, phoneString, catagorySelected);

                DatabaseReference mDatabase1 = FirebaseDatabase.getInstance().getReference()
                        .child("UserData").child(usernameString).child("generalInfo");
                mDatabase1.setValue(userDetailsData);
                getActivity().finish();
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Profile Updated", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getActivity(), HomeActivity.class));


            }

        }






    }
}
