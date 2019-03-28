package razon.lostandfound.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import razon.lostandfound.R;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.FoundLostItem;
import razon.lostandfound.model.FoundLostItemDb;
import razon.lostandfound.utils.FragmentNode;
import razon.lostandfound.utils.MyTextView;
import razon.lostandfound.utils.SharePreferenceSingleton;

/**
 * Created by HP on 18-Aug-17.
 */

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.MyViewHolder> {


    LayoutInflater inflater;
    Activity context;
    ArrayList<FoundLostItemDb> lostList, filterList;

    String username;

    public AdapterSearch(Activity context, ArrayList<FoundLostItemDb> lostList) {
        this.lostList = new ArrayList<>();
        this.filterList = new ArrayList<>();
        this.context = context;
        this.lostList = lostList;
        this.filterList.addAll(lostList);
        inflater = LayoutInflater.from(context);
        username = SharePreferenceSingleton.getInstance(context).getString("username");
        Log.d("propicAdapter", this.lostList.size() + "");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_lost, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final FoundLostItemDb currentItem = filterList.get(position);

        holder.caption.setText(currentItem.getCaption());
        holder.name.setText(currentItem.getName());
        holder.username.setText(currentItem.getUsername());
        holder.date.setText(currentItem.getTime());

        if (username.equals(currentItem.getUsername())) holder.chat.setVisibility(View.GONE);

        String proPic = currentItem.getProPic();

        Log.d("propicItem", lostList.get(position).getProPic() + " " + lostList.get(position).getName());

        if (!proPic.equals("1")) {

            byte[] data = Base64.decode(proPic, Base64.DEFAULT);

            final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.profileImage.setImageBitmap(bmp);

        } else {
            holder.profileImage.setImageResource(R.drawable.profile_dummy);
        }

        String image = currentItem.getImage();
        if (!image.equals("1")) {
            byte[] data = Base64.decode(image, Base64.DEFAULT);

            final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.productImage.setImageBitmap(bmp);

            holder.productImage.setOnClickListener(view -> showImage(bmp));


        } else {
            holder.productImage.setVisibility(View.GONE);
        }

        holder.details.setOnClickListener(view -> {

            Intent intent = new Intent(context, MainActivity.class)
                    .putExtra("type", currentItem.getType())
                    .putExtra("id", currentItem.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);

        });

        if (username.equals("admin12")) {
            holder.chat.setVisibility(View.GONE);
        }

        holder.chat.setOnClickListener(view -> {
            Intent intent = new Intent(context, MainActivity.class)
                    .putExtra("type", FragmentNode.CHAT)
                    .putExtra("receiver", currentItem.getUsername());
            //  .putExtra("receiverImage", currentItem.getProPic());
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
        });

    }

    private void showImage(Bitmap data) {

        View view = context.getLayoutInflater().inflate(R.layout.image_view_layout, null);
        ImageView imageView = view.findViewById(R.id.image);
        ImageView cancel = view.findViewById(R.id.cancel);
        imageView.setMinimumHeight(context.getWindowManager().getDefaultDisplay().getHeight());
        imageView.setMinimumWidth(context.getWindowManager().getDefaultDisplay().getWidth());
        imageView.setImageBitmap(data);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        cancel.setOnClickListener(view1 -> alertDialog.dismiss());

    }

    @Override
    public int getItemCount() {
        return (null != filterList ? filterList.size() : 0);
    }


    public void filter(String text) {



        if (text == null || text.equals("")) {
            filterList.clear();
            filterList.addAll(lostList);
            notifyDataSetChanged();
            return;
        }

        filterList.clear();

        for (FoundLostItemDb foundLost : lostList) {

            String caption = foundLost.getCaption().toLowerCase().trim();

            if (caption.contains(text.toLowerCase())){

                filterList.add(foundLost);

            }

        }

        notifyDataSetChanged();


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profileImage;
        public MyTextView name;
        public MyTextView username;
        public MyTextView caption;
        public ImageView productImage;
        public MyTextView details;
        public CardView chat;
        public MyTextView date;


        public MyViewHolder(View rootView) {
            super(rootView);
            this.profileImage = rootView.findViewById(R.id.profile_image);
            this.name = rootView.findViewById(R.id.name);
            this.username = rootView.findViewById(R.id.username);
            this.caption = rootView.findViewById(R.id.caption);
            this.productImage = rootView.findViewById(R.id.product_image);
            this.details = rootView.findViewById(R.id.details);
            this.chat = rootView.findViewById(R.id.chat);
            this.date = rootView.findViewById(R.id.date);
        }
    }

}
