package com.example.libraryland;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersListActivity extends AppCompatActivity {
    private ListView listViewUsers;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);




        // Button to log out and navigate to LoginActivity
        FloatingActionButton logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> {
            // Here you can perform logout logic if necessary

            // Redirect to LoginActivity
            Intent logoutIntent = new Intent(UsersListActivity.this, LoginActivity.class);
            startActivity(logoutIntent);
            finish(); // Close the current activity
        });
        FloatingActionButton homeButton = findViewById(R.id.home);
        homeButton.setOnClickListener(v -> {
            // Here you can perform logout logic if necessary

            // Redirect to LoginActivity
            Intent logoutIntent = new Intent(UsersListActivity.this, AdminActivity.class);
            startActivity(logoutIntent);
            finish(); // Close the current activity
        });





        listViewUsers = findViewById(R.id.listViewUsers);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Charger les utilisateurs depuis Firebase
        loadUsers();
    }

    private void loadUsers() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Map<String, String>> userList = new ArrayList<>();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    User user = itemSnapshot.getValue(User.class);
                    if (user != null) {
                        // Ajouter les données dans une Map pour le SimpleAdapter
                        Map<String, String> userData = new HashMap<>();
                        userData.put("name", user.getName());
                        userData.put("email", user.getEmail());
                        userData.put("role", user.getRole());
                        userData.put("username", user.getUsername());
                        userList.add(userData);
                    }
                }

                // Configurer le SimpleAdapter
                SimpleAdapter adapter = new SimpleAdapter(
                        UsersListActivity.this,
                        userList,
                        R.layout.user_list_item, // Utiliser le layout personnalisé
                        new String[]{"name", "email", "role", "username"},
                        new int[]{R.id.textName, R.id.textEmail, R.id.textRole, R.id.textUsername}
                );

                listViewUsers.setAdapter(adapter);
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(UsersListActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
