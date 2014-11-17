package protocole.distribution;

import adPoker.Client;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.echange.InitEchangePokerMessage;

public class FinDistributionOkPokerMessage extends PokerMessage {

    public FinDistributionOkPokerMessage() {
        super(TypeMessage.MESSAGE_FIN_OK_DISTRIBUTION);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            cli.setNbDistribOk(cli.getNbDistribOk() + 1);
            if (cli.getNbDistribOk() == cli.getListJoueurs().size()) {
                System.out.println("Envoi du message d'init echange");
                cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new InitEchangePokerMessage());
                System.out.println("FIN Envoi du message d'init echange");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
