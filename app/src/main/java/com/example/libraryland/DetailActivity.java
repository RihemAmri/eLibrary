package com.example.libraryland;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
                            Toast.makeText(DetailActivity.this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), BooksListActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(DetailActivity.this, "Failed to delete book: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });


    }
    }