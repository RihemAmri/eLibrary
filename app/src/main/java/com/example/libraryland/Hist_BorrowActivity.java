package com.example.libraryland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Hist_BorrowActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference database, borrowDatabase;
    MyBorrowAdapter myAdapter;
    ArrayList<Borrow> borrowList;
    ArrayList<Book> fullBookList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hist_borrow);



        // Button to log out and navigate to LoginActivity
        FloatingActionButton logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> {
            // Here you can perform logout logic if necessary

            // Redirect to LoginActivity
            Intent logoutIntent = new Intent(Hist_BorrowActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish(); // Close the current activity
        });
        FloatingActionButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(v -> {
            // Here you can perform logout logic if necessary

            // Redirect to LoginActivity
            Intent logoutIntent = new Intent(Hist_BorrowActivity.this, MainActivity.class);
            startActivity(logoutIntent);
            finish(); // Close the current activity
        });




        findViewById(R.id.category_romance).setOnClickListener(v -> filterByGenre("Romance"));
        findViewById(R.id.category_scifi).setOnClickListener(v -> filterByGenre("Science Fiction"));
        findViewById(R.id.category_fantasy).setOnClickListener(v -> filterByGenre("Fantasy"));
        findViewById(R.id.category_horror).setOnClickListener(v -> filterByGenre("Horror"));
        findViewById(R.id.category_adventure).setOnClickListener(v -> filterByGenre("Adventure"));
        findViewById(R.id.category_comedy).setOnClickListener(v -> filterByGenre("Comedy"));
        findViewById(R.id.search_container).setOnClickListener(v -> {
            myAdapter.borrowList = new ArrayList<>(borrowList); // Réinitialiser la liste complète
            myAdapter.notifyDataSetChanged();
        });

        recyclerView = findViewById(R.id.booklist);
        database = FirebaseDatabase.getInstance().getReference("books");
        borrowDatabase = FirebaseDatabase.getInstance().getReference("borrow");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        borrowList = new ArrayList<>();
        fullBookList = new ArrayList<>();
        myAdapter = new MyBorrowAdapter(this, borrowList);
        recyclerView.setAdapter(myAdapter);

        // Récupérer les emprunts de l'utilisateur
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", ""); // Utilise la session de l'utilisateur ici
        borrowDatabase.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                borrowList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Borrow borrow = dataSnapshot.getValue(Borrow.class);
                    if (borrow != null) {
                        borrowList.add(borrow);
                    }
                }
                myAdapter.notifyDataSetChanged();  // Mettre à jour l'adaptateur avec les emprunts
                loadBooks();  // Charger les livres après les emprunts
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors de la récupération des emprunts", error.toException());
            }
        });

        // Filtrer les livres par titre
        EditText searchBar = findViewById(R.id.search_bar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Charger les livres à partir de Firebase
    private void loadBooks() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullBookList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Book book = dataSnapshot.getValue(Book.class);
                    if (book != null) {
                        fullBookList.add(book);
                    }
                }
                // Appeler à nouveau l'adaptateur pour l'affichage
                //myAdapter.updateBooks(fullBookList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors du chargement des livres", error.toException());
            }
        });
    }

    private void filterList(String query) {
        ArrayList<Borrow> filteredList = new ArrayList<>();
        for (Borrow borrow : borrowList) {
            if (borrow.getBookname().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(borrow);
            }
        }
        myAdapter.borrowList = filteredList;
        myAdapter.notifyDataSetChanged();
    }

    private void filterByGenre(String genre) {
        ArrayList<Borrow> filteredList = new ArrayList<>();
        for (Borrow borrow : borrowList) {
            for (Book book : fullBookList) {
                if (book.getGenre().equalsIgnoreCase(genre) && borrow.getBookname().equals(book.getTitle())) {
                    filteredList.add(borrow);
                    break;
                }
            }
        }
        myAdapter.borrowList = filteredList;
        myAdapter.notifyDataSetChanged();
    }
}
