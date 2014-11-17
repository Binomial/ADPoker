package protocole.echange;

import adPoker.Client;

public class InitEchangePokerMessage extends EchangePokerMessage {

    public InitEchangePokerMessage() {
        super(TypeEchange.INIT);
    }

    @Override
    public void traitementMessage(Client cli, String from) {
        cli.setNbCarteAChanger(Client.alea(2, 5));
        cli.getLogger().write("InitEchangePokerMessage", from, "Carte a echangee : " + cli.getNbCarteAChanger());
        traitement(cli, from);
    }

}
