package com.example.libraryland;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BooksListActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    private List<Book> dataList = new ArrayList<>();

    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);
        recyclerView = findViewById(R.id.recyclerView);
        // Trouver le bouton d'ajout de livre
        fab = findViewById(R.id.fab);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(BooksListActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(BooksListActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();


        MyAdapter adapter= new MyAdapter(BooksListActivity.this, dataList);
        recyclerView.setAdapter(adapter);



        databaseReference = FirebaseDatabase.getInstance().getReference("books");
        //dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    Book dataClass = itemSnapshot.getValue(Book.class);
                    //dataClass.setKey(itemSnapshot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

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
