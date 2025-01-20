package com.example.libraryland;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.title.setText(borrow.getBookname()); // Nom du livre
        holder.requestDate.setText("Date de demande : " + borrow.getRequestDate()); // Date de demande
        holder.returnDate.setText("Date de retour : " + borrow.getReturnDate()); // Date de retour

        // Bouton de suppression
        holder.deletebtn.setOnClickListener(v -> {
            deleteBorrowRequest(borrow, position);
        });
    }

    @Override
    public int getItemCount() {
        return borrowList.size();
    }

    private void deleteBorrowRequest(Borrow borrow, int position) {
        // Supprimer l'emprunt de Firebase
        DatabaseReference borrowRef = FirebaseDatabase.getInstance().getReference("borrow");
        borrowRef.orderByChild("bookname").equalTo(borrow.getBookname()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Supprimer l'emprunt
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue().addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Emprunt supprimé avec succès");

                        // Mettre à jour la disponibilité du livre
                        updateBookAvailability(borrow.getBookname());

                        // Mise à jour de l'affichage après suppression
                        borrowList.remove(position);
                        notifyItemRemoved(position);
                    }).addOnFailureListener(e ->
                            Log.e("Firebase", "Erreur lors de la suppression", e)
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors de l'accès à Firebase", error.toException());
            }
        });
    }

    private void updateBookAvailability(String bookname) {
        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("book");
        bookRef.orderByChild("title").equalTo(bookname).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Book book = dataSnapshot.getValue(Book.class);
                    if (book != null) {
                        // Mettre à jour la disponibilité du livre
                        book.availability = true;

                        // Sauvegarder l'objet Book avec la disponibilité mise à jour
                        dataSnapshot.getRef().setValue(book).addOnSuccessListener(aVoid ->
                                Log.d("Firebase", "Disponibilité du livre mise à jour avec succès")
                        ).addOnFailureListener(e ->
                                Log.e("Firebase", "Erreur lors de la mise à jour de la disponibilité", e)
                        );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Erreur lors de l'accès à Firebase", error.toException());
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, requestDate, returnDate;
        Button deletebtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            requestDate = itemView.findViewById(R.id.request_date);
            returnDate = itemView.findViewById(R.id.return_date);
            deletebtn = itemView.findViewById(R.id.delete_button);
        }
    }
}


