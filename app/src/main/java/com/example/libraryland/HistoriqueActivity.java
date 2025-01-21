package com.example.libraryland;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoriqueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyResAdapter myAdapter;
    private DatabaseReference borrowDatabase, booksDatabase;
    private List<Borrow> borrowList;
    private Map<String, String> bookImageMap; // Associer les noms des livres à leurs URL d'images

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        borrowList = new ArrayList<>();
        bookImageMap = new HashMap<>();
        myAdapter = new MyResAdapter(this, borrowList, bookImageMap);
        recyclerView.setAdapter(myAdapter);

        borrowDatabase = FirebaseDatabase.getInstance().getReference("borrow");
        booksDatabase = FirebaseDatabase.getInstance().getReference("books");

        loadBorrowData();
    }

    private void loadBorrowData() {
        // Afficher une boîte de dialogue de chargement
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Charger tous les emprunts
        borrowDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                borrowList.clear();
                for (DataSnapshot borrowItem : snapshot.getChildren()) {
                    Borrow borrow = borrowItem.getValue(Borrow.class);
                    if (borrow != null) {
                        borrowList.add(borrow);
                    }
                }
                // Charger les images des livres après avoir chargé les emprunts
                loadBookImages(dialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors de la récupération des emprunts", error.toException());
                dialog.dismiss();
            }
        });
    }

    private void loadBookImages(AlertDialog dialog) {
        // Charger toutes les images des livres
        booksDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookImageMap.clear();
                for (DataSnapshot bookItem : snapshot.getChildren()) {
                    Book book = bookItem.getValue(Book.class);
                    if (book != null && book.getDataImage() != null) {
                        bookImageMap.put(book.getTitle(), book.getDataImage());
                    }
                }
                myAdapter.notifyDataSetChanged(); // Mettre à jour l'adaptateur
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors de la récupération des livres", error.toException());
                dialog.dismiss();
            }
        });
    }
}
