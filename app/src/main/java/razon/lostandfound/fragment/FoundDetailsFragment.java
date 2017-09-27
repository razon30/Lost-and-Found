package razon.lostandfound.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import razon.lostandfound.adapter.AdapterComment;
import razon.lostandfound.R;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.Comments;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.utils.FirebaseEndPoint;
import razon.lostandfound.utils.FragmentNode;
import razon.lostandfound.utils.MyTextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FoundDetailsFragment extends Fragment {


    public FoundDetailsFragment() {
        // Required empty public constructor
    }


    String id;
    private CircleImageView profileImage;
    private MyTextView name;
    private MyTextView username;
    private MyTextView caption;
    private MyTextView date;
    private ImageView productImage;
    MyTextView commentNumber;

    private RecyclerView commentRecycler;
    AdapterComment adaoterComment;
    LinearLayoutManager layoutManager;
    ArrayList<Comments> commentList;


    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    DatabaseReference commentDatabaseReference;
    ValueEventListener commentValueEventListener;
    ProgressDialog progressDialouge;

    private LinearLayout addComment;

    View mainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_lost_details, container, false);
        id = getActivity().getIntent().getStringExtra("id");
        initView(mainView);
        populateDetails();
        populateComments();

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), MainActivity.class)
                        .putExtra("type", FragmentNode.ADD_ITEM)
                        .putExtra("id", id)
                        .putExtra("itemType", FragmentNode.FOUND);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);

            }
        });

        return mainView;
    }

    private void populateComments() {

        commentDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseEndPoint.COMMENT).child(id);
        commentValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    commentList.add(postSnapshot.getValue(Comments.class));

                    Log.d("size", postSnapshot.getValue() + "");

                }


                if (progressDialouge !=null) {
                    progressDialouge.dismiss();
                }

                if (commentList.size() == 0){
                    commentNumber.setText("No Comments");
                }else {
                    Collections.reverse(commentList);
                    commentNumber.setText(commentList.size()+" Comments");
                }

                adaoterComment.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        commentDatabaseReference.addValueEventListener(commentValueEventListener);

    }

    private void populateDetails() {
        productImage.setVisibility(View.GONE);
        databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseEndPoint.FOUND).child(id);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                FoundLostItem curretnItem = dataSnapshot.getValue(FoundLostItem.class);

                if (curretnItem != null) {
                    name.setText(curretnItem.getName());
                    username.setText(curretnItem.getUsername());
                    caption.setText(curretnItem.getCaption());
                    date.setText(curretnItem.getTime());

                    String proPic = curretnItem.getProPic();
                    if (!proPic.equals("1")){

                        byte[] data = Base64.decode(proPic, Base64.DEFAULT);

                        final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        profileImage.setImageBitmap(bmp);

                    }else {
                        profileImage.setImageResource(R.drawable.profile_dummy);
                    }

                    String image = curretnItem.getImage();
                    if (!image.equals("1")) {
                        byte[] data = Base64.decode(image, Base64.DEFAULT);

                        final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        productImage.setImageBitmap(bmp);
                        productImage.setVisibility(View.VISIBLE);

                        showImage(bmp);

                        productImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showImage(bmp);
                            }
                        });

                    } else {
                        productImage.setVisibility(View.GONE);
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);

    }

    private void showImage(Bitmap data) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.image_view_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
        imageView.setMinimumHeight(mainView.getHeight());
        imageView.setMinimumWidth(mainView.getWidth());
        imageView.setImageBitmap(data);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


    }


    private void initView(View view) {
        addComment = (LinearLayout) view.findViewById(R.id.addPhoto);
        profileImage = (CircleImageView) view.findViewById(R.id.profile_image);
        name = (MyTextView) view.findViewById(R.id.name);
        username = (MyTextView) view.findViewById(R.id.username);
        caption = (MyTextView) view.findViewById(R.id.caption);
        date = (MyTextView) view.findViewById(R.id.date);
        commentNumber = (MyTextView) view.findViewById(R.id.commentNumber);
        productImage = (ImageView) view.findViewById(R.id.product_image);
        commentRecycler = (RecyclerView) view.findViewById(R.id.commentRecycler);
        commentList = new ArrayList<>();
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adaoterComment = new AdapterComment(getActivity(), commentList, FirebaseEndPoint.COMMENT);
        commentRecycler.setLayoutManager(layoutManager);
        commentRecycler.setAdapter(adaoterComment);

        progressDialouge = new ProgressDialog(getActivity());
        progressDialouge.setMessage("Please Wait...");
        progressDialouge.show();
    }

    @Override
    public void onPause() {

        if (valueEventListener!=null && databaseReference!=null){
            databaseReference.removeEventListener(valueEventListener);
        }
        if (commentValueEventListener!=null && commentDatabaseReference!=null){
            commentDatabaseReference.removeEventListener(commentValueEventListener);
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        if (valueEventListener!=null && databaseReference!=null){
            databaseReference.removeEventListener(valueEventListener);
        }
        if (commentValueEventListener!=null && commentDatabaseReference!=null){
            commentDatabaseReference.removeEventListener(commentValueEventListener);
        }
        super.onStop();
    }


}

