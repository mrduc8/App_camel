package com.dnd.camel.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dnd.camel.ChatActivity;
import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.R;
import com.dnd.camel.adapter.UserPostAdapter;
import com.dnd.camel.models.PostModel;
import com.dnd.camel.services.FirebaseStorageService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class UserHomeFragment extends Fragment {
    View rootView;
    TextView txtUsername;
    Button btnChat;
    RecyclerView rvUserPost;
    UserPostAdapter userPostAdapter;
    ArrayList<PostModel> postList = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseDatabase db = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
    ImageView imgBack, imgAvatar, imgBackground;
    FirebaseFirestore ft;
    private Uri imageUri = null;
    Long timestamp = System.currentTimeMillis();
    String field = null;
    String linkImg;
    @SuppressLint({"UseCompatLoadingForDrawables", "MissingInflatedId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        ft = FirebaseFirestore.getInstance();
        Mapping();
        assert getArguments() != null;
        txtUsername.setText(getArguments().getString("userName"));
        String id = getArguments().getString("id");
        int state = getArguments().getInt("state");
        imgBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                if(state == 1) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("userName", txtUsername.getText().toString());
                    intent.putExtra("linkImg", linkImg);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0 ,0);
                }
                else {
                    getParentFragmentManager().popBackStack();
                }
            }
        });
        imgBackground.setOnClickListener(v -> {
            field = "imgBackground";
            assert id != null;
            if(id.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                solve();
            }
        });
        rvUserPost = rootView.findViewById(R.id.rvUserPost);
        userPostAdapter = new UserPostAdapter(getContext(), postList);
        rvUserPost.setAdapter(userPostAdapter);
        rvUserPost.setLayoutManager(new LinearLayoutManager(getContext()));
        loadPost(id);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                field = "imgAvatar";
                assert id != null;
                if(id.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getUid())) {
                    solve();
                }
            }
        });

        assert id != null;
        DocumentReference document = ft.collection("users").document(id);
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    linkImg = doc.getString("imgAvatar");
                    String linkImgBackground = doc.getString("imgBackground");
                    assert linkImg != null;
                    if(linkImg.isEmpty()) {
                        imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_24));
                    }
                    else {
                        Glide.with(requireContext())
                                .load(linkImg)
                                .circleCrop()
                                .into(imgAvatar);
                    }
                    assert linkImgBackground != null;
                    if(linkImgBackground.isEmpty()) {
                        imgBackground.setImageDrawable(getResources().getDrawable(R.drawable.background));
                    }
                    else {
                        Glide.with(requireContext())
                                .load(linkImgBackground)
                                .into(imgBackground);
                    }
                }
            }
        });
        btnChat.setOnClickListener(v -> {
            DocumentReference document1 = ft.collection("users").document(id);
            document1.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String linkImg = doc.getString("imgAvatar");
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("linkImg", linkImg);
                        intent.putExtra("userName", txtUsername.getText().toString());
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                }
            });
        });
        return rootView;
    }

    private void Mapping() {
        txtUsername = rootView.findViewById(R.id.txtUsername);
        btnChat = rootView.findViewById(R.id.btnChat);
        imgBack = rootView.findViewById(R.id.back_button);
        imgAvatar = rootView.findViewById(R.id.imgAvatar);
        imgBackground = rootView.findViewById(R.id.imgBackground);

        LinearLayout userHomeFragment = rootView.findViewById(R.id.userHomeFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = userHomeFragment.getLayoutParams();
        layoutParams.height = newHeight;
        userHomeFragment.setLayoutParams(layoutParams);
    }

    private void loadPost(String id) {
        // get post by user id
        Query query = db.getReference(FirebaseDBConnection.POST).orderByChild("user_id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    PostModel post = postSnapshot.getValue(PostModel.class);
                    assert post != null;
                    if (post.isAnonymous() || post.isIs_deleted())
                        continue;
                    post.setId(postSnapshot.getKey());
                    postList.add(post);
                }
                Collections.reverse(postList);
                userPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static final int REQUEST_IMAGE_PICK = 1;
    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE); // To open camera
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean isImageLoaded = false;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImageUri = data.getData();
                imageUri = selectedImageUri;
                if(field.equals("imgAvatar")) {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .circleCrop()
                            .into(imgAvatar);
                    isImageLoaded = true;
                    imgAvatar.setVisibility(View.VISIBLE);
                }
                if(field.equals("imgBackground")) {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(imgBackground);
                    isImageLoaded = true;
                    imgBackground.setVisibility(View.VISIBLE);
                }
            }
        }
        // Upload image to Firebase Storage
        FirebaseStorageService storageService = new FirebaseStorageService();
        if (isImageLoaded) {
            byte[] imageInByte = getBytesFromImageUri(imageUri);
            String extension = getFileExtension(imageUri);
            storageService.uploadImage(imageInByte, "post_" + timestamp + extension).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    DocumentReference documentReference = ft.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    documentReference.update(field, downloadUri.toString())
                            .addOnSuccessListener(unused -> {

                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private byte[] getBytesFromImageUri(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
            ByteArrayOutputStream bass = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bass);
            return bass.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    @SuppressLint("SetTextI18n")
    private void solve() {
        @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(bottomSheetView);
        Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
        btnConfirm.setText("Upload Image");
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
        btnCancel.setText("Remove Image");
        TextView txtTitle = bottomSheetView.findViewById(R.id.txtTitle);
        txtTitle.setText("Do you want to upload new image?");
        btnConfirm.setOnClickListener(v1 -> {
            // check permission for camera
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 100);
            }
            // check permission for storage
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
            }
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
            }
            bottomSheetDialog.dismiss();
            pickImage();
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if(field.equals("imgAvatar")) {
                    imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_24));
                }
                else {
                    imgBackground.setImageDrawable(getResources().getDrawable(R.drawable.background));
                }
                imageUri = null;
                DocumentReference documentReference = ft.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                documentReference.update(field, "")
                        .addOnSuccessListener(unused -> {

                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }
}