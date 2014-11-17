package protocole.echange;

import adPoker.Client;
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
                cli.getLogger().write("EchangePokerMessage", from, "Carte a echanger : " + cli.getNbCarteAChanger() + " | carte envoyee : " + reponseEchange.getCarte());

            } else {
                cli.getLogger().write("EchangePokerMessage", from, "Passage de jeton a : " + cli.getAdversaireSuivant().getNom());
                if (cli.getNom().compareTo(cli.getMaitre().getNom()) == 0) {
                    cli.setNbTour(cli.getNbTour() + 1);
                    cli.getLogger().write("EchangePokerMessage", from, "Tour : "+cli.getNbTour()+"/"+Client.NB_TOUR_MAX);
                    if (cli.getNbTour() >= Client.NB_TOUR_MAX) {
                        cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new AfficheCartePokerMessage());
                    } else {
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
