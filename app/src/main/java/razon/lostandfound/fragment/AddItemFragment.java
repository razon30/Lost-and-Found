package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import razon.lostandfound.R;
import razon.lostandfound.activity.HomeActivity;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.Comments;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.FragmentNode;
import razon.lostandfound.utils.MyEditText;
import razon.lostandfound.utils.SharePreferenceSingleton;
import razon.lostandfound.utils.SimpleActivityTransition;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends Fragment {

    String status;
    private MyEditText caption;
    private ImageView productImage;
    private LinearLayout addPhoto;
    private CardView submit;
    private CardView cancel;
    private static final int RESULT_CANCELED = 0;
    private int GALLERY = 1, CAMERA = 2;

    String imageByte = "1";
    Bitmap thumbnail;

    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        status = getActivity().getIntent().getStringExtra("status");
        initView(view);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPictureDialog();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please Wait...");
                progressDialog.show();
                submit();

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

                productImage.setVisibility(View.VISIBLE);
                productImage.setImageBitmap(thumbnail);

                imageByte = "2";

                progressDialog.dismiss();

            }

        } else if (requestCode == CAMERA) {

            thumbnail = (Bitmap) data.getExtras().get("data");
            productImage.setVisibility(View.VISIBLE);
            productImage.setImageBitmap(thumbnail);

            imageByte = "2";

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

    public AddItemFragment() {
        // Required empty public constructor
    }

    private void initView(View view) {
        caption = (MyEditText) view.findViewById(R.id.caption);
        productImage = (ImageView) view.findViewById(R.id.product_image);
        addPhoto = (LinearLayout) view.findViewById(R.id.addPhoto);
        submit = (CardView) view.findViewById(R.id.submit);
        cancel = (CardView) view.findViewById(R.id.cancel);

        progressDialog = new ProgressDialog(getActivity());
    }

    private void submit() {
        // validate
        String captionString = caption.getText().toString().trim();
        if (TextUtils.isEmpty(captionString)) {
            caption.setError("Caption please");
            Toast.makeText(getContext(), "Please provide a caption", Toast.LENGTH_SHORT).show();
            return;
        }

       if (imageByte.equals("2")){

           ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
           thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
           byte[] byteArray = byteArrayOutputStream .toByteArray();
           imageByte = Base64.encodeToString(byteArray, Base64.DEFAULT);

       }

       if (getActivity().getIntent().getStringExtra("id") == null) {
           getItemID(captionString);
       }else {

           getCommentID(captionString,getActivity().getIntent().getStringExtra("id"));

       }

    }

    private void getCommentID(final String captionString, String id) {

        final String[] mId = new String[1];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseEndPoint.COMMENT_ID_GENERATE);
        final int[] c = {0};

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mId[0] = dataSnapshot.child("Count").getValue().toString();
                if (c[0] == 0) {
                    c[0] = 1;
                    addComment(captionString,getActivity().getIntent().getStringExtra("id"), mId[0]);

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addComment(String captionString, String id, String commentId) {

        String username = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
        String name = SharePreferenceSingleton.getInstance(getActivity()).getString("name");
        String proPic = SharePreferenceSingleton.getInstance(getActivity()).getString("propic");


        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
        Date date = new Date();
        String dateReadable = format.format(date);

        Comments comments = new Comments(username, name, captionString, imageByte, dateReadable, proPic);
        DatabaseReference usernameRefMeeting = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEndPoint.COMMENT).child(id).child(commentId);
        usernameRefMeeting.setValue(comments);

        FirebaseDatabase countData = FirebaseDatabase.getInstance();
        DatabaseReference countDataRefMeeting = countData.getReference(FirebaseEndPoint.COMMENT_ID_GENERATE).child("Count");
        commentId = String.valueOf(Integer.valueOf(commentId)+1);
        countDataRefMeeting.setValue(commentId);

        Intent intent = new Intent(getActivity(), MainActivity.class)
                .putExtra("type", getActivity().getIntent().getStringExtra("itemType"))
                .putExtra("id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
        getActivity().finish();

    }

    private void getItemID(final String captionString) {

        final String[] mId = new String[1];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseEndPoint.LOST_ID_GENERATE);
        final int[] c = {0};

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mId[0] = dataSnapshot.child("Count").getValue().toString();
                if (c[0] == 0) {
                    c[0] = 1;
                    addItem(mId[0], captionString);

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addItem(String itemId, String captionString) {

        String username = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
        String name = SharePreferenceSingleton.getInstance(getActivity()).getString("name");
        String proPic = SharePreferenceSingleton.getInstance(getActivity()).getString("propic");

        DatabaseReference usernameRefMeeting = FirebaseDatabase.getInstance().getReference()
                .child("UserData").child(username).child(status).child("id"+itemId);
        usernameRefMeeting.setValue(itemId);

        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
        Date date = new Date();
        String dateReadable = format.format(date);

        FoundLostItem foundLostItem = new FoundLostItem(itemId, username, name, captionString, imageByte, dateReadable, proPic);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference emailRefCount = database.getReference().child(status).child(itemId);
        emailRefCount.setValue(foundLostItem);

        FirebaseDatabase countData = FirebaseDatabase.getInstance();
        DatabaseReference countDataRefMeeting = countData.getReference(FirebaseEndPoint.LOST_ID_GENERATE).child("Count");
        itemId = String.valueOf(Integer.valueOf(itemId)+1);
        countDataRefMeeting.setValue(itemId);

        progressDialog.dismiss();

        SimpleActivityTransition.goToPreviousActivity(getActivity(), HomeActivity.class);
        getActivity().finish();

    }


}
