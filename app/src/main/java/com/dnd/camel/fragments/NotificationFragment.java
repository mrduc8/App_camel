package com.dnd.camel.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.PostDetailActivity;
import com.dnd.camel.R;
import com.dnd.camel.adapter.ListNotificationAdapter;
import com.dnd.camel.models.ListNotiModel;
import com.dnd.camel.models.NotiModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationFragment extends Fragment {
    View rootView;
    TextView txtBookMark, txtNotNotification;
    ImageView img;
    ListView lvNotification;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mDatabase;
    DatabaseReference notificationRef;
    ListNotificationAdapter listNotificationAdapter = null;
    ArrayList<ListNotiModel> listNotification = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        Mapping();
        notificationRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference().child(FirebaseDBConnection.NOTIFICATION);
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNotification.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    NotiModel notification = dataSnapshot.getValue(NotiModel.class);
                    assert notification != null;
                    if (notification.getUserPostId().equals(firebaseUser.getUid())) {
                        dataSnapshot.getRef().child("is_seen").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        notificationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNotification.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    NotiModel notification = dataSnapshot.getValue(NotiModel.class);
                    assert notification != null;
                    if (notification.getUserPostId().equals(firebaseUser.getUid()) && notification.isIs_active()) {
                        listNotification.add(new ListNotiModel(notification.getImgAvatar(), notification.getContent(), notification.getPostId(), notification.isIs_click(), dataSnapshot.getKey()));
                    }
                }
                if(listNotification.isEmpty()) {
                    txtNotNotification.setVisibility(View.VISIBLE);
                }
                else {
                    txtNotNotification.setVisibility(View.GONE);
                }
                Collections.reverse(listNotification);
                listNotificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listNotificationAdapter = new ListNotificationAdapter(requireContext(), listNotification);
        lvNotification.setAdapter(listNotificationAdapter);

        lvNotification.setOnItemClickListener((parent, view, position, id) -> {
            ListNotiModel notificationModel = listNotification.get(position);
            String notificationId = notificationModel.getId();

            notificationRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference().child(FirebaseDBConnection.NOTIFICATION).child(notificationId);
            notificationRef.child("is_click").setValue(true)
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(e -> {

                    });

            DatabaseReference postRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference().child("Post").child(notificationModel.getPostId());
            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        boolean isDeleted = Boolean.TRUE.equals(dataSnapshot.child("is_deleted").getValue(Boolean.class));
                        if (isDeleted) {
                            showAnnouncement("This post has been deleted");
                        }
                        else {
                            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                            intent.putExtra("post_id", notificationModel.getPostId());
                            startActivity(intent);
                        }
                    }
                    else {
                        showAnnouncement("The article does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        lvNotification.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListNotiModel selectedNotification = listNotification.get(position);
                LayoutInflater inflater1 = LayoutInflater.from(getContext());
                @SuppressLint("InflateParams") View bottomSheetView = inflater1.inflate(R.layout.bottom_sheet, null);
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
                bottomSheetDialog.setContentView(bottomSheetView);
                Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
                btnConfirm.setText("Delete Notification");
                Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
                btnCancel.setText("Cancel");
                TextView txtTitle = bottomSheetView.findViewById(R.id.txtTitle);
                txtTitle.setText("You want to delete this notification?");
                bottomSheetDialog.show();
                btnConfirm.setOnClickListener(v -> {
                    showAnnouncementDialogNotification(selectedNotification.getId());
                    bottomSheetDialog.dismiss();
                });
                btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
                return true;
            }
        });

        txtBookMark.setOnClickListener(v -> showAnnouncementDialog());

        return rootView;
    }

    private void showAnnouncementDialogNotification(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Announcement");
        builder.setMessage("Once deleted, notification cannot be recovered, are you sure you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            DatabaseReference notificationRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                    .getReference(FirebaseDBConnection.NOTIFICATION).child(id);
            notificationRef.child("is_active").setValue(false)
                    .addOnSuccessListener(aVoid -> {

                    })
                    .addOnFailureListener(e -> {

                    });
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Announcement");
        builder.setMessage("Would you like to mark all as read?");
        builder.setPositiveButton("Yes", (dialog, id) -> {
            mDatabase = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference().child(FirebaseDBConnection.NOTIFICATION);
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot notificationSnapshot : dataSnapshot.getChildren()) {
                        NotiModel notification = notificationSnapshot.getValue(NotiModel.class);
                        if (notification != null && notification.getUserPostId().equals(firebaseUser.getUid())
                                && !notification.isIs_click()) {
                            notificationSnapshot.getRef().child("is_click").setValue(true);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });
        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Mapping() {
        lvNotification = rootView.findViewById(R.id.lvNotification);
        txtBookMark = rootView.findViewById(R.id.txtBookMark);
        txtNotNotification = rootView.findViewById(R.id.txtNotNotification);
        img = rootView.findViewById(R.id.img);

        ConstraintLayout notificationFragment = rootView.findViewById(R.id.notificationFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = notificationFragment.getLayoutParams();
        layoutParams.height = newHeight;
        notificationFragment.setLayoutParams(layoutParams);

        // img
        int imgWidth = (int) (screenWidth * 0.3);
        int imgHeight = (int) (newHeight * 0.07);
        img.getLayoutParams().width = imgWidth;
        img.getLayoutParams().height = imgHeight;

        // lv
        lvNotification.getLayoutParams().height = (int) (newHeight * 0.84);
    }

    private void showAnnouncement(String note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Announcement");
        builder.setMessage(note);
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}