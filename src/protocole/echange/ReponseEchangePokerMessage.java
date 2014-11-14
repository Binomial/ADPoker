package protocole.echange;

import jeuCarte.Carte;


public class ReponseEchangePokerMessage extends EchangePokerMessage{
    
    private final Carte carte;
    
    public ReponseEchangePokerMessage(Carte carte) {
        super(TypeEchange.ECHANGE);
        this.carte = carte;
    }

    public Carte getCarte() {
        return carte;
    }   
    
}
