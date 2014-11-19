package protocole.election;

import adPoker.Client;
import adPoker.Joueur;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.distribution.DistributionPokerMessage;

public class ReponseFinElectionPokerMessage extends PokerMessage {

    public ReponseFinElectionPokerMessage() {
        super(TypeMessage.REPONSE_FIN_ELECTION);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            cli.setNbFinElection(cli.getNbFinElection() + 1);
            if (cli.getNbFinElection() == cli.getListJoueurs().size()) {
                //TODO: synchronise
                for (int i = 0; i < 5; i++) {
                    cli.getLogger().write("Distribution", from, "Tour : " + i + " / 5");
                    for (Joueur adve : cli.getListJoueurs()) {
                        DistributionPokerMessage distributionMessage = new DistributionPokerMessage(cli.getJeu().nvlleCarte());
                        cli.getReso().sendMessage(cli.getNom(), adve.getNom(), distributionMessage);
                    }
                }
            }
        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }

}
