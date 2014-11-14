package adPoker;

import java.io.Serializable;

public class Joueur implements Serializable{

    private String nom;
    private int id;

    public Joueur(String nom) {
        this.nom = nom;
        this.id = -1;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
