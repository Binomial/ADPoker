package protocole.connection;

import protocole.PokerMessage;
import protocole.TypeMessage;

/**
 * 
 * @class EjectionPokerMessage
 * Message envoye si la partie est en cours
 */
public class EjectionPokerMessage extends PokerMessage{

    public EjectionPokerMessage() {
        super(TypeMessage.DIFFUSION_EJECTION);
    }
    

}
