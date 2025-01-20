package com.example.libraryland;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;

public class AddBookActivity extends AppCompatActivity {
    private static final String TAG = "AddBookActivity";

    private ImageView uploadImage;
    private Button saveButton;
    private EditText inputTitle, inputAuthor, inputGenre, inputYear, inputDescription;
    private ProgressBar progressBar;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        uploadImage = findViewById(R.id.uploadImage);
        inputTitle = findViewById(R.id.input_title);
        inputAuthor = findViewById(R.id.input_author);
        inputGenre = findViewById(R.id.input_genre);
        inputYear = findViewById(R.id.input_year);
        inputDescription = findViewById(R.id.input_description);
        saveButton = findViewById(R.id.save_button);
        //progressBar = findViewById(R.id.progressBar);

        uploadImage.setOnClickListener(v -> selectImage());
        saveButton.setOnClickListener(v -> validateAndUploadBook());
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
            uploadImage.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAndUploadBook() {
        String title = inputTitle.getText().toString().trim();
        String author = inputAuthor.getText().toString().trim();
        String genre = inputGenre.getText().toString().trim();
        String yearString = inputYear.getText().toString().trim();
        String description = inputDescription.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || yearString.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "All fields and an image are required", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Integer.parseInt(yearString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid year", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToStorage(title, author, genre, yearString, description);
    }

    private void uploadImageToStorage(String title, String author, String genre, String year, String description) {
        //progressBar.setVisibility(View.VISIBLE);

        try {
            File imageFile = getFileFromUri(imageUri);

            // Utilisation de CloudinaryUploader pour uploader l'image
            CloudinaryUploader.uploadImage(imageFile, new CloudinaryUploader.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    // Appeler saveBookToDatabase avec l'URL de l'image Cloudinary
                    saveBookToDatabase(title, author, genre, year, description, imageUrl);
                }

                @Override
                public void onError(String errorMessage) {
                    //progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddBookActivity.this, "Image upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            //progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error preparing file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private void saveBookToDatabase(String title, String author, String genre, String year, String description, String imageUrl) {
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        // Création d'un objet Book
        Book book = new Book(title, author, genre, Integer.parseInt(year), true, currentDate, currentDate, description);
        book.setDataImage(imageUrl);

        // Référence à la table "books"
        DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference("books");
        String key = booksRef.push().getKey(); // Génère une clé unique
        book.setKey(key); // Associe cette clé à l'objet Book

        booksRef.child(key) // Utilise la clé générée comme identifiant
                .setValue(book)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddBookActivity.this, "Book added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(AddBookActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
