package com.dnd.camel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dnd.camel.R;
import com.dnd.camel.models.ListMenuModel;

import java.util.ArrayList;

public class ListMenuAdapter extends ArrayAdapter<ListMenuModel> {

    public ListMenuAdapter(@NonNull Context context, ArrayList<ListMenuModel> listMessView) {
        super(context, 0, listMessView);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_menu_custom, parent, false);
        ListMenuModel currentMenu = getItem(position);
        ImageView imageView = listItemView.findViewById(R.id.iconTitle);
        TextView title = listItemView.findViewById(R.id.txtTitle);
        if (currentMenu != null) {
            imageView.setImageResource(currentMenu.getImg());
            title.setText(currentMenu.getTitle());
        }
        return listItemView;
    }
}