package razon.lostandfound.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import razon.lostandfound.R;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.Inbox;
import razon.lostandfound.utils.FragmentNode;
import razon.lostandfound.utils.MyTextView;

/**
 * Created by HP on 18-Aug-17.
 */

public class AdapterInbox extends RecyclerView.Adapter<AdapterInbox.MyViewHolder> {


    LayoutInflater inflater;
    Activity context;
    ArrayList<Inbox> lostList = new ArrayList<>();

    public AdapterInbox(Activity context, ArrayList<Inbox> lostList) {
        this.context = context;
        this.lostList = lostList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_inbox, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Inbox currentNotification = lostList.get(position);

        if (!currentNotification.getImage().equals("1")){
            byte[] data = Base64.decode(currentNotification.getImage(), Base64.DEFAULT);
            final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.profileImage.setImageBitmap(bmp);
        }

        holder.name.setText(currentNotification.getName());
        holder.designation.setText(currentNotification.getDesignation());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MainActivity.class)
                        .putExtra("type", FragmentNode.CHAT)
                        .putExtra("receiver", currentNotification.getUsername());
                //  .putExtra("receiverImage", currentItem.getProPic());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);

            }
        });


    }


    @Override
    public int getItemCount() {
        return lostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profileImage;
        public MyTextView name;
        public MyTextView designation;
        View rootView ;


        public MyViewHolder(View rootView) {
            super(rootView);
            this.rootView = rootView;
            this.profileImage = (CircleImageView) rootView.findViewById(R.id.profile_image);
            this.name = (MyTextView) rootView.findViewById(R.id.name);
            this.designation = (MyTextView) rootView.findViewById(R.id.designation);
        }
    }


}
