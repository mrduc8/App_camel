package com.dnd.camel;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dnd.camel.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    ImageView img;
    TextView txtHaveAccount;
    Button btnSignUp, btnSignIn;
    EditText edtEmail, edtPass, edtFirstName, edtLastName;
    FirebaseAuth mAuth;
    FirebaseFirestore ft;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        ft = FirebaseFirestore.getInstance();
        Mapping();
        btnSignUp.setOnClickListener(v -> SignUp());
        btnSignIn.setOnClickListener((v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        }));
    }

    private void Mapping() {
        img = findViewById(R.id.img);
        txtHaveAccount = findViewById(R.id.txtHaveAccount);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        edtEmail = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        edtFirstName = findViewById(R.id.edtFirstname);
        edtLastName = findViewById(R.id.edtLastname);

        // img
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int imgWidth = (int) (screenWidth * 0.7);
        int imgHeight = (int) (screenHeight * 0.17);
        img.getLayoutParams().width = imgWidth;
        img.getLayoutParams().height = imgHeight;

        // edt
        int edtWidth = (int) (screenWidth * 0.8);
        int edtHeight = (int) (screenHeight * 0.06);
        edtEmail.getLayoutParams().width = edtWidth;
        edtEmail.getLayoutParams().height = edtHeight;
        edtPass.getLayoutParams().width = edtWidth;
        edtPass.getLayoutParams().height = edtHeight;
        edtFirstName.getLayoutParams().width = edtWidth;
        edtFirstName.getLayoutParams().height = edtHeight;
        edtLastName.getLayoutParams().width = edtWidth;
        edtLastName.getLayoutParams().height = edtHeight;

        // btn
        int btnSignInWidth = (int) (screenWidth * 0.6);
        int btnSignInHeight = (int) (screenHeight * 0.06);
        int btnSignUpWidth = (int) (screenWidth * 0.8);
        int btnSignUpHeight = (int) (screenHeight * 0.06);
        btnSignIn.getLayoutParams().width = btnSignInWidth;
        btnSignIn.getLayoutParams().height = btnSignInHeight;
        btnSignUp.getLayoutParams().width = btnSignUpWidth;
        btnSignUp.getLayoutParams().height = btnSignUpHeight;

        // txt
        int txtWidth = (int) (screenWidth * 0.57);
        int txtHeight = (int) (screenHeight * 0.04);
        txtHaveAccount.getLayoutParams().width = txtWidth;
        txtHaveAccount.getLayoutParams().height = txtHeight;
    }

    private void SignUp() {
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()) {
                        userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        System.out.println("Test " + userID);
                        DocumentReference documentReference = ft.collection("users").document(userID);
                        UserModel user = new UserModel();
                        user.setFirst_name(firstName);
                        user.setLast_name(lastName);
                        user.setEmail(email);
                        user.setPassword(password);
                        user.setImgAvatar("");
                        user.setImgBackground("");
                        user.setAddress("");
                        user.setSDT("");
                        user.setBirthday("");
                        documentReference.set(user)
                                .addOnSuccessListener(unused -> Toast.makeText(SignUpActivity.this, "Data added successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        showAnnouncementDialog();
                    }
                    else {
                        showErrorDialog();
                    }
                });
    }
    private void showAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Announcement");
        builder.setMessage("Sign Up Success");
        builder.setPositiveButton("Go to the login page", (dialog, id) -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Registration information is invalid, please check again");
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}