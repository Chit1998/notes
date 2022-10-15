package com.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.notes.utils.Constraint;

import java.util.HashMap;
import java.util.Map;

public class CreateNotesActivity extends AppCompatActivity {

    private EditText eTitle, eNotes;
    private FloatingActionButton fabSave;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private String title, content, noteId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notes);

        fabSave = findViewById(R.id.fabSave);
        eTitle = findViewById(R.id.eTitle);
        eNotes = findViewById(R.id.eNotes);
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        Log.d("TAG", "onCreate "+new Exception().getMessage());

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        noteId = getIntent().getStringExtra("noteId");
        if (title != null && content != null && noteId != null){
            eTitle.setText(title);
            eNotes.setText(content);
            fabSave.setOnClickListener(v -> {
                String title = eTitle.getText().toString();
                String content = eNotes.getText().toString();
                Constraint.setToast(v.getContext(),"Update");

                if (title.isEmpty() || content.isEmpty()){
                    Constraint.setToast(CreateNotesActivity.this, "Both field are require.");
                }else {
                    DocumentReference reference = firebaseFirestore.collection("notes")
                            .document(user.getUid())
                            .collection("myNotes").document(noteId);

                    Map<String,Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);

                    reference.set(note).addOnSuccessListener(unused -> {
                        Constraint.setToast(CreateNotesActivity.this, "Note updated successfully..!");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }).addOnFailureListener(e -> Constraint.setToast(CreateNotesActivity.this, e.getMessage()));

                }

            });
        }else {
            fabSave.setOnClickListener(v -> {
                String title = eTitle.getText().toString();
                String content = eNotes.getText().toString();


                if (title.isEmpty() || content.isEmpty()){
                    Constraint.setToast(CreateNotesActivity.this, "Both field are require.");
                }else {
                    DocumentReference reference = firebaseFirestore.collection("notes")
                            .document(user.getUid())
                            .collection("myNotes").document();

                    Map<String,Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);

                    reference.set(note).addOnSuccessListener(unused -> {
                        Constraint.setToast(CreateNotesActivity.this, "Note created successfully..!");
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }).addOnFailureListener(e -> Constraint.setToast(CreateNotesActivity.this, e.getMessage()));

                }
            });
        }




    }
}