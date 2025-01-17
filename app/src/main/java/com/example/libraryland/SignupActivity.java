package com.example.libraryland;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignupActivity extends AppCompatActivity {
    EditText signupName, signupEmail, signupUsername, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = signupName.getText().toString();
                String email = signupEmail.getText().toString();
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();

                if (validateInput(name, email, username, password)) {
                    checkUsernameExistsAndRegister(name, email, username, hashPassword(password));
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateInput(String name, String email, String username, String password) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Tous les champs doivent être remplis.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.length() < 5) {
            Toast.makeText(this, "Le nom d'utilisateur doit contenir au moins 5 caractères.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Veuillez saisir une adresse e-mail valide.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkUsernameExistsAndRegister(String name, String email, String username, String hashedPassword) {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        // Vérifie si le nom d'utilisateur existe déjà dans la base de données
        reference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(SignupActivity.this, "Le nom d'utilisateur existe déjà. Veuillez en choisir un autre.", Toast.LENGTH_SHORT).show();
                } else {
                    // Enregistre l'utilisateur si le nom d'utilisateur est disponible
                    HelperClass helperClass = new HelperClass(name, email, username, hashedPassword, "user"); // Rôle par défaut "user"
                    reference.child(username).setValue(helperClass);
                    Toast.makeText(SignupActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

                    // Redirige vers l'écran de connexion
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignupActivity.this, "Erreur lors de la vérification du nom d'utilisateur.", Toast.LENGTH_SHORT).show();
                Log.e("SignupActivity", "Database error: " + error.getMessage());
            }
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
