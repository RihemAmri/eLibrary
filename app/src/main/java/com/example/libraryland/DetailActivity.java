package com.example.libraryland;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {
    TextView detailDesc, detailTitle, detailLang,detailAuthor;
    ImageView detailImage;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        detailLang = findViewById(R.id.detailLang);
        detailAuthor = findViewById(R.id.detailAuthor);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailLang.setText(bundle.getString("Genre"));
            detailAuthor.setText(bundle.getString("Author"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Référence à Firebase Database
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("books");

                // Supprime le livre (y compris l'URL de l'image stockée)
                reference.child(key).removeValue()
                        .addOnSuccessListener(unused -> {
                            // Met à jour la date de suppression dans "RecentDates"
                            DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("RecentDates");
                            datesRef.child("lastDeleteDate").setValue(System.currentTimeMillis())
                                    .addOnSuccessListener(updateTask -> Log.d("DeleteBook", "lastDeleteDate updated successfully"))
                                    .addOnFailureListener(error -> Log.e("DeleteBook", "Failed to update lastDeleteDate: " + error.getMessage()));
                            Toast.makeText(DetailActivity.this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), BooksListActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DetailActivity.this, "Failed to delete book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
        Log.d("DetailActivity", "bookKey: " + key);
        Log.d("DetailActivity", "Year to pass: " + bundle.getInt("Year"));

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                        .putExtra("Title", detailTitle.getText().toString())
                        .putExtra("Description", detailDesc.getText().toString())
                        .putExtra("Genre", detailLang.getText().toString())
                        .putExtra("Author", detailAuthor.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key)
                        .putExtra("Year", bundle.getInt("Year")); ;
                startActivity(intent);
            }
        });





    }
    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les données à partir de la base de données Firebase
        loadBookDetails();
    }

    private void loadBookDetails() {
        // Accéder aux extras passés depuis l'activité précédente
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // Récupérer la clé du livre
            String key = bundle.getString("Key");

            // Accéder à la base de données Firebase pour récupérer les données les plus récentes
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("books").child(key);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Récupérer les nouvelles données du livre
                    Book updatedBook = snapshot.getValue(Book.class);
                    if (updatedBook != null) {
                        // Mettre à jour l'interface avec les nouvelles données
                        detailDesc.setText(updatedBook.getDescription());
                        detailTitle.setText(updatedBook.getTitle());
                        detailLang.setText(updatedBook.getGenre());
                        detailAuthor.setText(updatedBook.getAuthor());
                        Glide.with(DetailActivity.this).load(updatedBook.getDataImage()).into(detailImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DetailActivity.this, "Failed to load book details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}