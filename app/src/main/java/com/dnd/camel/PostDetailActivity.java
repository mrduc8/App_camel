package com.dnd.camel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.adapter.CommentAdapter;
import com.dnd.camel.models.CommentModel;
import com.dnd.camel.models.NotiModel;
import com.dnd.camel.models.PostModel;
import com.dnd.camel.models.UserModel;
import com.dnd.camel.utils.TimestampUtil;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PostDetailActivity extends AppCompatActivity {
    FirebaseDBConnection db = FirebaseDBConnection.getInstance();
    TextView txtPostTitle, txtPostContent, txtPostDate, txtFlair, txtUserName, txtReplyTo, txtCancelReply;
    ImageView imgPost, imgAvatar;
    Button btnLike, btnComment, btnSave;
    ImageButton ibBack, ibSendComment;
    EditText edtComment;
    RecyclerView rvComment;
    String postId;
    PostModel post;
    LinearLayout llReply;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String currentUserId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
    ArrayList<CommentModel> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;
    public String replyTo = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        edtComment = findViewById(R.id.edtComment);
        ibSendComment = findViewById(R.id.ibSendComment);
        rvComment = findViewById(R.id.rvComment);
        txtPostTitle = findViewById(R.id.txtPostTitle);
        txtPostContent = findViewById(R.id.txtPostContent);
        txtPostDate = findViewById(R.id.txtDate);
        txtUserName = findViewById(R.id.txtUsername);
        txtFlair = findViewById(R.id.txtFlair);
        imgPost = findViewById(R.id.ivPostImage);
        imgAvatar = findViewById(R.id.ivAvatar);
        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(v -> finish());
        btnLike = findViewById(R.id.btnLike);
        btnComment = findViewById(R.id.btnComment);
        btnSave = findViewById(R.id.btnSave);
        btnComment.setOnClickListener(v -> {
            edtComment.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtComment, InputMethodManager.SHOW_IMPLICIT);
        });
        commentAdapter = new CommentAdapter(this, commentList);
        rvComment.setAdapter(commentAdapter);
        rvComment.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));
        llReply = findViewById(R.id.llReply);
        txtReplyTo = findViewById(R.id.txtReplyingTo);
        txtCancelReply = findViewById(R.id.txtCancelReply);
        txtCancelReply.setOnClickListener(v -> {
            llReply.setVisibility(View.GONE);
            replyTo = null;
        });

        postId = getIntent().getStringExtra("post_id");
        boolean isComment = getIntent().getBooleanExtra("isComment", false);
        if (isComment) {
            btnComment.performClick();
        }
        getPost(postId).addOnCompleteListener(task -> {
            post = task.getResult();
            loadPost();
        });

        // Navigate to User Home
