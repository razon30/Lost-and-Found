package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import razon.lostandfound.activity.HomeActivity;
import razon.lostandfound.activity.LoginActivity;
import razon.lostandfound.utils.FirebaseEndPoint;
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

    private static final int RESULT_CANCELED = 0;
    private int GALLERY = 1, CAMERA = 2;

    String imageByte = "1";
    Bitmap thumbnail;

    ValueEventListener valueEventListener;
    DatabaseReference reference;
    ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
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

                SharePreferenceSingleton.getInstance(getActivity()).saveString("user","0");
                SharePreferenceSingleton.getInstance(getActivity()).saveString("username","0");
                SharePreferenceSingleton.getInstance(getActivity()).saveString("propic","1");

                SimpleActivityTransition.goToPreviousActivity(getActivity(), LoginActivity.class);
                getActivity().finish();

            }
        });

        return view;
    }


    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
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

        if (imageByte.equals("2")){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            imageByte = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String username1 = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
            SharePreferenceSingleton.getInstance(getActivity()).saveString("propic",imageByte);
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

    private void populateData() {

        String username1 = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
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
                }else {
                    lostNumber.setText("0");
                }
                if (dataSnapshot.hasChild(FirebaseEndPoint.FOUND)) {
                    String foundCount = String.valueOf(dataSnapshot.child(FirebaseEndPoint.FOUND).getChildrenCount());
                    foundNumber.setText(foundCount);
                }else {
                    foundNumber.setText("0");
                }


                setValue();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(valueEventListener);

    }

    private void setValue() {

        name.setText(HomeActivity.userGeneralInfo.getName());
        username.setText(HomeActivity.userGeneralInfo.getUsername());
        catagory.setText(HomeActivity.userGeneralInfo.getCatagory());
        email.setText(HomeActivity.userGeneralInfo.getEmail());
        nsuId.setText(HomeActivity.userGeneralInfo.getNsuId());
        phone.setText(HomeActivity.userGeneralInfo.getPhone());
        designation.setText(HomeActivity.userGeneralInfo.getDesignation());

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
    }
}
