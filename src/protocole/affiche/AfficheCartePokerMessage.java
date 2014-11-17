package protocole.affiche;

import adPoker.Client;
import jeuCarte.Carte;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.finPartie.MessageFinPartiePokerMessage;

public class AfficheCartePokerMessage extends PokerMessage {

    public AfficheCartePokerMessage() {
        super(TypeMessage.MESSAGE_AFFICHE);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
//            if (cli.getNbFinOk() <= cli.getListJoueurs().size() - 1) {  
//                cli.getLogger().write("AfficheCartePokerMessage", from, "Plus de carte");
//                if (cli.getCartes().isEmpty()) {
//                    cli.getReso().broadcastMessage(cli.getNom(), new DiffusionFinPartiePokerMessage());
//                    cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new AfficheCartePokerMessage());
//                } else {
//                    Carte carteRemove = cli.getCartes().remove(0);
//                    cli.getLogger().write("AfficheCartePokerMessage", from, "Envoie de la diffusion Affiche Carte");
//                    cli.getReso().broadcastMessage(from, new DiffusionAfficheCartePokerMessage(carteRemove));
//                    cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new AfficheCartePokerMessage());
//                }
//            }               

            Carte carteRemove = cli.getCartes().remove(0);
            cli.getLogger().write("AfficheCartePokerMessage", from, "Envoie de la diffusion Affiche Carte");
            cli.getReso().broadcastMessage(from, new DiffusionAfficheCartePokerMessage(carteRemove));
            if (cli.getCartes().isEmpty() && cli.getNom().compareTo(cli.getMaitre().getNom()) == 0) {
                cli.getLogger().write("AfficheCartePokerMessage", from, "Plus de carte");
                cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new MessageFinPartiePokerMessage());
            } else {
                cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new AfficheCartePokerMessage());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
