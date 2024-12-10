package com.dnd.camel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.PostDetailActivity;
import com.dnd.camel.R;
import com.dnd.camel.models.CommentModel;
import com.dnd.camel.models.NotiModel;
import com.dnd.camel.models.UserModel;
import com.dnd.camel.utils.TimestampUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final Context context;
    ArrayList<CommentModel> commentList;
    FirebaseFirestore ft = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
    String currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    public CommentAdapter(Context context, ArrayList<CommentModel> commentList) {
        this.context = context;
        this.commentList = commentList;
    }
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_comment, parent, false);
        return new CommentViewHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        CommentModel comment = commentList.get(position);

        // get user
        ft.collection("users").document(comment.getUser_id()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserModel user = documentSnapshot.toObject(UserModel.class);
                assert user != null;
                user.setId(documentSnapshot.getId());
                holder.user = user;
                holder.txtUsername.setText(user.getFirst_name() + " " + user.getLast_name());
                if (user.getImgAvatar() == null || user.getImgAvatar().isEmpty()) {
                    holder.ivAvatar.setImageResource(R.drawable.background_app);
                } else {
//                    Picasso.get().load(user.getImgAvatar()).into(holder.ivAvatar);
                    Glide.with(context).load(user.getImgAvatar()).circleCrop().into(holder.ivAvatar);
                }
            }
        });
        holder.txtCommentContent.setText(comment.getContent());
        holder.txtDate.setText(TimestampUtil.convertTimestampToDateString(comment.getCreated_date()));

        // like
        if (comment.getLikes() == null) {
            comment.setLikes(new HashMap<>());
        }
        holder.btnLike.setText(String.valueOf(comment.getLikes().size()));
        // get btn drawable
        Drawable likeIcon = holder.btnLike.getCompoundDrawables()[0];
        if (comment.getLikes().containsKey(currentUser)) {
            likeIcon.setTint(context.getResources().getColor(R.color.blue));
        }
        else {
            likeIcon.setTint(context.getResources().getColor(R.color.black));
        }
        holder.btnLike.setOnClickListener(v -> {
            // like comment
            if (comment.getLikes() == null) {
                comment.setLikes(new HashMap<>());
                //like post
            }
            if (comment.getLikes().containsKey(currentUser)) {
                comment.getLikes().remove(currentUser);
                likeIcon.setTint(context.getResources().getColor(R.color.black));
            }
            else {
                comment.getLikes().put(currentUser, true);
                likeIcon.setTint(context.getResources().getColor(R.color.blue));
                if(!comment.getUser_id().equals(currentUser)) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection("users").document(currentUser);
                    userRef.get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("first_name");
                            String lastName = documentSnapshot.getString("last_name");
                            String img = documentSnapshot.getString("imgAvatar");
                            DatabaseReference notificationRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference().child("Notification");
                            String notificationId = notificationRef.push().getKey();
                            NotiModel notificationModel = new NotiModel();
                            notificationModel.setPostId(comment.getPost_id());
                            notificationModel.setUserLikeId(currentUser);
                            notificationModel.setUserPostId(comment.getUser_id());
                            notificationModel.setImgAvatar(img);
                            notificationModel.setContent(firstName + " " + lastName + " đã thích bình luận: " + comment.getContent());
                            notificationModel.setIs_active(true);
                            notificationModel.setIs_seen(false);
                            notificationModel.setIs_click(false);
                            assert notificationId != null;
                            notificationRef.child(notificationId).setValue(notificationModel);
                        }
                    }).addOnFailureListener(e -> {

                    });
                }
            }
            // update likes
            DatabaseReference commentRef;
            if (comment.getParent_id() != null) {
                commentRef = db.getReference(FirebaseDBConnection.COMMENT).child(comment.getPost_id()).child(comment.getParent_id()).child("replies").child(comment.getId());
            } else {
                commentRef = db.getReference(FirebaseDBConnection.COMMENT).child(comment.getPost_id()).child(comment.getId());
            }
            commentRef.child("likes").setValue(comment.getLikes());
        });

        // reply
        holder.ibReply.setOnClickListener(v -> {
            PostDetailActivity activity = (PostDetailActivity) context;
            activity.replyTo = comment.getParent_id() == null ? comment.getId() : comment.getParent_id();
            activity.showReplyTo(holder.user.getFirst_name() + " " + holder.user.getLast_name());
        });
        if (comment.getParent_id() == null) {
            holder.verticalLine.setVisibility(View.GONE);
        } else {
            holder.verticalLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtCommentContent, txtDate;
        Button btnLike;
        ImageButton ibReply;
        ImageView ivAvatar;
        UserModel user;
        View verticalLine;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtCommentContent = itemView.findViewById(R.id.txtCommentContent);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnLike = itemView.findViewById(R.id.btnLike);
            ibReply = itemView.findViewById(R.id.ibReply);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            verticalLine = itemView.findViewById(R.id.vertical_line);
        }
    }
}
