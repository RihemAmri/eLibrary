package com.example.libraryland;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.libraryland.Book;
import com.example.libraryland.R;
import com.github.clans.fab.FloatingActionButton;


public class ViewDetails extends AppCompatActivity {

    private ImageView imageViewBookCover;
    private TextView textViewTitle, textViewAuthor, textViewYear, textViewAvailability,genre,description;
    private Button buttonBorrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);






        // Button to navigate to Historique
        FloatingActionButton historiqueButton = findViewById(R.id.historique);
        historiqueButton.setOnClickListener(v -> {
            Intent historiqueIntent = new Intent(ViewDetails.this, Hist_BorrowActivity.class);
            startActivity(historiqueIntent);
        });

        // Button to log out and navigate to LoginActivity
        FloatingActionButton logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> {
            // Here you can perform logout logic if necessary

            // Redirect to LoginActivity
            Intent logoutIntent = new Intent(ViewDetails.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish(); // Close the current activity
        });
        FloatingActionButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(v -> {
            // Here you can perform logout logic if necessary

            // Redirect to LoginActivity
            Intent logoutIntent = new Intent(ViewDetails.this, MainActivity.class);
            startActivity(logoutIntent);
            finish(); // Close the current activity
        });








        // Récupérer les vues
        imageViewBookCover = findViewById(R.id.imageViewBookCover);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        textViewYear = findViewById(R.id.textViewYear);
        textViewAvailability = findViewById(R.id.textViewAvailability);
        buttonBorrow = findViewById(R.id.buttonBorrow);
        genre=findViewById(R.id.genre);
        description=findViewById(R.id.description);


        // Récupérer l'objet Book depuis l'Intent
        Book book = (Book) getIntent().getSerializableExtra("BOOK_DETAILS");

        if (book != null) {
            // Mettre à jour les données dans les vues
            textViewTitle.setText(book.getTitle());
            textViewAuthor.setText("Author: " + book.getAuthor());
            textViewYear.setText("Year: " + book.getYear());
            description.setText("Description: "+book.getDescription());
            genre.setText("Genre: "+book.getGenre());
            textViewAvailability.setText(book.isAvailability() ? "Available" : "Not Available");
            Glide.with(this)
                    .load(book.getDataImage())  // URL de l'image
                    .placeholder(R.drawable.placeholder_image)  // Image de remplacement en attendant que l'image soit chargée
                      // Image d'erreur en cas de problème de chargement
                    .into(imageViewBookCover);  // ImageView où l'image sera affichée

            // Mettre à jour l'état du bouton selon la disponibilité
            if (book.isAvailability()) {
                buttonBorrow.setEnabled(true);
                buttonBorrow.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_green_light));
            } else {
                buttonBorrow.setEnabled(false);
                buttonBorrow.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            }

            // Ajouter un listener sur le bouton Borrow
            buttonBorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ViewDetails.this, BorrowBookActivity.class);
                    intent.putExtra("book", book); // Envoyer l'objet Book
                    startActivity(intent);
                }
            });
        } else {
            // Gestion de l'absence de livre
            textViewTitle.setText("Book details not available");
            textViewAuthor.setText("");
            textViewYear.setText("");
            textViewAvailability.setText("");
            buttonBorrow.setEnabled(false);
        }
    }
}
