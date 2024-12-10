package com.dnd.camel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.dnd.camel.R;
import com.dnd.camel.models.ListNotiModel;

import java.util.ArrayList;

public class ListNotificationAdapter extends ArrayAdapter<ListNotiModel> {

    public ListNotificationAdapter(@NonNull Context context, ArrayList<ListNotiModel> listNotificationView) {
        super(context, 0, listNotificationView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_notification, parent, false);
        ListNotiModel currentNotification = getItem(position);
        ImageView imageButton = listItemView.findViewById(R.id.imgAvatar);
        TextView notification = listItemView.findViewById(R.id.txtNoti);
        FrameLayout notificationFrame = listItemView.findViewById(R.id.frameNotification);

        if (currentNotification != null) {
            if(currentNotification.getImg().isEmpty()) {
                imageButton.setImageDrawable(getContext().getDrawable(R.drawable.baseline_account_circle_24));
                notification.setText(currentNotification.getContent());
            }
            else {
                Glide.with(getContext())
                        .load(currentNotification.getImg())
                        .circleCrop()
                        .into(imageButton);
                notification.setText(currentNotification.getContent());
            }

            if (!currentNotification.isIs_click()) {
                notificationFrame.setBackgroundColor(getContext().getColor(R.color.notification));
            }
        }

        return listItemView;
    }
}