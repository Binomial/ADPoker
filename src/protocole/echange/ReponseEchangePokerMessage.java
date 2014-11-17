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
        System.out.println("ECHANGE");
        cli.getCartes().add(getCarte());
        System.out.println("Nouvelle carte : " + getCarte());
        traitement(cli, from);
    }

}
