package com.dnd.camel.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.PostDetailActivity;
import com.dnd.camel.R;
import com.dnd.camel.adapter.ListSavedPostAdapter;
import com.dnd.camel.models.PostModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SavedPostFragment extends Fragment {
    View rootView;
    ListView lvPost = null;
    ImageButton ibBack;
    ListSavedPostAdapter listSavedPostAdapter = null;
    ArrayList<PostModel> postModels = new ArrayList<>();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
    String currentUser = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_saved_post, container, false);
        currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        Mapping();
        reloadDataFromFirebase();
        listSavedPostAdapter = new ListSavedPostAdapter(requireActivity(), postModels);
        lvPost.setAdapter(listSavedPostAdapter);
        lvPost.setOnItemLongClickListener((parent, view, position, id) -> {
            PostModel selectedPost = postModels.get(position);
            showAnnouncementDialog(selectedPost);
            return true;
        });

        lvPost.setOnItemClickListener((parent, view, position, id) -> {
            PostModel selectedPost = postModels.get(position);
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            intent.putExtra("post_id", selectedPost.getId());
            startActivity(intent);
        });

        ibBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return rootView;
    }

    private void Mapping() {
        lvPost = rootView.findViewById(R.id.lvPost);
        ibBack = rootView.findViewById(R.id.ibBack);

        ConstraintLayout savedPostFragment = rootView.findViewById(R.id.savedPostFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = savedPostFragment.getLayoutParams();
        layoutParams.height = newHeight;
        savedPostFragment.setLayoutParams(layoutParams);

        // lvPost
        lvPost.getLayoutParams().height = (int) (newHeight * 0.9);
    }

    private void showAnnouncementDialog(PostModel selectedPost) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Warning");
        builder.setMessage("Are you sure you want to un save this post?");
        builder.setPositiveButton("Yes", (dialog, id) -> {
            DatabaseReference postRef = db.getReference(FirebaseDBConnection.POST).child(selectedPost.getId());
            postRef.child("list_user_save").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    selectedPost.getList_user_save().remove(currentUser);
                    postRef.child("list_user_save").setValue(selectedPost.getList_user_save());
                    reloadDataFromFirebase();
                }
                else {
                    Log.d("UserPostAdapter", "Error getting user likes: ", task.getException());
                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void reloadDataFromFirebase() {
        postModels.clear();
        FirebaseDatabase database = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
        DatabaseReference databaseReferencePost = database.getReference().child(FirebaseDBConnection.POST);
        databaseReferencePost.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostModel postModel = dataSnapshot.getValue(PostModel.class);
                    if (postModel != null && postModel.getList_user_save().containsKey(currentUser) && !postModel.isIs_deleted()) {
                        postModel.setId(dataSnapshot.getKey());
                        postModels.add(postModel);
                    }
                }
                listSavedPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}