package razon.lostandfound.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import razon.lostandfound.R;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.utils.MyTextView;

/**
 * Created by HP on 18-Aug-17.
 */

public class AdapterLost extends RecyclerView.Adapter<AdapterLost.MyViewHolder> {


    LayoutInflater inflater;
    Activity context;
    ArrayList<FoundLostItem> lostList = new ArrayList<>();
    String type;

    public AdapterLost(Activity context, ArrayList<FoundLostItem> lostList, String type) {
        this.context = context;
        this.lostList = lostList;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_lost, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final FoundLostItem currentItem = lostList.get(position);

        holder.caption.setText(currentItem.getCaption());
        holder.name.setText(currentItem.getName());
        holder.username.setText(currentItem.getUsername());
        holder.date.setText(currentItem.getTime());

        String proPic = currentItem.getProPic();
        if (!proPic.equals("1")){

            byte[] data = Base64.decode(proPic, Base64.DEFAULT);

            final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.profileImage.setImageBitmap(bmp);

        }else {
            holder.profileImage.setImageResource(R.drawable.profile_dummy);
        }

        String image = currentItem.getImage();
        if (!image.equals("1")){
            byte[] data = Base64.decode(image, Base64.DEFAULT);

            final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.productImage.setImageBitmap(bmp);

            holder.productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImage(bmp);
                }
            });


        }else {
            holder.productImage.setVisibility(View.GONE);
        }

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MainActivity.class)
                        .putExtra("type", type)
                        .putExtra("id", currentItem.getId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);

            }
        });

    }

    private void showImage(Bitmap data) {

        View view = context.getLayoutInflater().inflate(R.layout.image_view_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
        imageView.setMinimumHeight(context.getWindowManager().getDefaultDisplay().getHeight());
        imageView.setMinimumWidth(context.getWindowManager().getDefaultDisplay().getWidth());
        imageView.setImageBitmap(data);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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

    @Override
    public int getItemCount() {
        return lostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImage;
        public MyTextView name;
        public MyTextView username;
        public MyTextView caption;
        public ImageView productImage;
        public MyTextView details;
        public MyTextView chat;
        public MyTextView date;


        public MyViewHolder(View rootView) {
            super(rootView);
            this.profileImage = (CircleImageView) rootView.findViewById(R.id.profile_image);
            this.name = (MyTextView) rootView.findViewById(R.id.name);
            this.username = (MyTextView) rootView.findViewById(R.id.username);
            this.caption = (MyTextView) rootView.findViewById(R.id.caption);
            this.productImage = (ImageView) rootView.findViewById(R.id.product_image);
            this.details = (MyTextView) rootView.findViewById(R.id.details);
            this.chat = (MyTextView) rootView.findViewById(R.id.chat);
            this.date = (MyTextView) rootView.findViewById(R.id.date);
        }
    }

}
