package protocole.distribution;

import jeuCarte.Carte;
import protocole.PokerMessage;
import protocole.TypeMessage;


public class DistributionPokerMessage extends PokerMessage{
    
    private Carte carte;
    
    public DistributionPokerMessage(Carte carte) {
        super(TypeMessage.MESSAGE_DISTRIBUTION);
        this.carte = carte;
    }

    public Carte getCarte() {
        return carte;
    }    

}
