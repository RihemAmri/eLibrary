package com.example.libraryland;

public class Book {
    String title, author, genre, created_at, updated_at, description,dataImage; // Ajout de description
    int year;
    boolean availability;

    public Book() {
    }

    public Book(String title, String author, String genre, int year, boolean availability, String created_at, String updated_at, String description) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.availability = availability;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.description = description; // Initialisation de la description
    }

    // Getter et Setter pour la description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Autres getters et setters inchangés
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

   public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }
}