//        imgAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String userName = txtUserName.getText().toString();
//                Bundle bundle = new Bundle();
//                bundle.putString("userName", userName);
//                bundle.putString("id", postId);
//                UserHomeFragment userHomeFragment = new UserHomeFragment();
//                userHomeFragment.setArguments(bundle);
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.frame_layout, userHomeFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
//            }
//        });

        // comment
        ibSendComment.setOnClickListener(v -> sendComment());
        loadComment();

        // btn like
        btnLike.setOnClickListener(v -> {
            FirebaseDatabase fb = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
            // like post
            DatabaseReference postRef = fb.getReference(FirebaseDBConnection.POST).child(post.getId());
            // get newest user likes
            postRef.child("user_likes").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().getValue() == null) {
                        post.setUser_likes(new HashMap<>());
                    }
                    else {
                        post.setUser_likes((Map<String, Integer>) task.getResult().getValue());
                    }
                    Log.d("UserPostAdapter", "user likes: " + post.getUser_likes());
                    Drawable likeIcon = btnLike.getCompoundDrawables()[0];
                    if (post.getUser_likes().containsKey(currentUserId)) {
                        // unlike post
                        post.getUser_likes().remove(currentUserId);
                        likeIcon.setTint(getResources().getColor(R.color.primary_color));
                    }
                    else {
                        // like post
                        post.getUser_likes().put(currentUserId, 1);
                        likeIcon.setTint(getResources().getColor(R.color.blue));
                    }
                    postRef.child("user_likes").setValue(post.getUser_likes());
                    btnLike.setText(String.valueOf(post.getUser_likes().size()));
                }
                else {
                    Log.d("UserPostAdapter", "Error getting user likes: ", task.getException());
                }
            });
        });

        // btn like save
        btnSave.setOnClickListener(v -> {
            FirebaseDatabase fb = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
            // save post
            DatabaseReference postRef = fb.getReference(FirebaseDBConnection.POST).child(post.getId());
            // get newest user likes
            postRef.child("list_user_save").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (post.getList_user_save().containsKey(currentUserId)) {
                        // un save post
                        post.getList_user_save().remove(currentUserId);
                        btnSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button));
                    }
                    else {
                        // like post
                        post.getList_user_save().put(currentUserId, 1);
                        btnSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.yellow));
                    }
                    postRef.child("list_user_save").setValue(post.getList_user_save());
                }
                else {
                    Log.d("UserPostAdapter", "Error getting user likes: ", task.getException());
                }
            });
        });


    }
    @SuppressLint("SetTextI18n")
    public void showReplyTo(String username) {
        llReply.setVisibility(View.VISIBLE);
        txtReplyTo.setText("Replying to " + username + "'s comment");
    }

    private void loadComment() {
        DatabaseReference commentRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                .getReference(FirebaseDBConnection.COMMENT)
                .child(postId);
        commentRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    CommentModel comment = commentSnapshot.getValue(CommentModel.class);
                    assert comment != null;
                    comment.setId(commentSnapshot.getKey());
                    commentList.add(comment);
                    if (comment.getReplies() != null) {
                        for (Map.Entry<String, CommentModel> entry : comment.getReplies().entrySet()) {
                            CommentModel reply = entry.getValue();
                            reply.setId(entry.getKey());
                            commentList.add(reply);
                        }
                    }
                    Log.d("Comment", "size: " + commentList.size());
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void sendComment() {////gui cmt
        String commentContent = edtComment.getText().toString();
        if (commentContent.isEmpty()) {
            return;
        }
        DatabaseReference commentRef;
        if (replyTo == null) {
            commentRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                    .getReference(FirebaseDBConnection.COMMENT)
                    .child(post.getId())
                    .push();
            DatabaseReference postRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference(FirebaseDBConnection.POST);
            postRef.child(post.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userId = dataSnapshot.child("user_id").getValue(String.class);
                        assert userId != null;
                        if(!userId.equals(currentUserId)) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("users").document(currentUserId);
                            userRef.get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String firstName = documentSnapshot.getString("first_name");
                                    String lastName = documentSnapshot.getString("last_name");
                                    String img = documentSnapshot.getString("imgAvatar");
                                    DatabaseReference notificationRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference().child(FirebaseDBConnection.NOTIFICATION);
                                    String notificationId = notificationRef.push().getKey();
                                    NotiModel notificationModel = new NotiModel();
                                    notificationModel.setPostId(post.getId());
                                    notificationModel.setUserLikeId(currentUserId);
                                    notificationModel.setUserPostId(userId);
                                    notificationModel.setImgAvatar(img);
                                    notificationModel.setContent(firstName + " " + lastName + " đã bình luận về bài viết: " + post.getTitle());
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else {
            commentRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                    .getReference(FirebaseDBConnection.COMMENT)
                    .child(post.getId()).child(replyTo).child("replies")
                    .push();

            DatabaseReference commentRefNew = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference(FirebaseDBConnection.COMMENT)
                    .child(post.getId())
                    .child(replyTo);
            commentRefNew.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userId = dataSnapshot.child("user_id").getValue(String.class);
                        String content = dataSnapshot.child("content").getValue(String.class);
                        assert userId != null;
                        if(!userId.equals(currentUserId)) {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("users").document(currentUserId);
                            userRef.get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String firstName = documentSnapshot.getString("first_name");
                                    String lastName = documentSnapshot.getString("last_name");
                                    String img = documentSnapshot.getString("imgAvatar");
                                    DatabaseReference notificationRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL).getReference().child(FirebaseDBConnection.NOTIFICATION);
                                    String notificationId = notificationRef.push().getKey();
                                    NotiModel notificationModel = new NotiModel();
                                    notificationModel.setPostId(post.getId());
                                    notificationModel.setUserLikeId(currentUserId);
                                    notificationModel.setUserPostId(userId);
                                    notificationModel.setImgAvatar(img);
                                    notificationModel.setContent(firstName + " " + lastName + " đã phản hồi bình luận: " + content);
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        CommentModel comment = new CommentModel();
        comment.setContent(commentContent);
        comment.setUser_id(currentUserId);
        comment.setPost_id(post.getId());
        comment.setParent_id(replyTo);
        Long timestamp = System.currentTimeMillis();
        comment.setCreated_date(timestamp);
        commentRef.setValue(comment);
        edtComment.setText("");
        txtCancelReply.performClick();
    }

    private void loadPost() {
        btnLike.setText(String.valueOf(post.getUser_likes().size()));
        Drawable likeIcon = btnLike.getCompoundDrawables()[0];
        if (post.getUser_likes().containsKey(currentUserId)) {
            likeIcon.setTint(getResources().getColor(R.color.blue));
        }
        else {
            likeIcon.setTint(getResources().getColor(R.color.primary_color));
        }
        if (post.getList_user_save().containsKey(currentUserId)) {
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.yellow));
        }
        else {
            btnSave.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button));
        }
        txtPostTitle.setText(post.getTitle());
        txtPostContent.setText(post.getContent());
        txtPostDate.setText(TimestampUtil.convertTimestampToDateString(post.getCreated_date()));
        if (post.getPostImg() == null || post.getPostImg().isEmpty()) {
            imgPost.setVisibility(View.GONE);
        }
        else {
            Picasso.get().load(post.getPostImg()).into(imgPost);
        }
    }
    private Task<PostModel> getPost(String postId) {
        TaskCompletionSource<PostModel> taskCompletionSource = new TaskCompletionSource<>();
        DatabaseReference postRef = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL)
                .getReference(FirebaseDBConnection.POST)
                .child(postId);
        // get post
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostModel post = snapshot.getValue(PostModel.class);
                assert post != null;
                post.setId(snapshot.getKey());
                taskCompletionSource.setResult(post);

                if (post.isAnonymous()) {
                    txtUserName.setText("Posted anonymously");
                    imgAvatar.setImageResource(R.drawable.anonymous);
                }
                else {
                    // get user
                    Log.d("User ID", post.getUser_id());
                    FirebaseFirestore.getInstance().collection("users").document(post.getUser_id()).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel userModel = task.getResult().toObject(UserModel.class);
                            assert userModel != null;
                            String userName = userModel.getFirst_name() + " " + userModel.getLast_name();
                            txtUserName.setText(userName);
                            if (userModel.getImgAvatar() == null || userModel.getImgAvatar().isEmpty()) {
                                imgAvatar.setImageResource(R.drawable.baseline_account_circle_24);
                            }
                            else {
//                            Picasso.get().load(userModel.getImgAvatar()).into(imgAvatar);
                                Glide.with(PostDetailActivity.this).load(userModel.getImgAvatar()).circleCrop().into(imgAvatar);

                            }
                        }
                    });
                }
                // get flair
                db.readData(FirebaseDBConnection.FLAIR, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String flairId = post.getFlair_id();
                        String flairName = snapshot.child(flairId).child("name").getValue(String.class);
                        txtFlair.setText(flairName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return taskCompletionSource.getTask();
    }
}