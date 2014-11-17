package protocole.echange;

import adPoker.Client;
import jeuCarte.Carte;

public class ReponseEchangePokerMessage extends EchangePokerMessage {

    private final Carte carte;

    public ReponseEchangePokerMessage(Carte carte) {
        super(TypeEchange.ECHANGE);
        this.carte = carte;
    }

    public Carte getCarte() {
        return carte;
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        cli.getCartes().add(getCarte());
        cli.getLogger().write("ReponseEchangePokerMessage", from, "Carte recue : " + getCarte());
        traitement(cli, from);
    }

}
