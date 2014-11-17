package protocole.distribution;

import adPoker.Client;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;


public class FinDistributionPokerMessage extends PokerMessage{

    public FinDistributionPokerMessage() {
        super(TypeMessage.DIFFUSION_FIN_DISTRIBUTION);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            cli.getReso().sendMessage(cli.getNom(), cli.getMaitre().getNom(), new FinDistributionOkPokerMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
