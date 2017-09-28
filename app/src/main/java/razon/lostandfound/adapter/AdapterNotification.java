package razon.lostandfound.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import razon.lostandfound.R;
import razon.lostandfound.activity.MainActivity;
import razon.lostandfound.model.Comments;
import razon.lostandfound.model.Notification;
import razon.lostandfound.utils.MyTextView;

/**
 * Created by HP on 18-Aug-17.
 */

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.MyViewHolder> {


    LayoutInflater inflater;
    Activity context;
    ArrayList<Notification> lostList = new ArrayList<>();

    public AdapterNotification(Activity context, ArrayList<Notification> lostList) {
        this.context = context;
        this.lostList = lostList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Notification currentNotification = lostList.get(position);

        String htmlText = "<body><h1><font color=\"#0091EA\">"+currentNotification.getCommentedBy()+"</font></h1>" +
                "<p>Commented on a "+currentNotification.getStatus()+"ed post, posted by " +
                "<strong><font color=\"#0091EA\">"+currentNotification.getPostedBy()+"</font> </strong>" +
                "on <font color=\"#0091EA\">"+currentNotification.getPostDate()+"</font></p></body>";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.noti.setText(Html.fromHtml(htmlText,Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.noti.setText(Html.fromHtml(htmlText));
        }

        holder.noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, MainActivity.class)
                        .putExtra("type", currentNotification.getStatus()+"Fragment")
                        .putExtra("id", currentNotification.getItemID());
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

        MyTextView noti;

        public MyViewHolder(View rootView) {
            super(rootView);
            noti = (MyTextView) rootView.findViewById(R.id.noti);
        }
    }

}
