package com.example.libraryland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText loginUsername, loginPassword;
    private Button loginButton;
    private TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialisation des vues
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginButton = findViewById(R.id.login_button);

        // Gestion du clic sur le bouton de connexion
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() || !validatePassword()) {
                    return; // Validation échouée, ne pas continuer
                } else {
                    checkUser(); // Vérifier l'utilisateur
                }
            }
        });

        // Redirection vers la page d'inscription
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    // Méthode pour valider le champ du nom d'utilisateur
    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Le nom d'utilisateur ne peut pas être vide");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    // Méthode pour valider le champ du mot de passe
    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Le mot de passe ne peut pas être vide");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    // Vérifier si l'utilisateur existe dans la base de données
    public void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginUsername.setError(null);

                    // Récupérer les données utilisateur
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);
                    String roleFromDB = snapshot.child(userUsername).child("role").getValue(String.class);

                    // Vérifier le mot de passe (hashé)
                    if (Objects.equals(passwordFromDB, hashPassword(userPassword))) {
                        loginUsername.setError(null);

                        // Créer une session utilisateur
                        createSession(userUsername, roleFromDB);

                        // Redirection selon le rôle
                        if ("admin".equals(roleFromDB)) {
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
                        } else if ("user".equals(roleFromDB)) {
                            Intent intent = new Intent(LoginActivity.this, Hist_BorrowActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        loginPassword.setError("Identifiants invalides");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginUsername.setError("L'utilisateur n'existe pas");
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // Méthode pour créer une session utilisateur
    private void createSession(String username, String role) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("username", username); // Stocker le nom d'utilisateur
        editor.putString("role", role);         // Stocker le rôle de l'utilisateur
        editor.apply();                         // Appliquer les changements
    }

    // Méthode pour hasher le mot de passe (SHA-256)
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
