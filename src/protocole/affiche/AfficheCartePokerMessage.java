package protocole.affiche;

import adPoker.Client;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeuCarte.Carte;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.finPartie.DiffusionFinPartiePokerMessage;

public class AfficheCartePokerMessage extends PokerMessage {

    public AfficheCartePokerMessage() {
        super(TypeMessage.MESSAGE_AFFICHE);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            if (cli.getNbFinOk() <= cli.getListJoueurs().size() - 1) {                
                if (cli.getCartes().isEmpty()) {
                    System.out.println("Demande de fin de partie");
                    cli.getReso().broadcastMessage(cli.getNom(), new DiffusionFinPartiePokerMessage());
                    cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new AfficheCartePokerMessage());

                } else {
                    Carte carteRemove = cli.getCartes().remove(0);
                    System.out.println(cli.getNom() + ":" + carteRemove);
                    cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new AfficheCartePokerMessage());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
