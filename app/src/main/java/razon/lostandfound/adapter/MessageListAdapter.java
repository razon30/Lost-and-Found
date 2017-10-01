package razon.lostandfound.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import razon.lostandfound.R;
import razon.lostandfound.model.Message;
import razon.lostandfound.model.UserGeneralInfo;

/**
 * Created by HP on 30-Sep-17.
 */

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Activity context;
    private ArrayList<Message> mMessageList = new ArrayList<>();
    String username;

    public MessageListAdapter(Activity context, ArrayList<Message> mMessageList, String username) {
        this.context = context;
        this.mMessageList = mMessageList;
        this.username = username;
        Log.d("sizeMsg", mMessageList.size()+"");
    }

    @Override
    public int getItemCount() {
        Log.d("sizeMsgCount", mMessageList.size()+"");
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.

    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);
        Log.d("sizeMsg", mMessageList.get(position).getBody());
        if (message.getSenderName().equals(username)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView image_message;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            image_message = (ImageView) itemView.findViewById(R.id.image_message);
        }

        void bind(Message message) {
            timeText.setText(message.getTime());
            if (message.getImage().equals("1")){
                image_message.setVisibility(View.GONE);
                messageText.setVisibility(View.VISIBLE);
                messageText.setText(message.getBody());
                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }else {
                messageText.setVisibility(View.GONE);
                image_message.setVisibility(View.VISIBLE);
                byte[] data = Base64.decode(message.getImage(), Base64.DEFAULT);
                final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                image_message.setImageBitmap(bmp);
                image_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(bmp);
                    }
                });
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView  image_message;


        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
          //  profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            image_message = (ImageView) itemView.findViewById(R.id.image_message);
        }

        void bind(Message message) {

            timeText.setText(message.getTime());
            nameText.setText(message.getSenderName());
            if (message.getImage().equals("1")){
                messageText.setVisibility(View.VISIBLE);
                image_message.setVisibility(View.GONE);
                messageText.setText(message.getBody());
                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }else {
                messageText.setVisibility(View.GONE);
                image_message.setVisibility(View.VISIBLE);
                byte[] data = Base64.decode(message.getImage(), Base64.DEFAULT);
                final Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                image_message.setImageBitmap(bmp);
                image_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImage(bmp);
                    }
                });
            }
        }
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
}
