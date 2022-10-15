package com.notes.auth;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.notes.MainActivity;
import com.notes.R;
import com.notes.utils.Constraint;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private EditText eEmailAddress;
    private EditText ePassword;
    private TextView btnSignIn;
    private TextView txtForgotPassword;
    private TextView txtSignUp;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.purple_500));
        setContentView(R.layout.activity_sign_in);
        init();
        validation();
        auth = FirebaseAuth.getInstance();

        btnSignIn.setOnClickListener(v -> {
            if (eEmailAddress.getText().toString().isEmpty()){
                eEmailAddress.setError(Constraint.empty);
                eEmailAddress.setBackgroundResource(R.drawable.edit_error_drawable);
            }else if (ePassword.getText().toString().isEmpty()){
                ePassword.setError(Constraint.empty);
                ePassword.setBackgroundResource(R.drawable.edit_error_drawable);
            }else {
                signIn(eEmailAddress.getText().toString().trim(), ePassword.getText().toString().trim());
            }
        });

        txtForgotPassword.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ForgotActivity.class)));

        txtSignUp.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
    }
    private void validation(){
            eEmailAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().matches(Constraint.emailPattern)){
                        eEmailAddress.setError(Constraint.email_valid);
                        eEmailAddress.setBackgroundResource(R.drawable.edit_error_drawable);
                        btnSignIn.setEnabled(false);
                    }else {
                        eEmailAddress.setBackgroundResource(R.drawable.edit_drawable_layout);
                        btnSignIn.setEnabled(true);
                    }
                }
            });
            ePassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!(s.toString().length() > 7)){
                        ePassword.setError(Constraint.password_length);
                        ePassword.setBackgroundResource(R.drawable.edit_error_drawable);
                        btnSignIn.setEnabled(false);
                    }else {
                        ePassword.setBackgroundResource(R.drawable.edit_drawable_layout);
                        btnSignIn.setEnabled(true);
                    }
                }
            });
    }

    private void init(){
        eEmailAddress =findViewById(R.id.eEmailAddress);
        ePassword =findViewById(R.id.ePassword);
        btnSignIn =findViewById(R.id.btnSignIn);
        txtForgotPassword =findViewById(R.id.txtForgotPassword);
        txtSignUp =findViewById(R.id.txtSignUp);
    }

    private void signIn(String email, String password){
        if (email.isEmpty() || password.isEmpty()){
            Constraint.setToast(SignInActivity.this, Constraint.empty);
        }else if (!email.matches(Constraint.emailPattern)){
            eEmailAddress.setError(Constraint.email_valid);
        }else {
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            isEmailVerified();
                        }else {
                            Constraint.setToast(SignInActivity.this,Constraint.check_credential);
                        }
                    }).addOnFailureListener(e -> Constraint.setToast(SignInActivity.this, e.getMessage()));
        }

    }

    private void isEmailVerified() {
        FirebaseUser user = auth.getCurrentUser();
        if (Objects.requireNonNull(user).isEmailVerified()){
            Constraint.setToast(SignInActivity.this,Constraint.sign_in_success);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else {
            Constraint.setToast(SignInActivity.this,Constraint.not_verified);
        }
    }
}
