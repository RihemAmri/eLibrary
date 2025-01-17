package com.example.libraryland;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddBookActivity extends AppCompatActivity {
    private static final String TAG = "AddBookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d(TAG, "AddBook created hmd");
        checkAndAddBooks();
    }

    // Vérification avant d'ajouter les livres
    private void checkAndAddBooks() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference booksRef = database.getReference("books");

        Log.d(TAG, "Checking if books exist in the database...");
        booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    Log.d(TAG, "No books found in the database. Adding default books...");
                    addBooksToFirebase();
                } else {
                    Log.d(TAG, "Books already exist in the database. Total books: " + snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Error checking books in database: " + error.getMessage());
            }
        });
    }

    private void addBooksToFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference booksRef = database.getReference("books");

        // Exemple de livres
        Book book1 = new Book("1984", "George Orwell", "Fiction", 1949, true, "2025-01-01 12:00", "2025-01-01 12:00");
        Book book2 = new Book("To Kill a Mockingbird", "Harper Lee", "Classic", 1960, true, "2025-01-01 12:00", "2025-01-01 12:00");
        Book book3 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "Fiction", 1925, true, "2025-01-01 12:00", "2025-01-01 12:00");

        Log.d(TAG, "Adding books to Firebase...");

        booksRef.push().setValue(book1).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Book 1 added successfully: " + book1.getTitle());
            } else {
                Log.d(TAG, "Failed to add Book 1: " + task.getException().getMessage());
            }
        });

        booksRef.push().setValue(book2).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Book 2 added successfully: " + book2.getTitle());
            } else {
                Log.d(TAG, "Failed to add Book 2: " + task.getException().getMessage());
            }
        });

        booksRef.push().setValue(book3).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Book 3 added successfully: " + book3.getTitle());
            } else {
                Log.d(TAG, "Failed to add Book 3: " + task.getException().getMessage());
            }
        });
    }



}