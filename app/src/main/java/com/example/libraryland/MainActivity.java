package com.example.libraryland;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.libraryland.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapterbookuser myAdapter;
    ArrayList<Book> list;
    ArrayList<Book> fullList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Button to navigate to Historique
        FloatingActionButton historiqueButton = findViewById(R.id.historique);
        historiqueButton.setOnClickListener(v -> {
            Intent historiqueIntent = new Intent(MainActivity.this, Hist_BorrowActivity.class);
            startActivity(historiqueIntent);
        });

        // Button to log out and navigate to LoginActivity
        FloatingActionButton logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> {
            // Here you can perform logout logic if necessary

            // Redirect to LoginActivity
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish(); // Close the current activity
        });






        findViewById(R.id.category_romance).setOnClickListener(v -> filterByGenre("Romance"));
        findViewById(R.id.category_scifi).setOnClickListener(v-> filterByGenre("Science Fiction"));
        findViewById(R.id.category_fantasy).setOnClickListener(v -> filterByGenre("Fantasy"));
        findViewById(R.id.category_horror).setOnClickListener(v -> filterByGenre("Horror"));
        findViewById(R.id.category_adventure).setOnClickListener(v -> filterByGenre("Adventure"));
        findViewById(R.id.category_comedy).setOnClickListener(v -> filterByGenre("Comedy"));
        findViewById(R.id.search_container).setOnClickListener(v -> {
            myAdapter.list = new ArrayList<>(fullList); // Rétablir la liste complète
            myAdapter.notifyDataSetChanged();
        });
        Log.d("hellooo", "hani fil main ");
        recyclerView = findViewById(R.id.booklist);
        database= FirebaseDatabase.getInstance().getReference("books");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list =new ArrayList<>();
        myAdapter=new MyAdapterbookuser(this,list);
        recyclerView.setAdapter(myAdapter);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Book book =dataSnapshot.getValue(Book.class);
                    Log.d("TAG", "Message à afficher");

                    if (book != null) {
                        // Définit l'ID dans l'objet Book
                        list.add(book);
                    }
                    fullList = new ArrayList<>(list);
                }
                for (Book book : list) {
                    Log.d("BookInList", "Book: " + book.getTitle() + ", Author: " + book.getAuthor());
                }
                Log.d("RecyclerViewUpdate", "Calling notifyDataSetChanged()");
                myAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        EditText searchBar = findViewById(R.id.search_bar);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Rien à faire ici
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Rien à faire ici
            }
        });





    }
    private void filterList(String query) {
        ArrayList<Book> filteredList = new ArrayList<>();
        for (Book book : fullList) { // Utiliser fullList pour filtrer
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                    book.getGenre().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }
        myAdapter.list = filteredList; // Mettre à jour la liste dans l'adaptateur
        myAdapter.notifyDataSetChanged(); // Notifier l'adaptateur pour rafraîchir
    }
    private void filterByGenre(String genre) {
        ArrayList<Book> filteredList = new ArrayList<>();
        for (Book book : fullList) {
            if (book.getGenre().equalsIgnoreCase(genre)) {
                filteredList.add(book);
            }
        }
        myAdapter.list = filteredList; // Mettre à jour la liste dans l'adaptateur
        myAdapter.notifyDataSetChanged(); // Rafraîchir l'adaptateur
    }



}