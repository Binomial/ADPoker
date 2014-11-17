package protocole.connection;

import adPoker.Client;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;

/**
 *
 * @class EjectionPokerMessage Message envoye si la partie est en cours
 */
public class EjectionPokerMessage extends PokerMessage {

    public EjectionPokerMessage() {
        super(TypeMessage.DIFFUSION_EJECTION);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            Thread.sleep(10000);
            //System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
