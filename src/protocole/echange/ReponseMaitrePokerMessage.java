
package protocole.echange;

import jeuCarte.Carte;
import protocole.PokerMessage;
import protocole.TypeMessage;


public class ReponseMaitrePokerMessage extends PokerMessage{
    
    private Carte carte;

    public ReponseMaitrePokerMessage(Carte carte) {
        super(TypeMessage.REPONSE_AU_MAITRE);
        this.carte = carte;
    }

    public Carte getCarte() {
        return carte;
    }

}
