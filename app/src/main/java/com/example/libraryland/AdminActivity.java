package com.example.libraryland;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        TextView adminMessage = findViewById(R.id.admin_message);
        adminMessage.setText("Bienvenue dans le panneau d'administration !");
    }
}
