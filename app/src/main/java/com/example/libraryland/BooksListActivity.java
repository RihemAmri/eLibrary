package com.example.libraryland;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BooksListActivity extends AppCompatActivity {
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);

        // Trouver le bouton d'ajout de livre
        fab = findViewById(R.id.fab);

        // Ajouter un clic listener pour ouvrir le formulaire d'ajout de livre
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer AddBookActivity
                Intent intent = new Intent(BooksListActivity.this, AddBookActivity.class);
                startActivity(intent);
                Log.d("BooksListActivity", "FAB clicked");

            }
        });
    }
}
