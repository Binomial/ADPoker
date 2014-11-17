package protocole.echange;

import adPoker.Client;
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
            cli.getLogger().write("ReponseMaitrePokerMessage", from, "Maitre : Carte recue : " + getCarte());
            cli.getJeu().ajoutCarte(getCarte());
            Carte nouvelleCarte = cli.getJeu().nvlleCarte();
            cli.getReso().sendMessage(cli.getNom(), from, new ReponseEchangePokerMessage(nouvelleCarte));
            cli.getLogger().write("ReponseMaitrePokerMessage", from, "Maitre : Carte envoyee : " + nouvelleCarte);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
