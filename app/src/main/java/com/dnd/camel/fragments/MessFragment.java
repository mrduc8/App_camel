package com.dnd.camel.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dnd.camel.ChatActivity;
import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.R;
import com.dnd.camel.adapter.ListMessAdapter;
import com.dnd.camel.models.ListMessModel;
import com.dnd.camel.models.MessageModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MessFragment extends Fragment {
    View rootView;
    ImageView img;
    TextView txtNotMess;
    FirebaseFirestore ft;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    ListMessAdapter listMessAdapter = null;
    ListView lvMess = null;
    ArrayList<ListMessModel> listMess = new ArrayList<>();
    int i = 0;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_mess, container, false);
        ft = FirebaseFirestore.getInstance();
        Mapping();
        reloadDataFromFirebase();
        listMessAdapter = new ListMessAdapter(requireActivity(), listMess);
        lvMess.setAdapter(listMessAdapter);

        lvMess.setOnItemClickListener((parent, view, position, id) -> {
            ListMessModel selectedMess = listMess.get(position);
            DocumentReference document = ft.collection("users").document(selectedMess.getId());
            document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String linkImg = doc.getString("imgAvatar");
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("linkImg", linkImg);
                        intent.putExtra("userName", selectedMess.getUserName());
                        intent.putExtra("id", selectedMess.getId());
                        startActivity(intent);
                    }
                }
            });
        });

        lvMess.setOnItemLongClickListener((parent, view, position, id) -> {
            ListMessModel selectedMess = listMess.get(position);
            LayoutInflater inflater1 = LayoutInflater.from(getContext());
            @SuppressLint("InflateParams") View bottomSheetView = inflater1.inflate(R.layout.bottom_sheet, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetView);
            Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
            btnConfirm.setText("Delete All Mess");
            Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
            btnCancel.setText("Cancel");
            TextView txtTitle = bottomSheetView.findViewById(R.id.txtTitle);
            txtTitle.setText("You want to delete this entire message?");
            bottomSheetDialog.show();
            btnConfirm.setOnClickListener(v -> {
                showAnnouncementDialog(selectedMess.getId());
                bottomSheetDialog.dismiss();
            });
            btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
            return true;
        });
        return rootView;
    }

    private void Mapping() {
        img = rootView.findViewById(R.id.img);
        lvMess = rootView.findViewById(R.id.lvMess);
        txtNotMess = rootView.findViewById(R.id.txtNotMess);

        ConstraintLayout messFragment = rootView.findViewById(R.id.messFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = messFragment.getLayoutParams();
        layoutParams.height = newHeight;
        messFragment.setLayoutParams(layoutParams);

        // img
        int imgWidth = (int) (screenWidth * 0.52);
        int imgHeight = (int) (newHeight * 0.1);
        img.getLayoutParams().width = imgWidth;
        img.getLayoutParams().height = imgHeight;

        // lvMess
        lvMess.getLayoutParams().height = (int) (newHeight * 0.79);
    }

    private void showAnnouncementDialog(String id) {
        String myId = firebaseUser.getUid();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Announcement");
        builder.setMessage("Once deleted, messages cannot be recovered, are you sure you want to delete them?");
        builder.setPositiveButton("Yes", (dialog, which) -> updateMessageIsDeleted(id, myId));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateMessageIsDeleted(String id, String myId) {
        DatabaseReference messagesRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference(FirebaseDBConnection.MESSAGE);
        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel message = snapshot.getValue(MessageModel.class);

                    if (message != null) {
                        if ((message.getReceiver_id().equals(myId) && message.getSender_id().equals(id)) ||
                                (message.getReceiver_id().equals(id) && message.getSender_id().equals(myId))) {
                            if(message.getSender_id().equals(myId)) {
                                snapshot.getRef().child("is_deleted_by_me").setValue(true);
                            }
                            if(message.getReceiver_id().equals(myId)) {
                                snapshot.getRef().child("is_deleted_by_other").setValue(true);
                            }
                        }
                    }
                }
                reloadDataFromFirebase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void reloadDataFromFirebase() {
        listMess.clear();
        ArrayList<String> testList = new ArrayList<>();
        HashSet<String> seen = new HashSet<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
        DatabaseReference databaseReferenceMess = database.getReference().child(FirebaseDBConnection.MESSAGE);
        databaseReferenceMess.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if (messageModel != null) {
                        String UID = null;
                        if(messageModel.getSender_id().equals(firebaseUser.getUid()) && messageModel.getReceiver_id().equals(firebaseUser.getUid())
                                && !messageModel.isIs_deleted_by_me()) {
                            UID = firebaseUser.getUid();
                        }
                        else if(messageModel.getSender_id().equals(firebaseUser.getUid()) && !messageModel.isIs_deleted_by_me()) {
                            UID = messageModel.getReceiver_id();
                        }
                        else if(messageModel.getReceiver_id().equals(firebaseUser.getUid()) && !messageModel.isIs_deleted_by_other()) {
                            UID = messageModel.getSender_id();
                        }
                        if(UID != null) {
                            testList.add(UID);
                        }
                    }
                }
                Collections.reverse(testList);
                for (int i = 0; i < testList.size(); i++) {
                    String item = testList.get(i);
                    if (seen.contains(item)) {
                        testList.remove(i);
                        i--;
                    }
                    else {
                        seen.add(item);
                    }
                }
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersRef = db.collection("users");
                i = 0;
                usersRef.get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            while (i < testList.size()) {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String userId = documentSnapshot.getId();
                                    String firstName = documentSnapshot.getString("first_name");
                                    String lastName = documentSnapshot.getString("last_name");
                                    String imgLink = documentSnapshot.getString("imgAvatar");
                                    if (userId.equals(testList.get(i))) {
                                        listMess.add(new ListMessModel(imgLink,
                                                firstName + " " + lastName, "", userId));
                                    }
                                }
                                i++;
                            }
                            if(listMess.isEmpty()) {
                                txtNotMess.setVisibility(View.VISIBLE);
                            }
                            else {
                                txtNotMess.setVisibility(View.GONE);
                            }
                            listMessAdapter.notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {

                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}