package com.example.libraryland;

/**
 * Classe Borrow représente un emprunt dans la bibliothèque.
 */
public class Borrow {
    private String username;    // Identifiant de l'utilisateur (clé primaire dans la table Users)
    private String bookname;      // Identifiant unique généré automatiquement pour le livre
    private String status;      // Statut de l'emprunt (ex : "pending", "returned")
    private String requestDate; // Date et heure de la demande
    private String returnDate;  // Date et heure de retour (null si pas encore retourné)

    /**
     * Constructeur par défaut requis par Firebase.
     */
    public Borrow() {
    }

    /**
     * Constructeur avec tous les paramètres.
     *
     * @param username    Identifiant de l'utilisateur (clé primaire du user).
     * @param bookname      Identifiant du livre (généré automatiquement par Firebase).
     * @param status      Statut de l'emprunt.
     * @param requestDate Date et heure de la demande.
     * @param returnDate  Date et heure de retour (peut être null).
     */
    public Borrow(String username, String bookname, String status, String requestDate, String returnDate) {
        this.username = username;
        this.bookname = bookname;
        this.status = status;
        this.requestDate = requestDate;
        this.returnDate = returnDate;
    }

    // Getters et Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
