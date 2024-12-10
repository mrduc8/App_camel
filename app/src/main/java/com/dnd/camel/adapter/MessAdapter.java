package com.dnd.camel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.R;
import com.dnd.camel.models.MessageModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessAdapter extends RecyclerView.Adapter<MessAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final Context mContext;
    private final List<MessageModel> listMessage;
    FirebaseUser firebaseUser;
    FirebaseFirestore ft;
    public MessAdapter(Context mContext, List<MessageModel> listMessage) {
        this.mContext = mContext;
        this.listMessage = listMessage;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        }
        else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageModel message = listMessage.get(position);
        holder.message.setText(message.getContent());
        DatabaseReference messageRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference(FirebaseDBConnection.MESSAGE).child(message.getId());
        Map<String, Object> updateData = new HashMap<>();
        if(message.getReceiver_id().equals(firebaseUser.getUid())) {
            updateData.put("is_seen", true);
        }
        messageRef.updateChildren(updateData)
                .addOnSuccessListener(aVoid -> {

                })
                .addOnFailureListener(e -> {

                });
        if (getItemViewType(position) == MSG_TYPE_LEFT) {
            ft = FirebaseFirestore.getInstance();
            DocumentReference document = ft.collection("users").document(message.getSender_id());
            document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String linkImg = doc.getString("imgAvatar");
                        assert linkImg != null;
                        if(linkImg.isEmpty()) {
                            holder.imgAvatar.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.baseline_account_circle_24));
                        }
                        else {
                            Glide.with(mContext)
                                    .load(linkImg)
                                    .circleCrop()
                                    .into(holder.imgAvatar);
                        }
                    }
                }
            });
        }
        holder.itemView.setOnLongClickListener(view -> {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            @SuppressLint("InflateParams") View bottomSheetView = inflater.inflate(R.layout.bottom_sheet, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(mContext);
            bottomSheetDialog.setContentView(bottomSheetView);
            Button btnConfirm = bottomSheetView.findViewById(R.id.btnConfirm);
            btnConfirm.setText("Delete This Mess");
            Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
            if(message.getSender_id().equals(firebaseUser.getUid())) {
                btnCancel.setText("Retrieve Message");
            }
            else {
                btnCancel.setText("Cancel");
            }
            TextView txtTitle = bottomSheetView.findViewById(R.id.txtTitle);
            txtTitle.setText("You want to delete this message?");
            bottomSheetDialog.show();
            btnConfirm.setOnClickListener(v -> {
                showAnnouncementDialog(message.getId(), message.getSender_id(), message.getReceiver_id());
                bottomSheetDialog.dismiss();
            });
            btnCancel.setOnClickListener(v -> {
                if(message.getSender_id().equals(firebaseUser.getUid())) {
                    showAnnouncementDialogRetrieve(message.getId());
                    bottomSheetDialog.dismiss();
                }
                else {
                    bottomSheetDialog.dismiss();
                }
            });
            return true;
        });
    }

    private void showAnnouncementDialogRetrieve(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Announcement");
        builder.setMessage("Once deleted, messages cannot be recovered, are you sure you want to delete them?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            DatabaseReference messageRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference(FirebaseDBConnection.MESSAGE).child(id);
            Map<String, Object> updateData = new HashMap<>();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            updateData.put("is_deleted_by_me", true);
            updateData.put("is_deleted_by_other", true);
            updateData.put("is_seen", true);
            messageRef.updateChildren(updateData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(mContext, "Message revoked", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(mContext, "Message recall failed", Toast.LENGTH_SHORT).show());
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAnnouncementDialog(String id, String sender_id, String receiver_id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Announcement");
        builder.setMessage("Once deleted, messages cannot be recovered, are you sure you want to delete them?");
        builder.setPositiveButton("Xoá", (dialog, which) -> {
            DatabaseReference messageRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference(FirebaseDBConnection.MESSAGE).child(id);
            Map<String, Object> updateData = new HashMap<>();
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            if(sender_id.equals(firebaseUser.getUid()) && receiver_id.equals(firebaseUser.getUid())) {
                updateData.put("is_deleted_by_me", true);
                updateData.put("is_deleted_by_other", true);
            }
            else if(sender_id.equals(firebaseUser.getUid())) {
                updateData.put("is_deleted_by_me", true);
            }
            else {
                updateData.put("is_deleted_by_other", true);
            }
            messageRef.updateChildren(updateData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(mContext, "Message deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(mContext, "Message deletion failed", Toast.LENGTH_SHORT).show());
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView message;
        public ImageView imgAvatar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            imgAvatar = itemView.findViewById(R.id.avatar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(listMessage.get(position).getSender_id().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        }
        return MSG_TYPE_LEFT;
    }
}