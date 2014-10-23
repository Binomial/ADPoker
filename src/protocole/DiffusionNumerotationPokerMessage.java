package protocole;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @class DiffusionConnectionPokerMessage
 * Message envoye des que le timer atteint 1 minute, 
 * pour lancer l'attribution des numeros
 */
public class DiffusionNumerotationPokerMessage extends PokerMessage implements Serializable{

    private List <String> joueursList;
    
    public DiffusionNumerotationPokerMessage(List<String> joueursList) {
        super(TypeMessage.DIFFUSION_NUMEROTATION);
        this.joueursList = joueursList;
    }
    
    public List<String> getJoueursList() {
        return this.joueursList;
    }    
    
    public int getNbAdversaire() {
        return joueursList.size();
    }
        
}
