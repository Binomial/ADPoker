package protocole.affiche;

import protocole.PokerMessage;
import protocole.TypeMessage;


public class AfficheCartePokerMessage  extends PokerMessage{

    public AfficheCartePokerMessage() {
        super(TypeMessage.MESSAGE_AFFICHE);
    }
    
}
