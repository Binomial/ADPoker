package protocole.connection;

import adPoker.Client;
import adPoker.Joueur;
import java.io.Serializable;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class ReponseConnectionPokerMessage extends PokerMessage implements Serializable {

    private String nom;

    public ReponseConnectionPokerMessage(String nom) {
        super(TypeMessage.REPONSE_CONNECTION);
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        if (!cli.existeJoueur(from)) {
            cli.getListJoueurs().add(new Joueur(from));
            System.out.println(getNom() + " nous a accepte");
        }
    }

}
