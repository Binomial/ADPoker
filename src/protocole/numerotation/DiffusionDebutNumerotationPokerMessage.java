package protocole.numerotation;

import adPoker.Adversaire;
import java.io.Serializable;
import java.util.List;
import protocole.PokerMessage;
import protocole.TypeMessage;

/**
 * 
 * @class DiffusionConnectionPokerMessage
 * Message envoye des que le timer atteint 1 minute, 
 * pour lancer l'attribution des numeros
 */
public class DiffusionDebutNumerotationPokerMessage extends PokerMessage implements Serializable{

    private List <Adversaire> joueursList;
    
    public DiffusionDebutNumerotationPokerMessage(List<Adversaire> joueursList) {
        super(TypeMessage.DIFFUSION_NUMEROTATION);
        this.joueursList = joueursList;
    }
    
    public List<Adversaire> getJoueursList() {
        return this.joueursList;
    }    
    
    public int getNbAdversaire() {
        return joueursList.size();
    }
        
}
