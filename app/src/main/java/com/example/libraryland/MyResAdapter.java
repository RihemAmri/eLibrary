package com.example.libraryland;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;
import java.util.Map;

public class MyResAdapter extends RecyclerView.Adapter<MyResAdapter.ViewHolder> {

    private Context context;
    private List<Borrow> borrowList;
    private Map<String, String> bookImageMap; // Map contenant les noms des livres et leurs images

    public MyResAdapter(Context context, List<Borrow> borrowList, Map<String, String> bookImageMap) {
        this.context = context;
        this.borrowList = borrowList;
        this.bookImageMap = bookImageMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerhist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Borrow borrow = borrowList.get(position);

        // Associer les données d'emprunt
        holder.recTitle.setText(borrow.getBookname());
        holder.recDesc.setText("Requested: " + borrow.getRequestDate());
        holder.recLang.setText("Return: " + (borrow.getReturnDate() != null ? borrow.getReturnDate() : "Not returned"));

        // Afficher l'image du livre correspondant
        String imageUrl = bookImageMap.get(borrow.getBookname());
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image) // Image par défaut

                    .into(holder.recImage);
        } else {
            holder.recImage.setImageResource(R.drawable.placeholder_image); // Image par défaut si aucune image
        }
    }

    @Override
    public int getItemCount() {
        return borrowList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView recTitle, recDesc, recLang;
        ShapeableImageView recImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recTitle = itemView.findViewById(R.id.recTitle);
            recDesc = itemView.findViewById(R.id.recDesc);
            recLang = itemView.findViewById(R.id.recLang);
            recImage = itemView.findViewById(R.id.recImage);
        }
    }
}
