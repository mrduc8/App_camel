package com.dnd.camel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.R;
import com.dnd.camel.models.ListMessModel;
import com.dnd.camel.models.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ListMessAdapter extends ArrayAdapter<ListMessModel> {
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    boolean isSeen = true;

    public ListMessAdapter(@NonNull Context context, ArrayList<ListMessModel> listMessView) {
        super(context, 0, listMessView);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        @SuppressLint("ViewHolder") View listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_mess_custom, parent, false);
        ListMessModel currentMess = getItem(position);
        ImageView imageButton = listItemView.findViewById(R.id.imgAvatar);
        ImageView imgTick = listItemView.findViewById(R.id.imgTick);
        TextView userName = listItemView.findViewById(R.id.txtUsername);
        TextView mess = listItemView.findViewById(R.id.txtMess);
        FirebaseDatabase database = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
        DatabaseReference databaseReferenceMess = database.getReference().child(FirebaseDBConnection.MESSAGE);
        databaseReferenceMess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    if (messageModel != null) {
                        if ((messageModel.getSender_id().equals(firebaseUser.getUid()) && messageModel.getReceiver_id().equals(Objects.requireNonNull(currentMess).getId())) ||
                                (messageModel.getSender_id().equals(Objects.requireNonNull(currentMess).getId()) && messageModel.getReceiver_id().equals(firebaseUser.getUid()))) {
                            if(messageModel.getReceiver_id().equals(firebaseUser.getUid()) && !messageModel.isIs_seen()) {
                                isSeen = false;
                                break;
                            }
                        }
                    }
                }
                if(!isSeen) {
                    imgTick.setImageDrawable(getContext().getResources().getDrawable(R.drawable.baseline_brightness_1_24));
                    mess.setTypeface(null, Typeface.BOLD);
                    isSeen = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });

        databaseReferenceMess.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<MessageModel> messageModels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel messageModel = snapshot.getValue(MessageModel.class);
                    if (messageModel != null) {
                        messageModels.add(messageModel);
                    }
                }

                Collections.reverse(messageModels);

                for (MessageModel messageModel : messageModels) {
                    if ((messageModel.getSender_id().equals(firebaseUser.getUid()) && messageModel.getReceiver_id().equals(Objects.requireNonNull(currentMess).getId())) ||
                            (messageModel.getSender_id().equals(Objects.requireNonNull(currentMess).getId()) && messageModel.getReceiver_id().equals(firebaseUser.getUid()))) {
                        if((!messageModel.isIs_deleted_by_me() && messageModel.getSender_id().equals(firebaseUser.getUid()))
                                || (messageModel.getReceiver_id().equals(firebaseUser.getUid()) && !messageModel.isIs_deleted_by_other())) {
                            mess.setText(messageModel.getContent());
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });

        if (currentMess != null) {
            if(currentMess.getImg().isEmpty()) {
                imageButton.setImageDrawable(getContext().getResources().getDrawable(R.drawable.baseline_account_circle_24));
            }
            else {
                Glide.with(getContext())
                        .load(currentMess.getImg())
                        .circleCrop()
                        .into(imageButton);
            }
            userName.setText(currentMess.getUserName());
        }
        return listItemView;
    }
}