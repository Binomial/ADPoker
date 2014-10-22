package protocole;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @class DiffusionConnectionPokerMessage
 * Message envoye des que le timer atteint 1 minute, 
 * pour lancer l'attribution des numeros
 */
public class DiffusionElectionPokerMessage extends PokerMessage implements Serializable{

    private ArrayList <String> joueursList;
    
    public DiffusionElectionPokerMessage(ArrayList<String> joueursList) {
        super(TypeMessage.DIFFUSION_ELECTION);
        this.joueursList = joueursList;
    }
    
        public ArrayList<String> getJoueursList() {
        return joueursList;
    }    
        
}
