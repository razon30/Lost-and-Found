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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import razon.lostandfound.R;
import razon.lostandfound.adapter.MessageListAdapter;
import razon.lostandfound.model.Message;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.SharePreferenceSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatDetailsFragment extends Fragment implements View.OnClickListener {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    ArrayList<Message> messageList;

    String body = "";
    String image = "1";
    private EditText edittextChatbox;
    private Button buttonChatboxSend;
    private LinearLayout layoutChatbox;
    ImageView button_image_send;
    private static final int RESULT_CANCELED = 0;
    private int GALLERY = 1, CAMERA = 2;

    String imageByte = "1";
    Bitmap thumbnail;

    String receiver;
    String username;
    String receiverImage;

    ValueEventListener valueEventListener;
    DatabaseReference reference;

    ValueEventListener msgListValueEventListener;
    DatabaseReference msgListReference;
    int[] chat = {0};
    int[] send = {0};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_details, container, false);
        receiver = getActivity().getIntent().getStringExtra("receiver");
        receiverImage = getActivity().getIntent().getStringExtra("receiverImage");
        username = SharePreferenceSingleton.getInstance(getActivity()).getString("username");
        initView(view);


        return view;
    }

    @Override
    public void onPause() {

        if (valueEventListener != null && reference != null) {
            reference.removeEventListener(valueEventListener);
        }
        if (msgListValueEventListener != null && msgListReference != null) {
            msgListReference.removeEventListener(msgListValueEventListener);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        if (valueEventListener != null && reference != null) {
            reference.removeEventListener(valueEventListener);
        }
        if (msgListValueEventListener != null && msgListReference != null) {
            msgListReference.removeEventListener(msgListValueEventListener);
        }
        super.onStop();
    }

    public ChatDetailsFragment() {
        // Required empty public constructor
    }

    private void initView(View view) {
        edittextChatbox = (EditText) view.findViewById(R.id.edittext_chatbox);
        button_image_send = (ImageView) view.findViewById(R.id.button_image_send);
        buttonChatboxSend = (Button) view.findViewById(R.id.button_chatbox_send);
        layoutChatbox = (LinearLayout) view.findViewById(R.id.layout_chatbox);
        messageList = new ArrayList<>();
        mMessageRecycler = (RecyclerView) view.findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(layoutManager);
        mMessageAdapter = new MessageListAdapter(getActivity(), messageList, username);
        mMessageRecycler.setAdapter(mMessageAdapter);
        populateRecycler();

        edittextChatbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) buttonChatboxSend.setEnabled(true);
                else buttonChatboxSend.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        buttonChatboxSend.setOnClickListener(this);
        button_image_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_chatbox_send:
                body = edittextChatbox.getText().toString();
                submit(body, "1");
                break;
            case R.id.button_image_send:
                showPictureDialog();
                break;
        }
    }

    private void submit(String body, String type) {
//        if (valueEventListener != null && reference != null) {
//            reference.removeEventListener(valueEventListener);
//        }
//        if (msgListValueEventListener != null && msgListReference != null) {
//            msgListReference.removeEventListener(msgListValueEventListener);
//        }
        chat[0] = 0;

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Sending msg...");
        progressDialog.show();

        send[0] = 0;

        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
        Date date = new Date();
        final String dateReadable = format.format(date);
        Message message = new Message();
        if (type.equals("1")) {
            message = new Message(body, image, username, dateReadable);
        }else if (type.equals("2")){
            message = new Message("", body, username, dateReadable);
        }

        reference = FirebaseDatabase.getInstance().getReference();
        final Message finalMessage = message;
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String id = dataSnapshot.child(FirebaseEndPoint.MSG_ID_GENERATE).child("Count").getValue().toString();

                if (send[0] == 0) {

                    if (dataSnapshot.child(FirebaseEndPoint.CHAT).hasChild(username+"_"+receiver)){
                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseEndPoint.CHAT)
                                .child(username+"_"+receiver)
                                .child(id)
                                .setValue(finalMessage);

                        id = String.valueOf(Integer.parseInt(id) + 1);

                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseEndPoint.MSG_ID_GENERATE)
                                .child("Count")
                                .setValue(id);
                    }else {

                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseEndPoint.CHAT)
                                .child(receiver+"_"+username)
                                .child(id)
                                .setValue(finalMessage);

                        id = String.valueOf(Integer.parseInt(id) + 1);

                        FirebaseDatabase.getInstance().getReference()
                                .child(FirebaseEndPoint.MSG_ID_GENERATE)
                                .child("Count")
                                .setValue(id);

                    }


                    edittextChatbox.setText("");

                  //  addMessage(dataSnapshot, username, receiver, message);
                  //  addMessage(dataSnapshot, receiver, username, message);
                    //  populateRecycler();
                    send[0] = 1;
                }


