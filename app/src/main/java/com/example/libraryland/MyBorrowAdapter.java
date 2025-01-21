package com.example.libraryland;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyBorrowAdapter extends RecyclerView.Adapter<MyBorrowAdapter.ViewHolder> {

    Context context;
    ArrayList<Borrow> borrowList;

    public MyBorrowAdapter(Context context, ArrayList<Borrow> borrowList) {
        this.context = context;
        this.borrowList = borrowList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookitemdelete, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Borrow borrow = borrowList.get(position);

        // Associer les champs de la classe Borrow aux vues de l'interface
        holder.title.setText(borrow.getBookname());
        holder.requestDate.setText("Date de demande : " + borrow.getRequestDate());
        holder.returnDate.setText("Date de retour : " + borrow.getReturnDate());

        // Récupérer le nom du livre pour effectuer une recherche
        String bookname = borrow.getBookname();

        // Référence à la base de données des livres
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books");

        // Chercher le livre par son nom
        bookRef.orderByChild("title").equalTo(bookname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Parcourir les livres récupérés
                for (DataSnapshot bookSnapshot : dataSnapshot.getChildren()) {
                    Book book = bookSnapshot.getValue(Book.class);

                    if (book != null) {
                        // Afficher l'image du livre si elle existe
                        String imageUrl = book.getDataImage();  // Récupérer l'URL de l'image du livre
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(holder.itemView.getContext())  // Utilisation de Glide
                                    .load(imageUrl)  // L'URL de l'image
                                    .placeholder(R.drawable.placeholder_image)  // Image de remplacement
                                     // Image en cas d'erreur
                                    .into(holder.bookImage);  // Affichage dans l'ImageView
                        } else {
                            holder.bookImage.setImageResource(R.drawable.placeholder_image);  // Affichage d'une image par défaut
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors de la récupération du livre", error.toException());
            }
        });
        // Bouton de suppression
        holder.deletebtn.setOnClickListener(v -> {
            deleteBorrowRequest(borrow, holder.getAdapterPosition()); // Utiliser getAdapterPosition
        });
    }


    @Override
    public int getItemCount() {
        return borrowList.size();
    }

    private void deleteBorrowRequest(Borrow borrow, int position) {
        // Référence à la base de données des emprunts
        DatabaseReference borrowRef = FirebaseDatabase.getInstance().getReference("borrow");

        borrowRef.orderByChild("bookname").equalTo(borrow.getBookname())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Borrow fetchedBorrow = dataSnapshot.getValue(Borrow.class);

                            if (fetchedBorrow != null && fetchedBorrow.getUsername().equals(borrow.getUsername())) {
                                // Supprimer l'emprunt
                                dataSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Firebase", "Emprunt supprimé avec succès : " + borrow.getBookname());

                                            // Mettre à jour la disponibilité du livre
                                            updateBookAvailability(borrow.getBookname());

                                            // Mise à jour de l'adaptateur après suppression
                                            borrowList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, borrowList.size()); // Mettre à jour les positions restantes
                                        })
                                        .addOnFailureListener(e -> Log.e("Firebase", "Erreur lors de la suppression", e));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Erreur lors de la récupération des emprunts", error.toException());
                    }
                });
    }


    private void updateBookAvailability(String bookname) {
        // Référence à la base de données des livres
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books");

        bookRef.orderByChild("title").equalTo(bookname)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Book book = dataSnapshot.getValue(Book.class);

                            if (book != null) {
                                book.setAvailability(true); // Marquer le livre comme disponible

                                // Sauvegarder les modifications
                                dataSnapshot.getRef().setValue(book)
                                        .addOnSuccessListener(aVoid -> Log.d("Firebase", "Disponibilité mise à jour pour : " + bookname))
                                        .addOnFailureListener(e -> Log.e("Firebase", "Erreur lors de la mise à jour de la disponibilité", e));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Erreur lors de la récupération des livres", error.toException());
                    }
                });
    }




    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, requestDate, returnDate;
        Button deletebtn;
        ImageView bookImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            requestDate = itemView.findViewById(R.id.request_date);
            returnDate = itemView.findViewById(R.id.return_date);
            deletebtn = itemView.findViewById(R.id.delete_button);
            bookImage = itemView.findViewById(R.id.book_image);
        }
    }
}


