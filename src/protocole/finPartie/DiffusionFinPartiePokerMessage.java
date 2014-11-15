package protocole.finPartie;

import protocole.PokerMessage;
import protocole.TypeMessage;


public class DiffusionFinPartiePokerMessage extends PokerMessage{

    public DiffusionFinPartiePokerMessage() {
        super(TypeMessage.DIFFUSION_FIN_PARTIE);
    }

}