//                if (dataSnapshot.hasChild(FirebaseEndPoint.CHAT)) {
//
//                    if (dataSnapshot.child(FirebaseEndPoint.CHAT).hasChild(receiver)) {
//
//                        if (send[0] == 0) {
//
//                            addMessage(dataSnapshot, username, receiver, message);
//                            addMessage(dataSnapshot, receiver, username, message);
//                            populateRecycler();
//                            send[0] = 1;
//                        }
//
//                    }else {
//                        Log.d("dekhi", "ghotona1");
//                        if (send[0] == 0) {
//
//                            addMessage(dataSnapshot, username, receiver, message);
//                            addMessage(dataSnapshot, receiver, username, message);
//                            populateRecycler();
//                            send[0] = 1;
//                        }
//                    }
//
//                }else {
//                    if (send[0] == 0) {
//                        Log.d("msgIDdekhi", "ghotona2");
//                        addMessage(dataSnapshot, username, receiver, message);
//                        addMessage(dataSnapshot, receiver, username, message);
//                        populateRecycler();
//                        send[0] = 1;
//                    }
//                }

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        reference.addValueEventListener(valueEventListener);

    }

    private void addMessage(DataSnapshot dataSnapshot, String username, String receiver, Message message) {

        String id = "0";

        if (dataSnapshot.child(username).hasChild(FirebaseEndPoint.CHAT)) {
            if (dataSnapshot.child(username).child(FirebaseEndPoint.CHAT).hasChild(FirebaseEndPoint.MSG_ID_GENERATE)) {
                id = dataSnapshot.child(username).child(FirebaseEndPoint.CHAT)
                        .child(FirebaseEndPoint.MSG_ID_GENERATE).getValue().toString();
            }
        }

        Log.d("msgID", "addMessage: " + id);


        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEndPoint.USER_INFO)
                .child(username)
                .child(FirebaseEndPoint.CHAT)
                .child(receiver)
                .child(id)
                .setValue(message);

        id = String.valueOf(Integer.parseInt(id) + 1);

        FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEndPoint.USER_INFO)
                .child(username)
                .child(FirebaseEndPoint.CHAT)
                .child(FirebaseEndPoint.MSG_ID_GENERATE)
                .setValue(id);


    }

    private void populateRecycler() {

        msgListReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseEndPoint.CHAT).child(username+"_"+receiver);

        msgListValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                if (dataSnapshot.getValue() == null){

                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child(FirebaseEndPoint.CHAT).child(receiver+"_"+username);


                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() == null){
                                Toast.makeText(getActivity(), "No chat yet",Toast.LENGTH_LONG).show();
                                return;
                            }else {

                                messageList.clear();
                                for (DataSnapshot data : dataSnapshot.getChildren()) {

                                    messageList.add(data.getValue(Message.class));


                                }

                                mMessageAdapter.notifyDataSetChanged();
                                mMessageRecycler.scrollToPosition(messageList.size() - 1);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }else {

                    messageList.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        messageList.add(data.getValue(Message.class));


                    }

                    mMessageAdapter.notifyDataSetChanged();
                    mMessageRecycler.scrollToPosition(messageList.size() - 1);

                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        msgListReference.addValueEventListener(msgListValueEventListener);

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

                imageByte = "2";

                progressDialog.dismiss();

            }

        } else if (requestCode == CAMERA) {

            thumbnail = (Bitmap) data.getExtras().get("data");

            imageByte = "2";

        }

        if (imageByte.equals("2")) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageByte = Base64.encodeToString(byteArray, Base64.DEFAULT);

            submit(imageByte, "2");

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




}
