package protocole;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @class DiffusionDebutJeuPokerMessage
 * Message envoye a la fin du premier chrono
 */
public class DiffusionDebutJeuPokerMessage extends PokerMessage implements Serializable{
    
    
    
    private ArrayList <String> joueursList;

    public DiffusionDebutJeuPokerMessage(ArrayList<String> joueursList) {
        super(TypeMessage.DIFFUSION_DEBUT_JEU);
        this.joueursList = joueursList;
    }

    public ArrayList<String> getJoueursList() {
        return joueursList;
    }    
    
}
