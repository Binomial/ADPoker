package protocole.echange;

import adPoker.Client;
import static adPoker.Client.NB_TOUR_MAX;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.affiche.AfficheCartePokerMessage;

public abstract class EchangePokerMessage extends PokerMessage {

    TypeEchange typeEchange;

    public EchangePokerMessage(TypeEchange type) {
        super(TypeMessage.MESSAGE_ECHANGE);
        this.typeEchange = type;
    }

    public TypeEchange getTypeEchange() {
        return typeEchange;
    }

    public void traitement(Client cli, String from) {
        try {
            if (cli.getNbCarteAChanger() > 0) {

                ReponseMaitrePokerMessage reponseEchange = new ReponseMaitrePokerMessage(cli.getCartes().remove(0));
                cli.getReso().sendMessage(cli.getNom(), cli.getMaitre().getNom(), reponseEchange);
                cli.setNbCarteAChanger(cli.getNbCarteAChanger() - 1);
                System.out.println("Carte a echangee : " + cli.getNbCarteAChanger() + "j'envoie : " + reponseEchange.getCarte());

            } else {
                System.out.println("passage de jeton a " + cli.getAdversaireSuivant().getNom());
                if (cli.getNom().compareTo(cli.getMaitre().getNom()) == 0) {
                    cli.setNbTour(cli.getNbTour() + 1);
                    if (cli.getNbTour() >= Client.NB_TOUR_MAX) {
                        System.err.println("Fin des echanges");
                        cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new AfficheCartePokerMessage());
                    } else {
                        System.err.println("FIN DU TOUR" + cli.getNbTour());
                        cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new InitEchangePokerMessage());
                    }

                } else {
                    cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new InitEchangePokerMessage());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
