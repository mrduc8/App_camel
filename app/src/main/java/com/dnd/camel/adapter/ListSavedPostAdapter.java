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

import com.bumptech.glide.Glide;
import com.dnd.camel.R;
import com.dnd.camel.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Objects;

public class ListSavedPostAdapter extends ArrayAdapter<PostModel> {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser = null;
    public ListSavedPostAdapter(@NonNull Context context, ArrayList<PostModel> listPostSavedView) {
        super(context, 0, listPostSavedView);
    }
    @NonNull
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View listItemView = LayoutInflater.from(getContext()).inflate(R.layout.saved_post_custom, parent, false);
        currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        PostModel currentPost = getItem(position);
        ImageView imgPost = listItemView.findViewById(R.id.imgPost);
        TextView txtTitle = listItemView.findViewById(R.id.txtTitle);
        TextView txtContent = listItemView.findViewById(R.id.txtContent);
        assert currentPost != null;
        txtTitle.setText(currentPost.getTitle());
        txtContent.setText(currentPost.getContent());
        if (currentPost.getPostImg() != null) {
            Glide.with(getContext())
                    .load(currentPost.getPostImg())
                    .into(imgPost);
        }
        else {
            imgPost.setImageDrawable(getContext().getResources().getDrawable(R.drawable.no_image));
        }
        return listItemView;
    }
}