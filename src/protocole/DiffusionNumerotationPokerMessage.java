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

    private int id;
    private int nbTour;
   
    
    public DiffusionNumerotationPokerMessage(int id, int nbTour) {
        super(TypeMessage.DIFFUSION_NUM);
        this.id = id;
        this.nbTour = nbTour;
    }
    
    public int getId() {
        return id;
    }
    
    public int getNumTour() {
        return nbTour;
    }
}
