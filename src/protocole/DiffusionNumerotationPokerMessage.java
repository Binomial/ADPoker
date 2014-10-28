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
    private int id;
    
    public DiffusionNumerotationPokerMessage(List<String> joueursList) {
        super(TypeMessage.DIFFUSION_ELECTION);
        this.joueursList = joueursList;
    }
    
    public DiffusionNumerotationPokerMessage(int id) {
        super(TypeMessage.DIFFUSION_NUM);
        this.id = id;
    }
    
    public List<String> getJoueursList() {
        return this.joueursList;
    }    
    
    public int getId() {
        return id;
    }
        
}
