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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    ImageView img;
    TextView txtNeedAccount;
    Button btnSignIn, btnSignUp, btnResetPass;
    EditText edtEmail, edtPass;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null) {
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        Mapping();
        mAuth = FirebaseAuth.getInstance();
        btnSignIn.setOnClickListener(v -> SignIn());
        btnSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });
        btnResetPass.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ResetPassActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Invalid login information");
        builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void SignIn() {
        String email = edtEmail.getText().toString();
        String password = edtPass.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        showErrorDialog();
                    }
                });
    }

    private void Mapping() {
        btnSignIn = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnResetPass = findViewById(R.id.btnResetPass);
        edtEmail = findViewById(R.id.edtUsername);
        edtPass = findViewById(R.id.edtPassword);
        img = findViewById(R.id.img);
        txtNeedAccount = findViewById(R.id.txtNeedAccount);

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
        int edtHeight = (int) (screenHeight * 0.0674);
        edtEmail.getLayoutParams().width = edtWidth;
        edtEmail.getLayoutParams().height = edtHeight;
        edtPass.getLayoutParams().width = edtWidth;
        edtPass.getLayoutParams().height = edtHeight;

        // btn and txt
        int btnResetWidth = (int) (screenWidth * 0.7);
        int btnResetHeight = (int) (screenHeight * 0.0561);

        int btnSignInWidth = (int) (screenWidth * 0.8);
        int btnSignInHeight = (int) (screenHeight * 0.068);

        int btnSignUpWidth = (int) (screenWidth * 0.6);
        int btnSignUpHeight = (int) (screenHeight * 0.068);

        int txtWidth = (int) (screenWidth * 0.6);
        int txtHeight = (int) (screenHeight * 0.043);

        btnResetPass.getLayoutParams().width = btnResetWidth;
        btnResetPass.getLayoutParams().height = btnResetHeight;
        btnSignIn.getLayoutParams().width = btnSignInWidth;
        btnSignIn.getLayoutParams().height = btnSignInHeight;
        btnSignUp.getLayoutParams().width = btnSignUpWidth;
        btnSignUp.getLayoutParams().height = btnSignUpHeight;
        txtNeedAccount.getLayoutParams().width = txtWidth;
        txtNeedAccount.getLayoutParams().height = txtHeight;
    }
}