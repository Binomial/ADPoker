package protocole.distribution;

import adPoker.Client;
import protocole.PokerMessage;
import protocole.TypeMessage;
import protocole.echange.InitEchangePokerMessage;

public class FinDistributionOkPokerMessage extends PokerMessage {

    public FinDistributionOkPokerMessage() {
        super(TypeMessage.MESSAGE_FIN_OK_DISTRIBUTION);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            cli.setNbDistribOk(cli.getNbDistribOk() + 1);
            if (cli.getNbDistribOk() == cli.getListJoueurs().size()) {
                cli.getLogger().write("FinDistributionOkPokerMessage", from, "Maitre : Les cartes sont distribuees");
                cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new InitEchangePokerMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
