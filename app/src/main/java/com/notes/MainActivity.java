package com.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.notes.auth.SignInActivity;
import com.notes.models.FirebaseModel;
import com.notes.utils.Constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirestoreRecyclerAdapter<FirebaseModel, NotesViewHolder> adapter;
    private Toolbar toolbar;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        auth=FirebaseAuth.getInstance();
        FloatingActionButton fabCreateNotes = findViewById(R.id.fabNotesCreate);
        RecyclerView recyclerViewNotes = findViewById(R.id.recyclerViewNotes);



        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        fabCreateNotes.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), CreateNotesActivity.class));
        });

        assert user != null;
        Query query = firebaseFirestore
                .collection("notes")
                .document(user.getUid())
                .collection("myNotes")
                .orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<FirebaseModel> allUserNotes = new FirestoreRecyclerOptions.Builder<FirebaseModel>()
                .setQuery(query, FirebaseModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<FirebaseModel, NotesViewHolder>(allUserNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull FirebaseModel model) {

                ImageView popupImg = holder.itemView.findViewById(R.id.imgMenu);

                holder.itemView.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),CreateNotesActivity.class)
                        .putExtra("title",holder.titleNote.getText())
                        .putExtra("content",holder.contentNote.getText())
                        .putExtra("noteId",adapter.getSnapshots().getSnapshot(position).getId())));

                popupImg.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(),popupImg);
                    popupMenu.setGravity(Gravity.END);
                    popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(item -> {
                        startActivity(new Intent(getApplicationContext(),CreateNotesActivity.class)
                                .putExtra("title",holder.titleNote.getText())
                                .putExtra("content",holder.contentNote.getText())
                                .putExtra("noteId",adapter.getSnapshots().getSnapshot(position).getId()));
                        return false;
                    });
                    popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(item -> {
                        DocumentReference reference = firebaseFirestore
                                .collection("notes")
                                .document(user.getUid())
                                .collection("myNotes")
                                .document(adapter.getSnapshots().getSnapshot(position).getId());

                        reference.delete().addOnSuccessListener(unused -> {
                            Constraint.setToast(v.getContext(), "Delete successfully");
                            adapter.notifyDataSetChanged();
                        }).addOnFailureListener(e -> Constraint.setToast(v.getContext(),"Delete failed "+ e.getMessage()));

                        return false;
                    });
                    popupMenu.show();
                });

                int colorCode = getRandomColor();
                holder.note.setBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));

                holder.titleNote.setText(model.getTitle());
                holder.contentNote.setText(model.getContent());
            }

            @NonNull
            @Override
            public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new NotesViewHolder(LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.created_notes_layout, parent, false));
            }
        };

        recyclerViewNotes.setHasFixedSize(true);
        recyclerViewNotes.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerViewNotes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private int getRandomColor() {
        List<Integer> colorcode = new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.greens);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);
        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        Random random = new Random();
        int number = random.nextInt(colorcode.size());

        return colorcode.get(number);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null){
            adapter.startListening();
        }
    }

    public static class NotesViewHolder extends RecyclerView.ViewHolder{
        private TextView titleNote;
        private TextView contentNote;
        private ConstraintLayout note;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleNote = itemView.findViewById(R.id.txtTitle);
            contentNote = itemView.findViewById(R.id.txtContent);
            note = itemView.findViewById(R.id.note);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menus,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logout){
            auth.signOut();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}