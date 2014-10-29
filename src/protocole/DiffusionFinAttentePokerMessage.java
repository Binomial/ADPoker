package protocole;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @class DiffusionConnectionPokerMessage
 * Message envoye des que le timer atteint 1 minute, 
 * pour lancer l'attribution des numeros
 */
public class DiffusionFinAttentePokerMessage extends PokerMessage implements Serializable{

    private List <String> joueursList;
    
    
    
    public DiffusionFinAttentePokerMessage(List<String> joueursList) {
        super(TypeMessage.DIFFUSION_FIN_ATTENTE);
        this.joueursList = joueursList;
    }
    
    public List<String> getJoueursList() {
        return this.joueursList;
    }    
}