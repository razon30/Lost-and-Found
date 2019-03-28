package razon.lostandfound.fragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import id.zelory.compressor.Compressor;
import razon.lostandfound.R;
import razon.lostandfound.activity.HomeActivity;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.Comments;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.model.Notification;
import razon.lostandfound.utils.FileUtil;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.FragmentNode;
import razon.lostandfound.utils.MyEditText;
import razon.lostandfound.utils.SharePreferenceSingleton;
import razon.lostandfound.utils.SimpleActivityTransition;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends Fragment {

    static final int PERMISSION_CODE = 1;
    String status;
    private MyEditText caption;
    private ImageView productImage;
    private LinearLayout addPhoto;
    private CardView submit;
    private CardView cancel;
    private static final int RESULT_CANCELED = 0;
    private int GALLERY = 1, CAMERA = 2;
    int choise;

    String imageByte = "1";
    Bitmap thumbnail;

    ProgressDialog progressDialog;

    File actualImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        status = getActivity().getIntent().getStringExtra("status");
        initView(view);

        addPhoto.setOnClickListener(view1 -> showPictureDialog());

        cancel.setOnClickListener(view12 -> getActivity().onBackPressed());

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submit();

            }
        });


        return view;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (
                    ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            )
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
            } else {
                openCamera();
            }

        } else {
            openCamera();
        }


    }

    private void openCamera() {
      //  Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
      //  startActivityForResult(intent, CAMERA);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch (requestCode) {
            case PERMISSION_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            }


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();

                try {
                    actualImage = FileUtil.from(getActivity(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Preparing image..");
                progressDialog.show();

                thumbnail = decodeUriToBitmap(getActivity(), contentURI);

                productImage.setVisibility(View.VISIBLE);
                productImage.setImageBitmap(thumbnail);

                imageByte = "2";
                choise = GALLERY;

                progressDialog.dismiss();

            }

        } else if (requestCode == CAMERA) {
            thumbnail = (Bitmap) data.getExtras().get("data");
            try {
                actualImage = FileUtil.from(getActivity(), getUri(thumbnail));
            } catch (IOException e) {
                e.printStackTrace();
            }


            productImage.setVisibility(View.VISIBLE);
            productImage.setImageBitmap(thumbnail);

            imageByte = "2";
            choise = CAMERA;

        }
    }

    private Uri getUri(Bitmap thumbnail) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), thumbnail, "Title", null);
        return Uri.parse(path);

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

        if (imageByte.equals("2")) {

            if (actualImage.length() > 200000) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    Bitmap compressedImageBitmap = new Compressor(getActivity())
                            .setMaxWidth(400)
                            .setMaxHeight(250)
                            .setQuality(100)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToBitmap(actualImage);
                    if (choise == CAMERA) {
                        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                    } else {
                        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                    }

                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    imageByte = Base64.encodeToString(byteArray, Base64.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                imageByte = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }


        }

        if (getActivity().getIntent().getStringExtra("id") == null) {
            getItemID(captionString);
        } else {

            getCommentID(captionString, getActivity().getIntent().getStringExtra("id"));

        }

    }

    private void getCommentID(final String captionString, String id) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        final String[] mId = new String[1];

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(FirebaseEndPoint.COMMENT_ID_GENERATE);
        final int[] c = {0};

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mId[0] = dataSnapshot.child("Count").getValue().toString();
                if (c[0] == 0) {
                    c[0] = 1;
                    addComment(captionString, getActivity().getIntent().getStringExtra("id"), mId[0]);

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

        setNotification(comments, id);

        FirebaseDatabase countData = FirebaseDatabase.getInstance();
        DatabaseReference countDataRefMeeting = countData.getReference(FirebaseEndPoint.COMMENT_ID_GENERATE).child("Count");
        commentId = String.valueOf(Integer.valueOf(commentId) + 1);
        countDataRefMeeting.setValue(commentId);

        progressDialog.dismiss();
        Intent intent = new Intent(getActivity(), MainActivity.class)
                .putExtra("type", getActivity().getIntent().getStringExtra("itemType"))
                .putExtra("id", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
        getActivity().finish();

    }

    private void setNotification(Comments comments, String id) {

        ArrayList<String> userNameList = new ArrayList<>();
        userNameList = getActivity().getIntent().getStringArrayListExtra("userlist");
        HashSet<String> hashSet = new HashSet<>(userNameList);
        userNameList.clear();
        userNameList.addAll(hashSet);
        if (comments.getUsername().equals(getActivity().getIntent().getStringExtra("postedBy"))) {

            if (userNameList.contains(comments.getUsername())) {
                userNameList.remove(userNameList.indexOf(comments.getUsername()));
            }

        } else {

            userNameList.add(getActivity().getIntent().getStringExtra("postedBy"));

        }

        String username = SharePreferenceSingleton.getInstance(getActivity()).getString("username");

        if (userNameList.contains(username)) {
            userNameList.remove(userNameList.indexOf(username));
        }


        Log.d("count", userNameList.size() + "");

        for (int i = 0; i < userNameList.size(); i++) {

            //  if (!userNameList.get(i).equals(getActivity().getIntent().getStringExtra("postedBy"))) {

            String notifiedUserName = userNameList.get(i);
            Notification notification = new Notification();
            notification.setCommentedBy(comments.getUsername());
            notification.setItemID(id);
            if (getActivity().getIntent().getStringExtra("postedBy") == null) {
                notification.setPostedBy("Someone");
            } else {
                notification.setPostedBy(getActivity().getIntent().getStringExtra("postedBy"));
            }

            if (getActivity().getIntent().getStringExtra("postTime") == null) {
                notification.setPostDate("Someday");
            } else {
                notification.setPostDate(getActivity().getIntent().getStringExtra("postTime"));
            }
            if (getActivity().getIntent().getStringExtra("itemType").equals(FragmentNode.LOST)) {
                notification.setStatus(FirebaseEndPoint.LOST);
            } else {
                notification.setStatus(FirebaseEndPoint.FOUND);
            }


            DatabaseReference usernameRefMeeting = FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseEndPoint.NOTIFICATION).child(notifiedUserName).push();
            usernameRefMeeting.setValue(notification);

            final DatabaseReference notiCount = FirebaseDatabase.getInstance().getReference()
                    .child(FirebaseEndPoint.USER_INFO).child(notifiedUserName);
            final int[] c = {0};
            notiCount.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild(FirebaseEndPoint.NOTI_COUNT)) {
                        String count = dataSnapshot.child(FirebaseEndPoint.NOTI_COUNT).getValue().toString();
                        if (c[0] == 0) {
                            c[0] = 1;
                            notiCount.child(FirebaseEndPoint.NOTI_COUNT).setValue(String.valueOf(Integer.parseInt(count) + 1));

                        }

                    } else {
                        notiCount.child(FirebaseEndPoint.NOTI_COUNT).setValue("1");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        //  }


    }

    private void getItemID(final String captionString) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

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
                .child("UserData").child(username).child(status).child("id" + itemId);
        usernameRefMeeting.setValue(itemId);

        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
        Date date = new Date();
        String dateReadable = format.format(date);

        FoundLostItem foundLostItem = new FoundLostItem(itemId, username, name, captionString, imageByte, dateReadable, proPic, "1");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference emailRefCount = database.getReference().child(status).child(itemId);
        emailRefCount.setValue(foundLostItem);

        FirebaseDatabase countData = FirebaseDatabase.getInstance();
        DatabaseReference countDataRefMeeting = countData.getReference(FirebaseEndPoint.LOST_ID_GENERATE).child("Count");
        itemId = String.valueOf(Integer.valueOf(itemId) + 1);
        countDataRefMeeting.setValue(itemId);

        progressDialog.dismiss();

        SimpleActivityTransition.goToPreviousActivity(getActivity(), HomeActivity.class);
        getActivity().finish();

    }


}
