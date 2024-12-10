package com.dnd.camel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.dnd.camel.dbconnect.FirebaseDBConnection;
import com.dnd.camel.databinding.ActivityMainBinding;
import com.dnd.camel.fragments.HomeFragment;
import com.dnd.camel.fragments.MenuFragment;
import com.dnd.camel.fragments.MessFragment;
import com.dnd.camel.fragments.NotificationFragment;
import com.dnd.camel.models.MessageModel;
import com.dnd.camel.models.NotiModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    TextView txtCountMess, txtCountNotification;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        txtCountMess = findViewById(R.id.countMess);
        txtCountNotification = findViewById(R.id.countNotification);
        txtCountMess.setVisibility(View.GONE);
        txtCountNotification.setVisibility(View.GONE);
        reloadCountMess();
        reloadCountNotification();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            }
            if (item.getItemId() == R.id.message) {
                replaceFragment(new MessFragment());
            }
            if (item.getItemId() == R.id.fab) {
                Intent intent = new Intent(MainActivity.this, AddPostActivity.class);
                startActivityForResult(intent, 1);
            }
            if (item.getItemId() == R.id.notification) {
                replaceFragment(new NotificationFragment());
            }
            if (item.getItemId() == R.id.menu) {
                replaceFragment(new MenuFragment());
            }
            return true;
        });
    }

    private void reloadCountNotification() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
        DatabaseReference databaseReferenceNotification = database.getReference().child(FirebaseDBConnection.NOTIFICATION);
        databaseReferenceNotification.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NotiModel notiModel = dataSnapshot.getValue(NotiModel.class);
                    assert notiModel != null;
                    if(notiModel.getUserPostId().equals(firebaseUser.getUid()) && notiModel.isIs_active() && !notiModel.isIs_seen()) {
                        count ++;
                        txtCountNotification.setVisibility(View.VISIBLE);
                        txtCountNotification.setText(String.valueOf(count));
                    }
                }
                if(count == 0) {
                    txtCountNotification.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void reloadCountMess() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(FirebaseDBConnection.DB_URL);
        DatabaseReference databaseReferenceMess = database.getReference().child(FirebaseDBConnection.MESSAGE);
        databaseReferenceMess.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> arrayUserId = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModel messageModel = snapshot.getValue(MessageModel.class);
                    if (messageModel != null) {
                        if(messageModel.getReceiver_id().equals(firebaseUser.getUid()) && !messageModel.isIs_seen()) {
                            if(!arrayUserId.contains(messageModel.getSender_id())) {
                                arrayUserId.add(messageModel.getSender_id());
                            }
                        }
                    }
                    if(arrayUserId.isEmpty()) {
                        txtCountMess.setVisibility(View.GONE);
                    }
                    else {
                        txtCountMess.setVisibility(View.VISIBLE);
                        txtCountMess.setText(String.valueOf(arrayUserId.size()));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            binding.bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }
}