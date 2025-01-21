package com.example.libraryland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.cardview.widget.CardView;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Trouver la CardView pour les livres
        CardView booksCard = findViewById(R.id.books_card);


        // Ajouter un clic listener
        booksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer BooksListActivity
                Intent intent = new Intent(AdminActivity.this, BooksListActivity.class);
                startActivity(intent);
            }
        });
        CardView usersCard = findViewById(R.id.users);

        // Ajouter un clic listener pour les utilisateurs
        usersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer UsersListActivity
                Intent intent = new Intent(AdminActivity.this, UsersListActivity.class);
                startActivity(intent);
            }
        });
    }
}
