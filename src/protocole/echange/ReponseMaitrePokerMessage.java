package protocole.echange;

import adPoker.Client;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jeuCarte.Carte;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class ReponseMaitrePokerMessage extends PokerMessage {

    private Carte carte;

    public ReponseMaitrePokerMessage(Carte carte) {
        super(TypeMessage.REPONSE_AU_MAITRE);
        this.carte = carte;
    }

    public Carte getCarte() {
        return carte;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            System.out.println("Carte recu de l echange : " + getCarte());
            cli.getJeu().ajoutCarte(getCarte());
            Carte nouvelleCarte = cli.getJeu().nvlleCarte();
            ReponseEchangePokerMessage msgKriss = new ReponseEchangePokerMessage(nouvelleCarte);
            cli.getReso().sendMessage(cli.getNom(), from, msgKriss);
            System.out.println("Carte envoye " + nouvelleCarte);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
