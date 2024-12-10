package com.dnd.camel.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dnd.camel.R;
import com.dnd.camel.SignInActivity;
import com.dnd.camel.adapter.ListMenuAdapter;
import com.dnd.camel.models.ListMenuModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    FirebaseFirestore ft;
    View rootView;
    FrameLayout frameLayout;
    Button btnLogout;
    ListView lvMenu;
    TextView txtUsername, txtMenu;
    ImageView imgAvatar;
    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        Mapping();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ft = FirebaseFirestore.getInstance();
        assert currentUser != null;
        String uid = currentUser.getUid();
        DocumentReference document = ft.collection("users").document(uid);
        document.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()) {
                    String linkImg = doc.getString("imgAvatar");
                    assert linkImg != null;
                    if (linkImg.isEmpty()) {
                        imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.baseline_account_circle_24));
                    } else {
                        Glide.with(requireContext())
                                .load(linkImg)
                                .circleCrop()
                                .into(imgAvatar);
                    }
                }
            }
        });
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);
        String[] username = new String[1];
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document1 = task.getResult();
                if (document1.exists()) {
                    String fName = document1.getString("first_name");
                    String lName = document1.getString("last_name");
                    username[0] = fName + " " + lName;
                    txtUsername.setText(username[0]);
                }
            }
        });
        frameLayout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("userName", username[0]);
            bundle.putString("id", uid);
            UserHomeFragment userHomeFragment = new UserHomeFragment();
            userHomeFragment.setArguments(bundle);
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.frame_layout, userHomeFragment);
            fragmentTransaction.commit();
        });

        ArrayList<ListMenuModel> listMenu = new ArrayList<>();
        listMenu.add(new ListMenuModel(R.drawable.icon_update_info, "Update Info"));
        listMenu.add(new ListMenuModel(R.drawable.icon_save_post, "Saved"));
        listMenu.add(new ListMenuModel(R.drawable.icon_update_pass, "Update Password"));
        ListMenuAdapter listMenuAdapter = new ListMenuAdapter(requireActivity(), listMenu);
        lvMenu.setAdapter(listMenuAdapter);
        lvMenu.setOnItemClickListener((parent, view, position, id) -> {
            ListMenuModel selectedItem = listMenu.get(position);
            if(selectedItem.getTitle().equals("Update Info")) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new ProfileFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            if(selectedItem.getTitle().equals("Update Password")) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new UpdatePassFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            if(selectedItem.getTitle().equals("Saved")) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new SavedPostFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        btnLogout.setOnClickListener(v -> {
            @SuppressLint("InflateParams") View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            bottomSheetDialog.setContentView(bottomSheetView);
            TextView txtTitle = bottomSheetView.findViewById(R.id.txtTitle);
            txtTitle.setText("Are you sure you want to logout?");

            Button btnConfirmLogout = bottomSheetView.findViewById(R.id.btnConfirm);
            btnConfirmLogout.setOnClickListener(v1 -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            });

            Button btnCancelLogout = bottomSheetView.findViewById(R.id.btnCancel);
            btnCancelLogout.setOnClickListener(v12 -> bottomSheetDialog.dismiss());

            bottomSheetDialog.show();
        });
        return rootView;
    }

    private void Mapping() {
        frameLayout = rootView.findViewById(R.id.userHome);
        btnLogout = rootView.findViewById(R.id.btnLogout);
        lvMenu = rootView.findViewById(R.id.lvMenu);
        txtUsername = rootView.findViewById(R.id.txtUsername);
        txtMenu = rootView.findViewById(R.id.txtMenu);
        imgAvatar = rootView.findViewById(R.id.avatar);

        // Screen
        ConstraintLayout menuFragment = rootView.findViewById(R.id.menuFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = menuFragment.getLayoutParams();
        layoutParams.height = newHeight;
        menuFragment.setLayoutParams(layoutParams);

        // ListView
        frameLayout.getLayoutParams().height = (int) (newHeight * 0.52);

        // Fragment
        int fragmentWidth = (int) (screenWidth * 0.9);
        int fragmentHeight = (int) (newHeight * 0.15);
        frameLayout.getLayoutParams().width = fragmentWidth;
        frameLayout.getLayoutParams().height = fragmentHeight;

        // btnLogout
        int btnWidth = (int) (screenWidth * 0.9);
        int btnHeight = (int) (newHeight * 0.064);
        btnLogout.getLayoutParams().width = btnWidth;
        btnLogout.getLayoutParams().height = btnHeight;

    }
}