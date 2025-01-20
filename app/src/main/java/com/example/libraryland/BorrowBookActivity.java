package com.example.libraryland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BorrowBookActivity extends AppCompatActivity {

    private EditText userNameEditText, requestDateEditText, bookNameEditText;
    private Button borrowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_book);

        // Initialiser les vues
        userNameEditText = findViewById(R.id.userName);
        requestDateEditText = findViewById(R.id.requestDate);
        bookNameEditText = findViewById(R.id.bookName);
        borrowButton = findViewById(R.id.borrowButton);

        // Récupérer le nom d'utilisateur à partir de la session
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = sharedPreferences.getString("username", "");
        userNameEditText.setText(userName);

        // Récupérer la date actuelle et la formater
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        requestDateEditText.setText(currentDate);

        // Définir un listener pour le bouton Borrow
        borrowButton.setOnClickListener(view -> checkBookAvailabilityAndBorrow());
    }

    private void checkBookAvailabilityAndBorrow() {
        String bookName = bookNameEditText.getText().toString();

        // Référence au nœud des livres
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("books");

        // Rechercher le livre par son titre
        booksRef.orderByChild("title").equalTo(bookName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Le livre existe
                    for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                        // Récupérer la disponibilité
                        boolean availability = bookSnapshot.child("availability").getValue(Boolean.class);

                        if (availability) {
                            // Le livre est disponible, procéder à l'emprunt
                            addBorrowData(bookSnapshot.getKey());
                        } else {
                            // Le livre est déjà emprunté
                            Toast.makeText(BorrowBookActivity.this, "This book is already borrowed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Le livre n'existe pas dans la base de données
                    Toast.makeText(BorrowBookActivity.this, "Book not found in the library.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Erreur lors de la recherche du livre", databaseError.toException());
                Toast.makeText(BorrowBookActivity.this, "Error: Unable to check book availability.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addBorrowData(String bookId) {
        // Connexion à la base de données Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference borrowRef = database.getReference("borrow");

        // Récupérer les valeurs des champs
        String username = userNameEditText.getText().toString();
        String bookname = bookNameEditText.getText().toString();
        String requestDate = requestDateEditText.getText().toString();

        // Calculer la date de retour
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        try {
            Date requestDateObj = dateFormat.parse(requestDate);
            if (requestDateObj != null) {
                calendar.setTime(requestDateObj);
                calendar.add(Calendar.DAY_OF_YEAR, 15);
                Date returnDateObj = calendar.getTime();
                String returnDate = dateFormat.format(returnDateObj);

                // Créer un objet Borrow avec les données
                Borrow borrow = new Borrow(username, bookname, "pending", requestDate, returnDate);

                // Ajouter un nouvel emprunt
                String borrowId = borrowRef.push().getKey();
                borrowRef.child(borrowId).setValue(borrow)
                        .addOnSuccessListener(aVoid -> {
                            // Mettre à jour la disponibilité du livre à false
                            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books").child(bookId);
                            bookRef.child("availability").setValue(false);

                            // Afficher un message de succès
                            Toast.makeText(this, "Book borrowed", Toast.LENGTH_SHORT).show();

                            // Rediriger vers MainActivity
                            Intent intent = new Intent(BorrowBookActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Afficher un message d'erreur
                            Toast.makeText(this, "Error: Unable to borrow book", Toast.LENGTH_SHORT).show();
                            Log.w("Firebase", "Erreur lors de l'ajout de l'emprunt", e);
                        });
            }
        } catch (Exception e) {
            Log.e("Date Error", "Erreur lors du calcul de la date de retour", e);
        }
    }
}
