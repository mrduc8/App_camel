package com.dnd.camel;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPassActivity extends AppCompatActivity {
    ImageView img;
    EditText edtEmail;
    Button btnResetPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        Mapping();
        btnResetPass.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = edtEmail.getText().toString();

            auth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showAnnouncementDialog();
                        }
                    });
        });
    }

    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Announcement");
        builder.setMessage("The password reset link has been sent to your email address");
        builder.setPositiveButton("Go to the login page", (dialog, id) -> {
            Intent intent = new Intent(ResetPassActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void Mapping() {
        img = findViewById(R.id.img);
        edtEmail = findViewById(R.id.edtEmail);
        btnResetPass = findViewById(R.id.btnResetPass);
        // img
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int imgWidth = (int) (screenWidth * 0.7);
        int imgHeight = (int) (screenHeight * 0.17);
        img.getLayoutParams().width = imgWidth;
        img.getLayoutParams().height = imgHeight;

        // editText
        int edtWidth = (int) (screenWidth * 0.8);
        int edtHeight = (int) (screenHeight * 0.06);
        edtEmail.getLayoutParams().width = edtWidth;
        edtEmail.getLayoutParams().height = edtHeight;

        // btn
        int btnResetWidth = (int) (screenWidth * 0.8);
        int btnResetHeight = (int) (screenHeight * 0.06);
        btnResetPass.getLayoutParams().width = btnResetWidth;
        btnResetPass.getLayoutParams().height = btnResetHeight;
    }
}