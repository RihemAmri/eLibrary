package com.example.libraryland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        CardView borrowsCard = findViewById(R.id.borrow_card);
        Button logoutButton = findViewById(R.id.logoutButton); // Bouton logout

        // Ajouter un clic listener
        booksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer BooksListActivity
                Intent intent = new Intent(AdminActivity.this, BooksListActivity.class);
                startActivity(intent);
            }
        });


        borrowsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer BooksListActivity
                Intent intent = new Intent(AdminActivity.this, HistoriqueActivity.class);
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
        // Ajouter un clic listener pour le bouton logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(); // Appeler la méthode de déconnexion
            }
        });
    }
    // Méthode pour gérer la déconnexion
    private void logout() {
        // Supprimer la session utilisateur
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Supprime toutes les données de la session
        editor.apply();

        // Rediriger vers l'écran de login
        Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Effacer l'historique des activités
        startActivity(intent);
        finish(); // Fermer l'activité actuelle
    }
}
