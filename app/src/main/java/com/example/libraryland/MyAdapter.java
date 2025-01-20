package com.example.libraryland;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private List<Book> dataList = new ArrayList<>();

    public MyAdapter(Context context, List<Book> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Récupère l'URL de l'image depuis Cloudinary
        String imageUrl = dataList.get(position).getDataImage();
        Log.d("ImageURL", "Loading image from URL: " + imageUrl);
        // Utilisation de Glide pour charger l'image depuis l'URL de Cloudinary
        Glide.with(context)
                .load(imageUrl)  // L'URL retournée par Cloudinary
                .into(holder.recImage);

        // Mise à jour des champs avec les données du livre
        holder.recTitle.setText(dataList.get(position).getTitle());
        holder.recDesc.setText(dataList.get(position).getAuthor());
        holder.recLang.setText(dataList.get(position).getGenre());
        // Configuration du clic sur la CardView
        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent pour passer à l'activité DetailActivity
                Intent intent = new Intent(context, DetailActivity.class);

                // Transmission des données du livre via l'Intent
                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDescription()); // Envoi de la description
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("Author", dataList.get(holder.getAdapterPosition()).getAuthor()); // Envoi de l'auteur
                intent.putExtra("Genre", dataList.get(holder.getAdapterPosition()).getGenre()); // Envoi du genre
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
                // Démarrer l'activité
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Méthode pour mettre à jour la liste des données (par exemple, pour une recherche)
    /*public void searchDataList(List<Book> searchList) {
        dataList = searchList;
        notifyDataSetChanged();
    }*/

    // Classe interne MyViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recImage;
        TextView recTitle, recDesc, recLang;
        CardView recCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recImage = itemView.findViewById(R.id.recImage);
            recCard = itemView.findViewById(R.id.recCard);
            recDesc = itemView.findViewById(R.id.recDesc);
            recLang = itemView.findViewById(R.id.recLang);
            recTitle = itemView.findViewById(R.id.recTitle);
        }
    }
}
