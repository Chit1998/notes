package com.notes.auth;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.notes.R;
import com.notes.utils.Constraint;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private EditText eFullName;
    private EditText eEmail;
    private EditText ePass;
    private TextView btnSignUp;
    private TextView txtSignIn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.purple_500));
        setContentView(R.layout.activity_sign_up);
        init();
        auth = FirebaseAuth.getInstance();

        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        });

        validation();
        btnSignUp.setOnClickListener(v -> {
            if (eFullName.getText().toString().isEmpty()){
                eFullName.setError(Constraint.empty);
                eFullName.setBackgroundResource(R.drawable.edit_error_drawable);
            }else if (eEmail.getText().toString().isEmpty()){
                eEmail.setError(Constraint.empty);
                eEmail.setBackgroundResource(R.drawable.edit_error_drawable);
            }else if (ePass.getText().toString().isEmpty()){
                ePass.setError(Constraint.empty);
                ePass.setBackgroundResource(R.drawable.edit_error_drawable);
            }else {
                eEmail.setBackgroundResource(R.drawable.edit_drawable_layout);
                ePass.setBackgroundResource(R.drawable.edit_drawable_layout);
                signUp(eEmail.getText().toString().trim(), ePass.getText().toString().trim());
            }
        });

    }

    private void init(){
        eFullName = findViewById(R.id.eFullName);
        eEmail = findViewById(R.id.eEmail);
        ePass = findViewById(R.id.ePass);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtSignIn = findViewById(R.id.txtSignIn);
    }

    private void validation() {
        eEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches(Constraint.emailPattern)){
                    eEmail.setError(Constraint.email_valid);
                    btnSignUp.setEnabled(false);
                }else {
                    btnSignUp.setEnabled(true);
                }
            }
        });
        ePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!(s.toString().length() > 7)){
                    ePass.setError(Constraint.password_length);
                    btnSignUp.setEnabled(false);
                }else {
                    btnSignUp.setEnabled(true);
                }
            }
        });
    }


    private void signUp(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        verifyEmailLinkSent();
                    }else {
                        Toast.makeText(SignUpActivity.this, Constraint.exist_user, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Constraint.setToast(SignUpActivity.this,e.getMessage()));
    }

    private void verifyEmailLinkSent() {
        FirebaseUser user = auth.getCurrentUser();

        Objects.requireNonNull(user).sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Constraint.setToast(SignUpActivity.this,Constraint.verify_your_email);
            }else {
                Constraint.setToast(SignUpActivity.this,Constraint.verify_email_not_sent);
            }
        }).addOnFailureListener(e -> Constraint.setToast(SignUpActivity.this,e.getMessage()));
    }



}