// ViewDetailsActivity.java
package com.example.libraryland;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewDetails extends AppCompatActivity {

    private TextView textViewTitle, textViewAuthor, textViewDescription, textViewGenre, textViewYear, textViewAvailability;
    private Button buttonBorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        // Récupérer les vues
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewGenre = findViewById(R.id.textViewGenre);
        textViewYear = findViewById(R.id.textViewYear);
        textViewAvailability = findViewById(R.id.textViewAvailability);
        buttonBorrow = findViewById(R.id.buttonBorrow);

        // Récupérer l'objet Book passé via l'intent
        Book book = (Book) getIntent().getSerializableExtra("BOOK_DETAILS");

        if (book != null) {
            textViewTitle.setText("Title: " + book.getTitle());
            textViewAuthor.setText("Author: " + book.getAuthor());
            textViewDescription.setText("Description: " + book.getDescription());
            textViewGenre.setText("Genre: " + book.getGenre());
            textViewYear.setText("Year: " + book.getYear());
            textViewAvailability.setText("Availability: " + (book.isAvailability() ? "Available" : "Not Available"));
        }

        // Action pour le bouton "Borrow"
        buttonBorrow.setOnClickListener(v -> {
            borrowBook(book);
        });
    }

    private void borrowBook(Book book) {
        // Implémentation de la logique de prêt (mettre à jour la base de données ou le stock)
        // Exemple ici de modification fictive du statut de disponibilité
        textViewAvailability.setText("Status: Borrowed");
    }
}
