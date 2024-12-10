package com.dnd.camel.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.PostDetailActivity;
import com.dnd.camel.R;
import com.dnd.camel.fragments.UserHomeFragment;
import com.dnd.camel.models.NotiModel;
import com.dnd.camel.models.PostModel;
import com.dnd.camel.models.UserModel;
import com.dnd.camel.utils.TimestampUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.UserPostViewHolder> {
    private final Context context;
    ArrayList<PostModel> postList;
    FirebaseDatabase db = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUser;
    public UserPostAdapter(Context context, ArrayList<PostModel> postList) {
        this.context = context;
        this.postList = postList;
        currentUser = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_post, parent, false);
        return new UserPostViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        // load post
        PostModel postModel = postList.get(position);
        holder.txtPostTitle.setText(postModel.getTitle());

        if (postModel.isAnonymous()) {
            holder.txtUserName.setText("Posted anonymously");
            holder.imgUserPost.setImageResource(R.drawable.anonymous);
        }
        else { // get user
            FirebaseFirestore.getInstance().collection("users").document(postList.get(position).getUser_id()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UserModel userModel = task.getResult().toObject(UserModel.class);
                    assert userModel != null;
                    String userName = userModel.getFirst_name() + " " + userModel.getLast_name();

                    holder.txtUserName.setText(userName);
                    if (userModel.getImgAvatar() == null || userModel.getImgAvatar().isEmpty()) {
                        holder.imgUserPost.setImageResource(R.drawable.baseline_account_circle_24);
                    } else {
//                    Picasso.get().load(userModel.getImgAvatar()).into(holder.imgUserPost);
                        Glide.with(context).load(userModel.getImgAvatar()).circleCrop().into(holder.imgUserPost);
                    }
                }
            });
        }



        // get likes
        int likes = postModel.getUser_likes().size();
        holder.btnLike.setText(String.valueOf(likes));
        // check if user liked post
        // get button drawable
        Drawable drawable = holder.btnLike.getCompoundDrawables()[0];
        boolean liked = postModel.getUser_likes().containsKey(currentUser);
        if (liked) {
            drawable.setTint(context.getResources().getColor(R.color.blue));
        }
        else {
            drawable.setTint(context.getResources().getColor(R.color.primary_color));
        }

        // btnLike onClick
        holder.btnLike.setOnClickListener(v -> {
            DatabaseReference postRef = db.getReference(FirebaseDBConnection.POST).child(postModel.getId());
            // get newest user likes
            postRef.child("user_likes").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() == null) {
                        postModel.setUser_likes(new HashMap<>());
                    }
                    else {
                        postModel.setUser_likes((Map<String, Integer>) task.getResult().getValue());
                    }
                    Log.d("UserPostAdapter", "user likes: " + postModel.getUser_likes());
                    if (postModel.getUser_likes().containsKey(currentUser)) {
                        // unlike post
                        postModel.getUser_likes().remove(currentUser);
                        drawable.setTint(context.getResources().getColor(R.color.primary_color));
                        // cập nhật trường is_active = false (dùng so sánh postid = postmodel.id ; người like là mình ; người post: postModel.UserId = snap
                    }
                    else {
                        // like post
                        postModel.getUser_likes().put(currentUser, 1);
                        drawable.setTint(context.getResources().getColor(R.color.blue));
                        //insert notification
                        insertNotification(postModel);

                    }
                    postRef.child("user_likes").setValue(postModel.getUser_likes());
                    holder.btnLike.setText(String.valueOf(postModel.getUser_likes().size()));
                }
                else {
                    Log.d("UserPostAdapter", "Error getting user likes: ", task.getException());
                }
            });

        });

        boolean saved = postModel.getList_user_save().containsKey(currentUser);
        if (saved) {
            holder.btnSave.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.yellow));
        }
        else {
            holder.btnSave.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button));
        }
        // btnSave onClick
        holder.btnSave.setOnClickListener(v -> {
            DatabaseReference postRef = db.getReference(FirebaseDBConnection.POST).child(postModel.getId());
            // get newest user save
            postRef.child("list_user_save").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (postModel.getList_user_save().containsKey(currentUser)) {
                        // unSave post
                        postModel.getList_user_save().remove(currentUser);
                        holder.btnSave.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.button));
                    }
                    else {
                        // Save post
                        postModel.getList_user_save().put(currentUser, 1);
                        holder.btnSave.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.yellow));
                    }
                    postRef.child("list_user_save").setValue(postModel.getList_user_save());
                }
                else {
                    Log.d("UserPostAdapter", "Error getting user likes: ", task.getException());
                }
            });
        });

        String date = TimestampUtil.convertTimestampToDateString(postModel.getCreated_date());
        holder.txtDate.setText(date);
        if (postModel.getPostImg() == null || postModel.getPostImg().isEmpty()) {
            holder.imgPost.setVisibility(View.GONE);
        }
        else {
            holder.imgPost.setVisibility(View.VISIBLE);
            Picasso.get().load(postModel.getPostImg()).into(holder.imgPost);
        }

        holder.imgUserPost.setOnClickListener(v -> {
            if (postModel.isAnonymous()) {
                return;
            }
            String userName = holder.txtUserName.getText().toString();
            String id = postModel.getUser_id();
            Bundle bundle = new Bundle();
            bundle.putString("userName", userName);
            bundle.putString("id", id);
            UserHomeFragment userHomeFragment = new UserHomeFragment();
            userHomeFragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout, userHomeFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        // onItemClick
        holder.itemView.setOnClickListener(v -> {
            // go to post detail
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", postModel.getId());
            context.startActivity(intent);
        });

        // btn Comment
        holder.btnComment.setOnClickListener(v -> {
            // go to post detail
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra("post_id", postModel.getId());
            intent.putExtra("isComment", true);
            context.startActivity(intent);
        });

        // menu item
        holder.ibMore.setOnClickListener(v -> {
            // show menu
            showPopupMenu(v, postModel);
        });
    }

    private void insertNotification(PostModel postModel) {
        String id = firebaseUser.getUid();
        if(!postModel.getUser_id().equals(id)) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("users").document(id);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String firstName = documentSnapshot.getString("first_name");
                    String lastName = documentSnapshot.getString("last_name");
                    String img = documentSnapshot.getString("imgAvatar");
                    DatabaseReference notificationRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference().child(FirebaseDBConnection.NOTIFICATION);
                    String notificationId = notificationRef.push().getKey();
                    NotiModel notificationModel = new NotiModel();
                    notificationModel.setPostId(postModel.getId());
                    notificationModel.setUserLikeId(id);
                    notificationModel.setUserPostId(postModel.getUser_id());
                    notificationModel.setImgAvatar(img);
                    notificationModel.setContent(firstName + " " + lastName + " đã thích bài viết: " + postModel.getTitle());
                    notificationModel.setIs_active(true);
                    notificationModel.setIs_seen(false);
                    notificationModel.setIs_click(false);
                    assert notificationId != null;
                    notificationRef.child(notificationId).setValue(notificationModel);
                }
            }).addOnFailureListener(e -> {
                // Xử lý lỗi nếu có
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void showPopupMenu(View v, PostModel postModel) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.post_menu);
        // hide delete option if post is not created by current user
        if (!postModel.getUser_id().equals(currentUser)) {
            popupMenu.getMenu().findItem(R.id.post_delete).setVisible(false);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.post_delete) {
                // set post is_deleted = true
                DatabaseReference postRef = db.getReference(FirebaseDBConnection.POST).child(postModel.getId());
                postRef.child("is_deleted").setValue(true);
                postList.remove(postModel);
                notifyDataSetChanged();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class UserPostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserPost, imgPost;
        TextView txtUserName, txtPostTitle, txtDate;
        Button btnLike, btnComment, btnSave;
        ImageButton ibMore;

        public UserPostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserPost = itemView.findViewById(R.id.ivAvatar);
            imgPost = itemView.findViewById(R.id.ivPostImage);
            txtUserName = itemView.findViewById(R.id.txtUsername);
            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnSave = itemView.findViewById(R.id.btnSave);
            ibMore = itemView.findViewById(R.id.ibMore);
        }
    }
}
