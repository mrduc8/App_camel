package com.dnd.camel.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.dnd.camel.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePassFragment extends Fragment {
    View rootView;
    ImageView img;
    EditText edtNewPass, edtConfirmPass;
    Button btnUpdate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_update_pass, container, false);
        Mapping();
        btnUpdate.setOnClickListener(v -> {
            String newPassword = edtNewPass.getText().toString();
            if(!edtNewPass.getText().toString().equals(edtConfirmPass.getText().toString())) {
                showErrorDialog("The confirmation password does not match the new password");
            }
            else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    showAnnouncementDialog();
                                } else {
                                    showErrorDialog("Password update failed");
                                }
                            });
                }
            }
        });
        return rootView;
    }

    private void Mapping() {
        img = rootView.findViewById(R.id.img);
        edtNewPass = rootView.findViewById(R.id.edtNewPass);
        edtConfirmPass = rootView.findViewById(R.id.edtNewPassConfirm);
        btnUpdate = rootView.findViewById(R.id.btnUpdatePass);

        ConstraintLayout updatePassLayout = rootView.findViewById(R.id.updatePassFragment);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int newHeight = (int) ((811.0 / 891.0) * screenHeight);
        ViewGroup.LayoutParams layoutParams = updatePassLayout.getLayoutParams();
        layoutParams.height = newHeight;
        updatePassLayout.setLayoutParams(layoutParams);

        // img
        int imgWidth = (int) (screenWidth * 0.68);
        int imgHeight = (int) (newHeight * 0.18);
        img.getLayoutParams().width = imgWidth;
        img.getLayoutParams().height = imgHeight;

        // edt
        int edtWidth = (int) (screenWidth * 0.8);
        int edtHeight = (int) (newHeight * 0.067);
        edtNewPass.getLayoutParams().width = edtWidth;
        edtNewPass.getLayoutParams().height = edtHeight;
        edtConfirmPass.getLayoutParams().width = edtWidth;
        edtConfirmPass.getLayoutParams().height = edtHeight;

        // btn
        btnUpdate.getLayoutParams().width = edtWidth;
        btnUpdate.getLayoutParams().height = edtHeight;
    }

    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thông báo");
        builder.setMessage("Updated password successfully");
        builder.setPositiveButton("OK", (dialog, id) -> {
            dialog.dismiss();
            edtNewPass.setText("");
            edtConfirmPass.setText("");
            edtNewPass.requestFocus();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Lỗi");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}