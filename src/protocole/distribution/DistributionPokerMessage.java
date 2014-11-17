package protocole.distribution;

import adPoker.Client;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeuCarte.Carte;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class DistributionPokerMessage extends PokerMessage {

    private Carte carte;

    public DistributionPokerMessage(Carte carte) {
        super(TypeMessage.MESSAGE_DISTRIBUTION);
        this.carte = carte;
    }

    public Carte getCarte() {
        return carte;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {

            cli.getCartes().add(getCarte());
            cli.getLogger().write("DistributionPokerMessage", from, "Carte : " + cli.getCartes().size() + "/ 5");
            if (cli.getCartes().size() == 5) {
                if (cli.getNom().compareTo(cli.getMaitre().getNom()) == 0) {
                    cli.getReso().broadcastMessage(cli.getNom(), new FinDistributionPokerMessage());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
