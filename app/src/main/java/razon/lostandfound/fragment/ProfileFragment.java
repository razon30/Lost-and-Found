package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import razon.lostandfound.R;
import razon.lostandfound.activity.AdminActivity;
import razon.lostandfound.activity.HomeActivity;
import razon.lostandfound.activity.LoginActivity;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.UserGeneralInfo;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.FragmentNode;
import razon.lostandfound.utils.MyTextView;
import razon.lostandfound.utils.SharePreferenceSingleton;
import razon.lostandfound.utils.SimpleActivityTransition;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private CircleImageView profileImage;
    private MyTextView name;
    private MyTextView username;
    private MyTextView catagory;
    private MyTextView foundNumber;
    private MyTextView lostNumber;
    private MyTextView email;
    private MyTextView nsuId;
    private MyTextView phone;
    private MyTextView designation;
    private CardView changePhoto;
    private CardView logout;
    private CardView update;

    private static final int RESULT_CANCELED = 0;
    private int GALLERY = 1, CAMERA = 2;

    String imageByte = "1";
    Bitmap thumbnail;

    ValueEventListener valueEventListener;
    DatabaseReference reference;
    ProgressDialog progressDialog;

    String usernameAdmin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        usernameAdmin = SharePreferenceSingleton.getInstance(getActivity()).getString("username");

        if (usernameAdmin.equals("admin12")) {
            worksOnAdmin(view);
            logout.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
        }else {
            logout.setVisibility(View.VISIBLE);
            update.setVisibility(View.VISIBLE);
        }

        populateData();

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharePreferenceSingleton.getInstance(getActivity()).saveString("user", "0");
                SharePreferenceSingleton.getInstance(getActivity()).saveString("username", "0");
                SharePreferenceSingleton.getInstance(getActivity()).saveString("propic", "1");

                SimpleActivityTransition.goToPreviousActivity(getActivity(), LoginActivity.class);
                getActivity().finish();

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MainActivity.class)
                        .putExtra("type", FragmentNode.EDIT_PROFILE);
                //  .putExtra("receiverImage", currentItem.getProPic());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);

            }
        });

        return view;
    }

    private void worksOnAdmin(View view) {
        changePhoto.setVisibility(View.GONE);
        final String currentuserName = getActivity().getIntent().getStringExtra("receiver");
        final String currentuserPass = getActivity().getIntent().getStringExtra("pass");
        final String tempEMail = currentuserName + "@gmail.com";
        CardView delete = (CardView) view.findViewById(R.id.delete);
        delete.setVisibility(View.VISIBLE);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Deleting User..");
                progressDialog.show();

                FirebaseDatabase.getInstance().getReference().child(FirebaseEndPoint.USER_INFO).child(currentuserName).removeValue();
                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(tempEMail, currentuserPass)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (progressDialog!=null){
                                                progressDialog.dismiss();
                                            }

                                            SimpleActivityTransition.goToPreviousActivity(getActivity(), AdminActivity.class);

                                        }
                                    });

                                    if (progressDialog!=null){
                                        progressDialog.dismiss();
                                    }

                                } else {
                                    if (progressDialog!=null){
                                        progressDialog.dismiss();
                                    }
                                }
                            }
                        });

            }
        });

    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false);
        startActivityForResult(photoPickerIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Preparing image..");
                progressDialog.show();

                thumbnail = decodeUriToBitmap(getActivity(), contentURI);

                profileImage.setImageBitmap(thumbnail);

                imageByte = "2";

                progressDialog.dismiss();

            }

        } else if (requestCode == CAMERA) {

            thumbnail = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(thumbnail);

            imageByte = "2";

        }

        sendToServer(thumbnail);

    }

    private void sendToServer(Bitmap thumbnail) {

        if (imageByte.equals("2")) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageByte = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String username1 = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
            SharePreferenceSingleton.getInstance(getActivity()).saveString("propic", imageByte);
            FirebaseDatabase.getInstance().getReference().child(FirebaseEndPoint.USER_INFO)
                    .child(username1)
                    .child(FirebaseEndPoint.IMAGE)
                    .setValue(imageByte);
        }

    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return getBitmap;
    }


    @Override
    public void onPause() {

        if (valueEventListener != null && reference != null) {
            reference.removeEventListener(valueEventListener);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        if (valueEventListener != null && reference != null) {
            reference.removeEventListener(valueEventListener);
        }
        super.onStop();
    }

    private void populateData() {

        String username1 = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
        Log.d("username1", username1);
        if (username1.equals("admin12")) {
            username1 = getActivity().getIntent().getStringExtra("receiver");
            Log.d("username1", username1);
        }

        reference = FirebaseDatabase.getInstance().getReference().child("UserData").child(username1);

        progressDialog.show();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(FirebaseEndPoint.IMAGE)) {
                    String image = dataSnapshot.child(FirebaseEndPoint.IMAGE).getValue().toString();
                    byte[] data = Base64.decode(image, Base64.DEFAULT);

                    final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    profileImage.setImageBitmap(bmp);

                }

                if (dataSnapshot.hasChild(FirebaseEndPoint.LOST)) {
                    String lostCount = String.valueOf(dataSnapshot.child(FirebaseEndPoint.LOST).getChildrenCount());
                    lostNumber.setText(lostCount);
                } else {
                    lostNumber.setText("0");
                }
                if (dataSnapshot.hasChild(FirebaseEndPoint.FOUND)) {
                    String foundCount = String.valueOf(dataSnapshot.child(FirebaseEndPoint.FOUND).getChildrenCount());
                    foundNumber.setText(foundCount);
                } else {
                    foundNumber.setText("0");
                }

                UserGeneralInfo userGeneralInfo = dataSnapshot.child(FirebaseEndPoint.USER_GENERAL_INFO).getValue(UserGeneralInfo.class);


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

        if (userGeneralInfo != null) {
            name.setText(userGeneralInfo.getName());
            username.setText(userGeneralInfo.getUsername());
            catagory.setText(userGeneralInfo.getCatagory());
            email.setText(userGeneralInfo.getEmail());
            nsuId.setText(userGeneralInfo.getNsuId());
            phone.setText(userGeneralInfo.getPhone());
            designation.setText(userGeneralInfo.getDesignation());
        } else {

            Toast.makeText(getActivity(), "Some data is missing, Please reload", Toast.LENGTH_LONG).show();

        }

    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    private void initView(View view) {
        profileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        name = (MyTextView) view.findViewById(R.id.name);
        username = (MyTextView) view.findViewById(R.id.username);
        catagory = (MyTextView) view.findViewById(R.id.catagory);
        foundNumber = (MyTextView) view.findViewById(R.id.found_number);
        lostNumber = (MyTextView) view.findViewById(R.id.lost_number);
        email = (MyTextView) view.findViewById(R.id.email);
        nsuId = (MyTextView) view.findViewById(R.id.nsu_id);
        phone = (MyTextView) view.findViewById(R.id.phone);
        designation = (MyTextView) view.findViewById(R.id.designation);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait..");
        changePhoto = (CardView) view.findViewById(R.id.change_photo);
        logout = (CardView) view.findViewById(R.id.logout);
        update = (CardView) view.findViewById(R.id.update);
    }
}
