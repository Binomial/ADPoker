
package protocole.affiche;

import adPoker.Client;
import jeuCarte.Carte;
import protocole.PokerMessage;
import protocole.TypeMessage;


public class DiffusionAfficheCartePokerMessage extends PokerMessage{

    private Carte carteRemove;
    
    public DiffusionAfficheCartePokerMessage(Carte rmCarte) {
        super(TypeMessage.DIFFUSION_AFFICHE);
        this.carteRemove = rmCarte;
    }
    

    @Override
    public void traitementMessage(Client cli, String from) {
        cli.getLogger().write("DiffusionAfficheCartePokerMessage", from, "Carte : " + carteRemove);
    }

}
