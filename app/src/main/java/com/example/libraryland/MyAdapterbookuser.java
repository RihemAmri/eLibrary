package com.example.libraryland;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapterbookuser extends RecyclerView.Adapter<MyAdapterbookuser.MyViewHolder> {
    Context context;
    ArrayList<Book> list;

    public MyAdapterbookuser(Context context, ArrayList<Book> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.bookitem, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Book book = list.get(position);
        holder.titre.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.genre.setText(book.getGenre());
        Log.d("TAG", "mizilt lina");
        Uri imageUri = Uri.parse(book.getDataImage());

        Glide.with(holder.itemView.getContext())  // Utilisation de Glide
                .load(imageUri)  // L'URI de l'image
                .placeholder(R.drawable.placeholder_image)  // Image de remplacement
                // Image en cas d'erreur de chargement
                .into(holder.bookImage);

        holder.moreOptionsButton.setOnClickListener(v -> {
            // Redirection vers ViewDetailsActivity avec l'ID du livre
            Intent intent = new Intent(context, ViewDetails.class);
            intent.putExtra("BOOK_ID", book.getTitle()); // Passez l'ID du livre
            intent.putExtra("BOOK_DETAILS", book); // Passez l'ID du livre
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titre, author, genre;
        Button moreOptionsButton;
        ImageView bookImage;

        public MyViewHolder(@NonNull View bookitemView) {
            super(bookitemView);
            titre = bookitemView.findViewById(R.id.title);
            author = bookitemView.findViewById(R.id.author);
            genre = bookitemView.findViewById(R.id.genre);
            bookImage = bookitemView.findViewById(R.id.book_image);
            moreOptionsButton = bookitemView.findViewById(R.id.book_more_options);
        }
    }
}
