package protocole.finPartie;

import adPoker.Client;
import protocole.PokerMessage;
import protocole.TypeMessage;

public class MessageFinPartiePokerMessage extends PokerMessage {

    public MessageFinPartiePokerMessage() {
        super(TypeMessage.MESSAGE_FIN_PARTIE);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        try {
            cli.getLogger().write("MessageFinPartiePokerMessage", from, "Fin de partie");
            cli.getReso().sendMessage(cli.getNom(), cli.getAdversaireSuivant().getNom(), new MessageFinPartiePokerMessage());
            cli.getReso().removeClient(cli.getNom());
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
