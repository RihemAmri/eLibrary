package com.example.libraryland;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BorrowBookActivity extends AppCompatActivity {

    private EditText userNameEditText, requestDateEditText;
    private Button borrowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_book);

        // Initialiser les vues
        userNameEditText = findViewById(R.id.userName);
        requestDateEditText = findViewById(R.id.requestDate);
        borrowButton = findViewById(R.id.borrowButton);

        // Récupérer le nom d'utilisateur à partir de la session
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = sharedPreferences.getString("username", ""); // Récupérer le nom d'utilisateur
        userNameEditText.setText(userName); // Remplir le champ User Name

        // Récupérer la date actuelle et la formater
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date()); // Obtenir la date actuelle
        requestDateEditText.setText(currentDate); // Remplir le champ Request Date
    }
}
