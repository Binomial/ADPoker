package protocole.finPartie;

import adPoker.Client;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class DiffusionFinPartiePokerMessage extends PokerMessage {

    public DiffusionFinPartiePokerMessage() {
        super(TypeMessage.DIFFUSION_FIN_PARTIE);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            cli.setNbFinOk(cli.getNbFinOk() + 1);
            System.out.println(from + ": nb fin ok " + cli.getNbFinOk() + "/" + cli.getListJoueurs().size());
            if (cli.getNbFinOk() == cli.getListJoueurs().size()) {
                cli.getReso().removeClient(cli.getNom());
                //System.exit(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
