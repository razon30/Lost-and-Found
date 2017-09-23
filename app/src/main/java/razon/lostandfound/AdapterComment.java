package razon.lostandfound;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.Comments;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.utils.MyTextView;

/**
 * Created by HP on 18-Aug-17.
 */

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.MyViewHolder> {


    LayoutInflater inflater;
    Activity context;
    ArrayList<Comments> lostList = new ArrayList<>();
    String type;

    public AdapterComment(Activity context, ArrayList<Comments> lostList, String type) {
        this.context = context;
        this.lostList = lostList;
        this.type = type;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {


        final Comments currentItem = lostList.get(position);

        holder.caption.setText(currentItem.getCaption());
        holder.name.setText(currentItem.getName());
        holder.username.setText(currentItem.getUsername());

        String image = currentItem.getImage();
        if (!image.equals("1")){
            byte[] data = Base64.decode(image, Base64.DEFAULT);

            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.productImage.setImageBitmap(bmp);


        }

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


        public MyViewHolder(View rootView) {
            super(rootView);
            this.profileImage = (CircleImageView) rootView.findViewById(R.id.profile_image);
            this.name = (MyTextView) rootView.findViewById(R.id.name);
            this.username = (MyTextView) rootView.findViewById(R.id.username);
            this.caption = (MyTextView) rootView.findViewById(R.id.caption);
            this.productImage = (ImageView) rootView.findViewById(R.id.product_image);

        }
    }

}
