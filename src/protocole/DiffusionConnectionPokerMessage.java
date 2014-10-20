package protocole;

import java.io.Serializable;

/**
 * 
 * @class DiffusionConnectionPokerMessage
 * Message envoye apres la declaration du client
 */
public class DiffusionConnectionPokerMessage extends PokerMessage{

    private String nom;

    public String getNom() {
        return nom;
    }    

    public DiffusionConnectionPokerMessage(String nom) {
        super(TypeMessage.DIFFUSION_CONNECTION);
        this.nom = nom;
    }
    

}
