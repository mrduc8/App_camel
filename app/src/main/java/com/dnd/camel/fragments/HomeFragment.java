package com.dnd.camel.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.R;
import com.dnd.camel.adapter.UserPostAdapter;
import com.dnd.camel.models.PostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HomeFragment extends Fragment {
    RecyclerView rvUserPost;
    ArrayList<PostModel> postList = new ArrayList<>();
    UserPostAdapter userPostAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getPosts();
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rvUserPost = rootView.findViewById(R.id.rvUserPost);
        userPostAdapter = new UserPostAdapter(getActivity(), postList);
        rvUserPost.setAdapter(userPostAdapter);
        rvUserPost.setLayoutManager(new LinearLayoutManager(getActivity()));
        FrameLayout homeFragment = rootView.findViewById(R.id.homeFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = homeFragment.getLayoutParams();
        layoutParams.height = newHeight;
        homeFragment.setLayoutParams(layoutParams);
        return rootView;

    }

    private void getPosts() {
        DatabaseReference postRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference(FirebaseDBConnection.POST);
        postRef.orderByChild("is_deleted").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);
                    assert post != null;
                    post.setId(postSnapshot.getKey());
                    postList.add(post);
                }
                Collections.reverse(postList);
                userPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}