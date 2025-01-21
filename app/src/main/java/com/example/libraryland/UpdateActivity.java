package com.example.libraryland;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UpdateActivity extends AppCompatActivity {

    private ImageView updateImage;
    private EditText updateTitle, updateAuthor, updateYear, updateDescription;
    private Spinner updateGenre;
    private Button updateButton;
    private Uri imageUri;
    private String bookKey, oldImageUrl;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize views
        updateImage = findViewById(R.id.update_image);
        updateTitle = findViewById(R.id.update_title);
        updateAuthor = findViewById(R.id.update_author);
        updateYear = findViewById(R.id.update_year);
        updateDescription = findViewById(R.id.update_description);
        updateGenre = findViewById(R.id.update_genre);
        updateButton = findViewById(R.id.update_button);
        if (updateGenre == null) {
            Log.e("UpdateActivity", "Spinner updateGenre est null");
        }

        // Populate spinner with genres
        String[] genres = {"Romance", "Science fiction", "Fantasy", "Horror", "Adventure", "Comedy"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genres);
        if (updateGenre != null) {
            updateGenre.setAdapter(adapter);
        } else {
            Log.e("UpdateActivity", "Le Spinner updateGenre est null !");
        }


        // Retrieve data passed from previous activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setText(bundle.getString("Title"));
            updateAuthor.setText(bundle.getString("Author"));
            updateYear.setText(String.valueOf(bundle.getInt("Year")));// Si l'année est transmise
            Log.d("UpdateActivity", "Year received: " + bundle.getInt("Year"));
            updateDescription.setText(bundle.getString("Description"));
            bookKey = bundle.getString("Key");
            oldImageUrl = bundle.getString("Image");
            if (bookKey == null || bookKey.isEmpty()) {
                Log.e("UpdateActivity", "bookKey est null ou vide !");
                Toast.makeText(this, "Erreur : Impossible de charger les détails du livre.", Toast.LENGTH_SHORT).show();
                finish(); // Ferme l'activité si les données sont invalides
                return;
            }

            // Set selected genre
            String currentGenre = bundle.getString("Genre");
            if (currentGenre != null) {
                int position = adapter.getPosition(currentGenre);
                updateGenre.setSelection(position);
            } else {
                Log.e("UpdateActivity", "Genre non trouvé dans le spinner !");
            }
        }

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("books").child(bookKey);

        // Image update
        updateImage.setOnClickListener(v -> selectImage());

        // Save updates
        updateButton.setOnClickListener(v -> validateAndUpdateBook());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            imageUri = data.getData();
            updateImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAndUpdateBook() {
        String title = updateTitle.getText().toString().trim();
        String author = updateAuthor.getText().toString().trim();
        String genre = updateGenre.getSelectedItem().toString().trim();
        String yearString = updateYear.getText().toString().trim();
        String description = updateDescription.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || yearString.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            Toast.makeText(UpdateActivity.this, "Invalid year", Toast.LENGTH_SHORT).show();
            return;
        }


        if (imageUri != null) {
            try {
                File imageFile = getFileFromUri(imageUri);
                CloudinaryUploader.uploadImage(imageFile, new CloudinaryUploader.UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        updateBookInDatabase(title, author, genre, yearString, description, imageUrl);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(UpdateActivity.this, "Image upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error preparing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            updateBookInDatabase(title, author, genre, yearString, description, oldImageUrl);
        }
    }

    private void updateBookInDatabase(String title, String author, String genre, String year, String description, String imageUrl) {
        Book book = new Book(title, author, genre, Integer.parseInt(year), true, null, null, description);
        book.setDataImage(imageUrl);
        book.setKey(bookKey);

        databaseReference.setValue(book).addOnSuccessListener(aVoid -> {
            // Met à jour la date de modification dans RecentDates
            DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("RecentDates");
            datesRef.child("lastModifyDate").setValue(System.currentTimeMillis())
                    .addOnSuccessListener(updateTask -> Log.d("ModifyBook", "lastModifyDate updated successfully"))
                    .addOnFailureListener(error -> Log.e("ModifyBook", "Failed to update lastModifyDate: " + error.getMessage()));

            Toast.makeText(UpdateActivity.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(UpdateActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private File getFileFromUri(Uri uri) throws Exception {
        File file = new File(getCacheDir(), "temp_image");
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }
}
