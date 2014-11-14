package protocole.connection;

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

}
