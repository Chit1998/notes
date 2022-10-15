package com.notes.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.notes.R;
import com.notes.utils.Constraint;

public class ForgotActivity extends AppCompatActivity {

    private EditText eEmailAddress;
    private TextView btnForgot;
    private TextView txtSignIn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.purple_500));
        setContentView(R.layout.activity_forgot);

        eEmailAddress = findViewById(R.id.eEmailAddress);
        btnForgot = findViewById(R.id.btnForgot);
        txtSignIn = findViewById(R.id.txtSignIn);
        auth = FirebaseAuth.getInstance();
        validation();

        btnForgot.setOnClickListener(v -> {
            if (eEmailAddress.getText().toString().isEmpty()){
                eEmailAddress.setBackgroundResource(R.drawable.edit_error_drawable);
                eEmailAddress.setError(Constraint.empty);
            }else {
                forgot(eEmailAddress.getText().toString().trim());
            }
        });

        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        });
    }

    private void validation() {

        eEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!eEmailAddress.getText().toString().matches(Constraint.emailPattern)){
                    eEmailAddress.setError(Constraint.email_valid);
                    eEmailAddress.setBackgroundResource(R.drawable.edit_error_drawable);
                    btnForgot.setEnabled(false);
                }else {
                    eEmailAddress.setBackgroundResource(R.drawable.edit_drawable_layout);
                    btnForgot.setEnabled(true);
                }
            }
        });
    }

    private void forgot(String email) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Constraint.setToast(ForgotActivity.this,Constraint.reset_mail);
                    }else {
                        Constraint.setToast(ForgotActivity.this,Constraint.reset_mail_not);
                    }
                }).addOnFailureListener(e -> Constraint.setToast(ForgotActivity.this,e.getMessage()));
    }
}